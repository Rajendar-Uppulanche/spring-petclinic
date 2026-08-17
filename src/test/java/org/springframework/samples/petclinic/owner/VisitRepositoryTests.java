package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VisitRepositoryTests {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private OwnerRepository ownerRepository; // To save owners and pets
    
    @Autowired
    private VetRepository vetRepository; // To save vets

    private Owner owner1;
    private Pet pet1;
    private Vet vet1;
    private Visit visit1;
    private Visit visit2;

    @BeforeEach
    void setup() {
        // Setup Owner
        owner1 = new Owner();
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");
        owner1.setAddress("110 W. Liberty St.");
        owner1.setCity("Madison");
        owner1.setTelephone("6085551023");
        ownerRepository.save(owner1);

        // Setup PetType (assuming it exists)
        PetType cat = new PetType();
        cat.setName("cat");
        // In a real scenario, PetType would be managed by its own repository.
        // For this test, we'll just create an instance.

        // Setup Pet
        pet1 = new Pet();
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2000, 1, 1));
        pet1.setType(cat);
        owner1.addPet(pet1); // This will set pet's owner and save pet via cascade if configured.
        ownerRepository.save(owner1); // Save owner again to ensure pet is persisted

        // Setup Vet
        vet1 = new Vet();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vetRepository.save(vet1);

        // Setup Visits
        visit1 = new Visit();
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);
        visit1.setVet(vet1);
        visitRepository.save(visit1);

        visit2 = new Visit();
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);
        visit2.setVet(vet1);
        visitRepository.save(visit2);
    }

    @Test
    void testFindByPetId() {
        List<Visit> visits = visitRepository.findByPetId(pet1.getId());
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    void testFindAllWithSpecification_byPetId() {
        Specification<Visit> spec = (root, query, cb) -> cb.equal(root.get("pet").get("id"), pet1.getId());
        List<Visit> visits = visitRepository.findAll(spec, PageRequest.of(0, 10)).getContent();
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    void testFindAllWithSpecification_byVetId() {
        Specification<Visit> spec = (root, query, cb) -> cb.equal(root.get("vet").get("id"), vet1.getId());
        List<Visit> visits = visitRepository.findAll(spec, PageRequest.of(0, 10)).getContent();
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    void testFindAllWithSpecification_byDateRange() {
        Specification<Visit> spec = (root, query, cb) -> cb.between(root.get("date"), LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31));
        List<Visit> visits = visitRepository.findAll(spec, PageRequest.of(0, 10)).getContent();
        assertThat(visits).hasSize(1);
        assertThat(visits).containsExactly(visit1);
    }

    @Test
    void testFindAllWithSpecification_byDescriptionKeyword() {
        Specification<Visit> spec = (root, query, cb) -> cb.like(cb.lower(root.get("description")), "%checkup%");
        List<Visit> visits = visitRepository.findAll(spec, PageRequest.of(0, 10)).getContent();
        assertThat(visits).hasSize(1);
        assertThat(visits).containsExactly(visit1);
    }

    @Test
    void testFindAllWithSpecification_combinedFilters() {
        Specification<Visit> spec = (root, query, cb) -> cb.and(
            cb.equal(root.get("pet").get("id"), pet1.getId()),
            cb.equal(root.get("vet").get("id"), vet1.getId()),
            cb.greaterThanOrEqualTo(root.get("date"), LocalDate.of(2023, 2, 1))
        );
        List<Visit> visits = visitRepository.findAll(spec, PageRequest.of(0, 10)).getContent();
        assertThat(visits).hasSize(1);
        assertThat(visits).containsExactly(visit2);
    }
}