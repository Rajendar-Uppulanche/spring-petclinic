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
        owner1 = new Owner();
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");
        owner1.setAddress("110 W. Liberty St.");
        owner1.setCity("Madison");
        owner1.setTelephone("6085551023");
        entityManager.persist(owner1);

        owner2 = new Owner();
        owner2.setFirstName("Betty");
        owner2.setLastName("Davis");
        owner2.setAddress("638 Cardinal Ave.");
        owner2.setCity("Sun Prairie");
        owner2.setTelephone("6085551749");
        entityManager.persist(owner2);

        PetType cat = new PetType();
        cat.setName("cat");
        entityManager.persist(cat);

        PetType dog = new PetType();
        dog.setName("dog");
        entityManager.persist(dog);

        pet1 = new Pet();
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2010, 9, 7));
        pet1.setType(cat);
        pet1.setOwner(owner1);
        entityManager.persist(pet1);

        pet2 = new Pet();
        pet2.setName("Basil");
        pet2.setBirthDate(LocalDate.of(2012, 9, 6));
        pet2.setType(dog);
        pet2.setOwner(owner2);
        entityManager.persist(pet2);

        vet1 = new Veterinarian();
        vet1.setName("Dr. James Carter");
        vet1.setContactInformation("james@example.com");
        entityManager.persist(vet1);

        vet2 = new Veterinarian();
        vet2.setName("Dr. Helen Leary");
        vet2.setContactInformation("helen@example.com");
        entityManager.persist(vet2);

        Visit visit1 = new Visit();
        visit1.setVisitDate(LocalDate.of(2013, 1, 1));
        visit1.setDescription("routine checkup");
        visit1.setPet(pet1);
        visit1.setVeterinarian(vet1);
        entityManager.persist(visit1);

        Visit visit2 = new Visit();
        visit2.setVisitDate(LocalDate.of(2013, 1, 2));
        visit2.setDescription("vaccination");
        visit2.setPet(pet2);
        visit2.setVeterinarian(vet2);
        entityManager.persist(visit2);

        Visit visit3 = new Visit();
        visit3.setVisitDate(LocalDate.of(2013, 2, 1));
        visit3.setDescription("dental cleaning");
        visit3.setPet(pet1);
        visit3.setVeterinarian(vet1);
        entityManager.persist(visit3);

        Visit visit4 = new Visit();
        visit4.setVisitDate(LocalDate.of(2014, 3, 1));
        visit4.setDescription("annual checkup");
        visit4.setPet(pet2);
        visit4.setVeterinarian(vet1);
        entityManager.persist(visit4);

        entityManager.flush();
    }

    @Test
    void shouldFindVisitsByPetId() {
        List<Visit> visits = visitRepository.findByPetId(pet1.getId());
        assertThat(visits).hasSize(2);
        assertThat(visits.get(0).getPet().getId()).isEqualTo(pet1.getId());
    }

    @Test
    void shouldFindAllVisitsWhenNoFilters() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, null, null, null);
        assertThat(visits).hasSize(4);
    }

    @Test
    void shouldFilterByPetId() {
        List<Visit> visits = visitRepository.findFilteredVisits(pet1.getId(), null, null, null, null, null);
        assertThat(visits).hasSize(2);
        assertThat(visits.get(0).getPet().getId()).isEqualTo(pet1.getId());
    }

    @Test
    void shouldFilterByOwnerId() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, owner1.getId(), null, null, null, null);
        assertThat(visits).hasSize(2);
        assertThat(visits.get(0).getPet().getOwner().getId()).isEqualTo(owner1.getId());
    }

    @Test
    void shouldFilterByVeterinarianId() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, vet2.getId(), null, null, null);
        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getVeterinarian().getId()).isEqualTo(vet2.getId());
    }

    @Test
    void shouldFilterByStartDate() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, LocalDate.of(2013, 2, 1), null, null);
        assertThat(visits).hasSize(2);
        assertThat(visits.get(0).getVisitDate()).isAfterOrEqualTo(LocalDate.of(2013, 2, 1));
    }

    @Test
    void shouldFilterByEndDate() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, null, LocalDate.of(2013, 1, 15), null);
        assertThat(visits).hasSize(2);
        assertThat(visits.get(0).getVisitDate()).isBeforeOrEqualTo(LocalDate.of(2013, 1, 15));
    }

    @Test
    void shouldFilterByDescriptionKeyword() {
        List<Visit> visits = visitRepository.findFilteredVisits(null, null, null, null, null, "checkup");
        assertThat(visits).hasSize(2);
        assertThat(visits.get(0).getDescription()).containsIgnoringCase("checkup");
    }

    @Test
    void shouldFilterByMultipleCriteria() {
        List<Visit> visits = visitRepository.findFilteredVisits(
                pet1.getId(), owner1.getId(), vet1.getId(), LocalDate.of(2013, 1, 1), LocalDate.of(2013, 1, 31), "checkup");
        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getId()).isEqualTo(entityManager.find(Visit.class, 1).getId());
    }
}