package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class OwnerRepositoryTests {

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void findByIdWithPetsAndVisitsShouldLoadAllData() {
        // Given an owner with pets and visits
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Anytown");
        owner.setTelephone("5551234567");

        Pet pet1 = new Pet();
        pet1.setName("Buddy");
        pet1.setBirthDate(LocalDate.of(2018, 1, 1));
        PetType dog = new PetType();
        dog.setName("dog");
        entityManager.persist(dog);
        pet1.setType(dog);
        owner.addPet(pet1);

        Visit visit1 = new Visit();
        visit1.setDate(LocalDate.now());
        visit1.setDescription("Routine checkup");
        pet1.addVisit(visit1);

        Pet pet2 = new Pet();
        pet2.setName("Whiskers");
        pet2.setBirthDate(LocalDate.of(2019, 5, 10));
        PetType cat = new PetType();
        cat.setName("cat");
        entityManager.persist(cat);
        pet2.setType(cat);
        owner.addPet(pet2);

        // Pet2 has no visits
        
        ownerRepository.save(owner);
        entityManager.flush();
        entityManager.clear(); // Detach entities to ensure they are reloaded from DB

        // When
        Optional<Owner> foundOwnerOptional = ownerRepository.findByIdWithPetsAndVisits(owner.getId());

        // Then
        assertThat(foundOwnerOptional).isPresent();
        Owner foundOwner = foundOwnerOptional.get();

        assertThat(foundOwner.getFirstName()).isEqualTo("John");
        assertThat(foundOwner.getPets()).hasSize(2);

        // Verify pet1 and its visit
        Optional<Pet> foundPet1Optional = foundOwner.getPets().stream()
            .filter(p -> p.getName().equals("Buddy"))
            .findFirst();
        assertThat(foundPet1Optional).isPresent();
        Pet foundPet1 = foundPet1Optional.get();
        assertThat(foundPet1.getVisits()).hasSize(1);
        assertThat(foundPet1.getVisits().iterator().next().getDescription()).isEqualTo("Routine checkup");

        // Verify pet2 and its lack of visits
        Optional<Pet> foundPet2Optional = foundOwner.getPets().stream()
            .filter(p -> p.getName().equals("Whiskers"))
            .findFirst();
        assertThat(foundPet2Optional).isPresent();
        Pet foundPet2 = foundPet2Optional.get();
        assertThat(foundPet2.getVisits()).isEmpty(); // Should be 0 visits
    }
}
