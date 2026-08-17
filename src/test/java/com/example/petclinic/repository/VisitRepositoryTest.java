package com.example.petclinic.repository;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PetType;
import com.example.petclinic.model.Veterinarian;
import com.example.petclinic.model.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VisitRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VisitRepository visitRepository;

    private Pet pet1;
    private Pet pet2;
    private Owner owner1;
    private Owner owner2;
    private Veterinarian vet1;
    private Veterinarian vet2;

    @BeforeEach
    void setUp() {
        // Clear existing data to ensure test isolation
        visitRepository.deleteAll();
        entityManager.clear(); // Clear persistence context

        // Setup PetType
        PetType cat = new PetType();
        cat.setName("cat");
        entityManager.persist(cat);

        PetType dog = new PetType();
        dog.setName("dog");
        entityManager.persist(dog);

        // Setup Owners
        owner1 = new Owner();
        owner1.setFirstName("John");
        owner1.setLastName("Doe");
        owner1.setAddress("123 Main St");
        owner1.setCity("Anytown");
        owner1.setTelephone("555-1234");
        entityManager.persist(owner1);

        owner2 = new Owner();
        owner2.setFirstName("Jane");
        owner2.setLastName("Smith");
        owner2.setAddress("456 Oak Ave");
        owner2.setCity("Otherville");
        owner2.setTelephone("555-5678");
        entityManager.persist(owner2);

        // Setup Pets
        pet1 = new Pet();
        pet1.setName("Whiskers");
        pet1.setBirthDate(LocalDate.of(2020, 1, 1));
        pet1.setType(cat);
        pet1.setOwner(owner1);
        entityManager.persist(pet1);

        pet2 = new Pet();
        pet2.setName("Buddy");
        pet2.setBirthDate(LocalDate.of(2019, 5, 10));
        pet2.setType(dog);
        pet2.setOwner(owner2);
        entityManager.persist(pet2);

        // Setup Veterinarians
        vet1 = new Veterinarian();
        vet1.setName("Dr. John Doe");
        vet1.setContactInformation("john.doe@example.com");
        entityManager.persist(vet1);

        vet2 = new Veterinarian();
        vet2.setName("Dr. Jane Smith");
        vet2.setContactInformation("jane.smith@example.com");
        entityManager.persist(vet2);

        // Setup Visits
        Visit visit1 = new Visit();
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Annual checkup");
        visit1.setPet(pet1);
        visit1.setVeterinarian(vet1);
        entityManager.persist(visit1);

        Visit visit2 = new Visit();
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet2);
        visit2.setVeterinarian(vet2);
        entityManager.persist(visit2);

        Visit visit3 = new Visit();
        visit3.setDate(LocalDate.of(2023, 3, 10));
        visit3.setDescription("Dental cleaning");
        visit3.setPet(pet1);
        visit3.setVeterinarian(vet1);
        entityManager.persist(visit3);

        Visit visit4 = new Visit();
        visit4.setDate(LocalDate.of(2023, 4, 5));
        visit4.setDescription("Follow-up check");
        visit4.setPet(pet2);
        visit4.setVeterinarian(vet2);
        entityManager.persist(visit4);

        entityManager.flush();
    }

    @Test
    void testFindFilteredVisits_noFilters() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, null, null, null);
        assertThat(visits).hasSize(4);
    }

    @Test
    void testFindFilteredVisits_byPetId() {
        List<Visit> visits = visitRepository.findFilteredVisits(pet1.getId(), null, null, null, null, null);
        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(visit -> visit.getPet().getId()).containsOnly(pet1.getId());
    }

    @Test
    void testFindFilteredVisits_byOwnerId() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, owner1.getId(), null, null, null, null);
        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(visit -> visit.getPet().getOwner().getId()).containsOnly(owner1.getId());
    }

    @Test
    void testFindFilteredVisits_byVeterinarianId() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, vet1.getId(), null, null, null);
        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(visit -> visit.getVeterinarian().getId()).containsOnly(vet1.getId());
    }

    @Test
    void testFindFilteredVisits_byStartDate() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, LocalDate.of(2023, 3, 1), null, null);
        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDate).contains(LocalDate.of(2023, 3, 10), LocalDate.of(2023, 4, 5));
    }

    @Test
    void testFindFilteredVisits_byEndDate() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, null, LocalDate.of(2023, 2, 28), null);
        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDate).contains(LocalDate.of(2023, 1, 15), LocalDate.of(2023, 2, 20));
    }

    @Test
    void testFindFilteredVisits_byDateRange() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, LocalDate.of(2023, 2, 1), LocalDate.of(2023, 3, 31), null);
        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDate).contains(LocalDate.of(2023, 2, 20), LocalDate.of(2023, 3, 10));
    }

    @Test
    void testFindFilteredVisits_byDescriptionLike() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, null, null, "checkup");
        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDescription).contains("Annual checkup", "Follow-up check");
    }

    @Test
    void testFindFilteredVisits_multipleFilters() {
        List<Visit> visits = visitRepository.findFilteredVisits(pet1.getId(), owner1.getId(), vet1.getId(), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 3, 31), "checkup");
        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getDescription()).isEqualTo("Annual checkup");
        assertThat(visits.get(0).getPet().getId()).isEqualTo(pet1.getId());
        assertThat(visits.get(0).getVeterinarian().getId()).isEqualTo(vet1.getId());
    }

    @Test
    void testFindFilteredVisits_noMatch() {
        List<Visit> visits = visitRepository.findFilteredVisits(pet1.getId(), null, vet2.getId(), null, null, null);
        assertThat(visits).isEmpty();
    }
}