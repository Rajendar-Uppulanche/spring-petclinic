package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.util.EntityUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(OwnerRepository.class))
class VisitRepositoryTests {

    @Autowired
    VisitRepository visits;

    @Autowired
    OwnerRepository owners;

    @Autowired
    PetRepository pets;

    private Owner owner1;
    private Pet pet1;
    private Pet pet2;

    @BeforeEach
    void setup() {
        owner1 = owners.findById(1); // Owner with ID 1 (George Franklin)
        pet1 = pets.findById(7); // Pet with ID 7 (Samantha) - owned by George Franklin
        pet2 = pets.findById(8); // Pet with ID 8 (Max) - owned by George Franklin

        // Ensure pet1 has visits
        Visit visit1 = new Visit();
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup for Samantha");
        pet1.addVisit(visit1);
        visits.save(visit1);

        Visit visit2 = new Visit();
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination for Samantha");
        pet1.addVisit(visit2);
        visits.save(visit2);

        // Ensure pet2 has visits
        Visit visit3 = new Visit();
        visit3.setDate(LocalDate.of(2023, 3, 10));
        visit3.setDescription("Dental cleaning for Max");
        pet2.addVisit(visit3);
        visits.save(visit3);

        Visit visit4 = new Visit();
        visit4.setDate(LocalDate.of(2023, 4, 5));
        visit4.setDescription("Follow-up for Max's dental");
        pet2.addVisit(visit4);
        visits.save(visit4);
    }

    @Test
    void shouldFindVisitsByOwnerIdAndNoFilters() {
        List<Visit> filteredVisits = visits.findByOwnerIdAndFilters(owner1.getId(), null, null, null);
        assertThat(filteredVisits).hasSize(4); // All 4 visits for owner1's pets
        assertThat(filteredVisits).extracting(Visit::getDescription)
            .containsExactlyInAnyOrder("Routine checkup for Samantha", "Vaccination for Samantha", "Dental cleaning for Max", "Follow-up for Max's dental");
    }

    @Test
    void shouldFindVisitsByOwnerIdAndFromDate() {
        LocalDate fromDate = LocalDate.of(2023, 3, 1);
        List<Visit> filteredVisits = visits.findByOwnerIdAndFilters(owner1.getId(), fromDate, null, null);
        assertThat(filteredVisits).hasSize(2);
        assertThat(filteredVisits).extracting(Visit::getDescription)
            .containsExactlyInAnyOrder("Dental cleaning for Max", "Follow-up for Max's dental");
    }

    @Test
    void shouldFindVisitsByOwnerIdAndToDate() {
        LocalDate toDate = LocalDate.of(2023, 2, 28);
        List<Visit> filteredVisits = visits.findByOwnerIdAndFilters(owner1.getId(), null, toDate, null);
        assertThat(filteredVisits).hasSize(2);
        assertThat(filteredVisits).extracting(Visit::getDescription)
            .containsExactlyInAnyOrder("Routine checkup for Samantha", "Vaccination for Samantha");
    }

    @Test
    void shouldFindVisitsByOwnerIdAndDateRange() {
        LocalDate fromDate = LocalDate.of(2023, 2, 1);
        LocalDate toDate = LocalDate.of(2023, 3, 31);
        List<Visit> filteredVisits = visits.findByOwnerIdAndFilters(owner1.getId(), fromDate, toDate, null);
        assertThat(filteredVisits).hasSize(2);
        assertThat(filteredVisits).extracting(Visit::getDescription)
            .containsExactlyInAnyOrder("Vaccination for Samantha", "Dental cleaning for Max");
    }

    @Test
    void shouldFindVisitsByOwnerIdAndKeyword() {
        List<Visit> filteredVisits = visits.findByOwnerIdAndFilters(owner1.getId(), null, null, "dental");
        assertThat(filteredVisits).hasSize(2);
        assertThat(filteredVisits).extracting(Visit::getDescription)
            .containsExactlyInAnyOrder("Dental cleaning for Max", "Follow-up for Max's dental");
    }

    @Test
    void shouldFindVisitsByOwnerIdAndKeywordCaseInsensitive() {
        List<Visit> filteredVisits = visits.findByOwnerIdAndFilters(owner1.getId(), null, null, "SAMANTHA");
        assertThat(filteredVisits).hasSize(2);
        assertThat(filteredVisits).extracting(Visit::getDescription)
            .containsExactlyInAnyOrder("Routine checkup for Samantha", "Vaccination for Samantha");
    }

    @Test
    void shouldFindVisitsByOwnerIdAndAllFilters() {
        LocalDate fromDate = LocalDate.of(2023, 2, 1);
        LocalDate toDate = LocalDate.of(2023, 3, 31);
        List<Visit> filteredVisits = visits.findByOwnerIdAndFilters(owner1.getId(), fromDate, toDate, "cleaning");
        assertThat(filteredVisits).hasSize(1);
        assertThat(filteredVisits).extracting(Visit::getDescription)
            .containsExactly("Dental cleaning for Max");
    }

    @Test
    void shouldReturnEmptyListIfNoVisitsMatch() {
        LocalDate fromDate = LocalDate.of(2024, 1, 1); // Future date
        List<Visit> filteredVisits = visits.findByOwnerIdAndFilters(owner1.getId(), fromDate, null, null);
        assertThat(filteredVisits).isEmpty();
    }

    @Test
    void shouldReturnEmptyListIfOwnerHasNoVisits() {
        Owner owner2 = new Owner();
        owner2.setId(99); // Assuming owner 99 does not exist or has no visits
        // Mock findById for owner2 if it's not in the default data
        // given(owners.findById(99)).willReturn(owner2);
        List<Visit> filteredVisits = visits.findByOwnerIdAndFilters(owner2.getId(), null, null, null);
        assertThat(filteredVisits).isEmpty();
    }
}
