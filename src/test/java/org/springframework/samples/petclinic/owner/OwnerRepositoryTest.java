package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.samples.petclinic.owner.dto.PetVisitCountDto;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class OwnerRepositoryTest {

    @Autowired
    private OwnerRepository ownerRepository;

    @Test
    @Transactional
    void shouldFindPetsWithVisitCountsByOwnerId() {
        // Owner 1 (George Franklin) has 1 pet (Leo) with 1 visit
        // Owner 6 (Jeanette Sutherland) has 2 pets (Samantha, Max)
        // Samantha (pet 7) has 2 visits
        // Max (pet 8) has 0 visits

        // Test owner with pets and visits (ownerId = 1)
        List<PetVisitCountDto> petsForOwner1 = ownerRepository.findPetsWithVisitCountsByOwnerId(1);
        assertThat(petsForOwner1).hasSize(1);
        PetVisitCountDto pet1 = petsForOwner1.get(0);
        assertThat(pet1.getName()).isEqualTo("Leo");
        assertThat(pet1.getVisitCount()).isEqualTo(1);
        assertThat(pet1.getTypeName()).isEqualTo("cat");
        assertThat(pet1.getBirthDate()).isEqualTo(LocalDate.of(2000, 9, 7));

        // Test owner with multiple pets, some with visits, some without (ownerId = 6)
        List<PetVisitCountDto> petsForOwner6 = ownerRepository.findPetsWithVisitCountsByOwnerId(6);
        assertThat(petsForOwner6).hasSize(2);

        // Pets are ordered by name, so Max then Samantha
        PetVisitCountDto pet6_1 = petsForOwner6.get(0); // Max
        assertThat(pet6_1.getName()).isEqualTo("Max");
        assertThat(pet6_1.getVisitCount()).isEqualTo(0);
        assertThat(pet6_1.getTypeName()).isEqualTo("cat");

        PetVisitCountDto pet6_2 = petsForOwner6.get(1); // Samantha
        assertThat(pet6_2.getName()).isEqualTo("Samantha");
        assertThat(pet6_2.getVisitCount()).isEqualTo(2);
        assertThat(pet6_2.getTypeName()).isEqualTo("cat");

        // Test owner with no pets (assuming owner 10 doesn't exist or has no pets in test data)
        // Let's create a new owner with no pets for this test
        Owner newOwner = new Owner();
        newOwner.setFirstName("Test");
        newOwner.setLastName("Owner");
        newOwner.setAddress("123 Test St");
        newOwner.setCity("Test City");
        newOwner.setTelephone("1234567890");
        ownerRepository.save(newOwner);

        List<PetVisitCountDto> petsForNewOwner = ownerRepository.findPetsWithVisitCountsByOwnerId(newOwner.getId());
        assertThat(petsForNewOwner).isEmpty();

        // Test non-existent owner
        List<PetVisitCountDto> petsForNonExistentOwner = ownerRepository.findPetsWithVisitCountsByOwnerId(9999);
        assertThat(petsForNonExistentOwner).isEmpty();
    }
}