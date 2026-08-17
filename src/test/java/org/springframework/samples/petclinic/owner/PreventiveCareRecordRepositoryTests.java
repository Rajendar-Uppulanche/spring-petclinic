package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class PreventiveCareRecordRepositoryTests {

    @Autowired
    private PreventiveCareRecordRepository preventiveCareRecordRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindPreventiveCareRecordsByPetId() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");
        entityManager.persist(owner);

        Pet pet = new Pet();
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.of(2000, 9, 7));
        pet.setType(entityManager.find(PetType.class, 1)); // Assuming PetType with id 1 exists
        owner.addPet(pet);
        entityManager.persist(pet);

        PreventiveCareRecord record1 = new PreventiveCareRecord();
        record1.setPet(pet);
        record1.setRecordDate(LocalDate.now());
        record1.setCareType("Vaccination");
        record1.setDescription("Rabies vaccine");
        record1.setOutcome("Successful");
        entityManager.persist(record1);

        PreventiveCareRecord record2 = new PreventiveCareRecord();
        record2.setPet(pet);
        record2.setRecordDate(LocalDate.now().minusMonths(6));
        record2.setCareType("Deworming");
        record2.setDescription("Fenbendazole");
        record2.setOutcome("Successful");
        entityManager.persist(record2);

        List<PreventiveCareRecord> records = preventiveCareRecordRepository.findByPetId(pet.getId());
        assertThat(records).hasSize(2);
        assertThat(records).extracting(PreventiveCareRecord::getCareType).containsExactlyInAnyOrder("Vaccination", "Deworming");
    }

    @Test
    void shouldFindPreventiveCareRecordsByPetOwnerId() {
        Owner owner1 = new Owner();
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");
        owner1.setAddress("110 W. Liberty St.");
        owner1.setCity("Madison");
        owner1.setTelephone("6085551023");
        entityManager.persist(owner1);

        Pet pet1 = new Pet();
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2000, 9, 7));
        pet1.setType(entityManager.find(PetType.class, 1));
        owner1.addPet(pet1);
        entityManager.persist(pet1);

        PreventiveCareRecord record1 = new PreventiveCareRecord();
        record1.setPet(pet1);
        record1.setRecordDate(LocalDate.now());
        record1.setCareType("Vaccination");
        record1.setDescription("Rabies vaccine");
        record1.setOutcome("Successful");
        entityManager.persist(record1);

        Owner owner2 = new Owner();
        owner2.setFirstName("Betty");
        owner2.setLastName("Davis");
        owner2.setAddress("638 Cardinal Ave.");
        owner2.setCity("Sun Prairie");
        owner2.setTelephone("6085551749");
        entityManager.persist(owner2);

        Pet pet2 = new Pet();
        pet2.setName("Max");
        pet2.setBirthDate(LocalDate.of(2007, 1, 1));
        pet2.setType(entityManager.find(PetType.class, 2)); // Assuming PetType with id 2 exists
        owner2.addPet(pet2);
        entityManager.persist(pet2);

        PreventiveCareRecord record2 = new PreventiveCareRecord();
        record2.setPet(pet2);
        record2.setRecordDate(LocalDate.now());
        record2.setCareType("Flea Treatment");
        record2.setDescription("Frontline");
        record2.setOutcome("Successful");
        entityManager.persist(record2);

        List<PreventiveCareRecord> recordsForOwner1 = preventiveCareRecordRepository.findByPetOwnerId(owner1.getId());
        assertThat(recordsForOwner1).hasSize(1);
        assertThat(recordsForOwner1.get(0).getCareType()).isEqualTo("Vaccination");

        List<PreventiveCareRecord> recordsForOwner2 = preventiveCareRecordRepository.findByPetOwnerId(owner2.getId());
        assertThat(recordsForOwner2).hasSize(1);
        assertThat(recordsForOwner2.get(0).getCareType()).isEqualTo("Flea Treatment");
    }
}
