package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.samples.petclinic.pet.PetType;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.Specialty;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VisitRepositoryTests {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Owner owner1;
    private Pet pet1;
    private Vet vet1;
    private Visit visit1;
    private Visit visit2;

    @BeforeEach
    void setUp() {
        // Setup Owner
        owner1 = new Owner();
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");
        owner1.setAddress("110 W. Liberty St.");
        owner1.setCity("Madison");
        owner1.setTelephone("6085551023");
        entityManager.persist(owner1);

        // Setup PetType
        PetType cat = new PetType();
        cat.setName("cat");
        entityManager.persist(cat);

        // Setup Pet
        pet1 = new Pet();
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2000, 9, 7));
        pet1.setType(cat);
        pet1.setOwner(owner1);
        entityManager.persist(pet1);

        // Setup Vet
        vet1 = new Vet();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        Specialty radiology = new Specialty();
        radiology.setName("radiology");
        entityManager.persist(radiology);
        vet1.addSpecialty(radiology);
        entityManager.persist(vet1);

        // Setup Visits
        visit1 = new Visit();
        visit1.setDate(LocalDate.now().plusDays(5));
        visit1.setDescription("Routine checkup");
        visit1.setVeterinarian(vet1);
        visit1.setStatus(VisitStatus.SCHEDULED);
        visit1.setPet(pet1);
        entityManager.persist(visit1);

        visit2 = new Visit();
        visit2.setDate(LocalDate.now().plusDays(10));
        visit2.setDescription("Vaccination");
        visit2.setVeterinarian(vet1);
        visit2.setStatus(VisitStatus.SCHEDULED);
        visit2.setPet(pet1);
        entityManager.persist(visit2);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void findByPetOwnerId() {
        List<Visit> visits = visitRepository.findByPetOwnerId(owner1.getId());
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    void findByPetId() {
        List<Visit> visits = visitRepository.findByPetId(pet1.getId());
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    void findByVeterinarianId() {
        List<Visit> visits = visitRepository.findByVeterinarianId(vet1.getId());
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    void findByDateBetween() {
        List<Visit> visits = visitRepository.findByDateBetween(LocalDate.now().plusDays(4), LocalDate.now().plusDays(6));
        assertThat(visits).hasSize(1);
        assertThat(visits).containsExactly(visit1);
    }

    @Test
    void findByStatus() {
        List<Visit> visits = visitRepository.findByStatus(VisitStatus.SCHEDULED);
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    void findVisitsWithMultipleCriteria() {
        VisitService visitService = new VisitService(visitRepository);
        List<Visit> visits = visitService.findVisits(owner1.getId(), pet1.getId(), vet1.getId(), LocalDate.now().plusDays(4), LocalDate.now().plusDays(11), VisitStatus.SCHEDULED);
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);

        visits = visitService.findVisits(owner1.getId(), pet1.getId(), vet1.getId(), LocalDate.now().plusDays(4), LocalDate.now().plusDays(6), VisitStatus.SCHEDULED);
        assertThat(visits).hasSize(1);
        assertThat(visits).containsExactly(visit1);

        visits = visitService.findVisits(owner1.getId(), null, null, null, null, VisitStatus.COMPLETED);
        assertThat(visits).isEmpty();
    }
}
