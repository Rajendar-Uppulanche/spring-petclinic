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
class VisitRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VisitRepository visitRepository;

    private Owner owner1;
    private Pet pet1;
    private Veterinarian vet1;
    private Veterinarian vet2;

    @BeforeEach
    void setUp() {
        PetType cat = new PetType();
        cat.setName("cat");
        entityManager.persist(cat);

        owner1 = new Owner();
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");
        owner1.setAddress("110 W. Liberty St.");
        owner1.setCity("Madison");
        owner1.setTelephone("6085551023");
        entityManager.persist(owner1);

        pet1 = new Pet();
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2010, 9, 7));
        pet1.setType(cat);
        owner1.addPet(pet1);
        entityManager.persist(pet1);

        vet1 = new Veterinarian("James Carter", "james@example.com");
        entityManager.persist(vet1);

        vet2 = new Veterinarian("Helen Leary", "helen@example.com");
        entityManager.persist(vet2);

        Visit visit1 = new Visit();
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);
        visit1.setVeterinarian(vet1);
        entityManager.persist(visit1);

        Visit visit2 = new Visit();
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);
        visit2.setVeterinarian(vet2);
        entityManager.persist(visit2);

        Visit visit3 = new Visit();
        visit3.setDate(LocalDate.of(2023, 3, 10));
        visit3.setDescription("Dental cleaning");
        visit3.setPet(pet1);
        visit3.setVeterinarian(vet1);
        entityManager.persist(visit3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindFilteredVisits_noFilters() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, null, null, null);
        assertThat(visits).hasSize(3);
    }

    @Test
    void testFindFilteredVisits_byPetId() {
        List<Visit> visits = visitRepository.findFilteredVisits(pet1.getId(), null, null, null, null, null);
        assertThat(visits).hasSize(3);
        assertThat(visits).allMatch(v -> v.getPet().getId().equals(pet1.getId()));
    }

    @Test
    void testFindFilteredVisits_byOwnerId() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, owner1.getId(), null, null, null, null);
        assertThat(visits).hasSize(3);
        assertThat(visits).allMatch(v -> v.getPet().getOwner().getId().equals(owner1.getId()));
    }

    @Test
    void testFindFilteredVisits_byVeterinarianId() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, vet1.getId(), null, null, null);
        assertThat(visits).hasSize(2);
        assertThat(visits).allMatch(v -> v.getVeterinarian().getId().equals(vet1.getId()));
    }

    @Test
    void testFindFilteredVisits_byDateRange() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 28), null);
        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getDescription()).isEqualTo("Vaccination");
    }

    @Test
    void testFindFilteredVisits_byDescriptionKeyword() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, null, null, "checkup");
        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getDescription()).isEqualTo("Routine checkup");
    }

    @Test
    void testFindFilteredVisits_multipleFilters() {
        List<Visit> visits = visitRepository.findFilteredVisits(pet1.getId(), owner1.getId(), vet1.getId(), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 2, 1), "checkup");
        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getDescription()).isEqualTo("Routine checkup");
        assertThat(visits.get(0).getVeterinarian().getId()).isEqualTo(vet1.getId());
    }

    @Test
    void testFindFilteredVisits_noMatch() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31), null);
        assertThat(visits).isEmpty();
    }
}