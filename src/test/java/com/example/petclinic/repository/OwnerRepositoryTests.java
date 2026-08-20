package com.example.petclinic.repository;

import com.example.petclinic.model.Owner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class OwnerRepositoryTests {

    @Autowired
    OwnerRepository ownerRepository;

    @Test
    void shouldFindOwnerByLastName() {
        Collection<Owner> owners = ownerRepository.findByLastName("Franklin");
        assertThat(owners).hasSize(1);
        assertThat(owners.iterator().next().getFirstName()).isEqualTo("George");
    }

    @Test
    void shouldFindOwnerByUserId() {
        // Given an owner with a userId
        Owner owner = new Owner();
        owner.setFirstName("Test");
        owner.setLastName("User");
        owner.setAddress("123 Test St");
        owner.setCity("Test City");
        owner.setTelephone("1234567890");
        owner.setUserId("testuser123");
        ownerRepository.save(owner);

        // When
        Optional<Owner> foundOwner = ownerRepository.findByUserId("testuser123");

        // Then
        assertThat(foundOwner).isPresent();
        assertThat(foundOwner.get().getFirstName()).isEqualTo("Test");
        assertThat(foundOwner.get().getUserId()).isEqualTo("testuser123");
    }

    @Test
    void shouldNotAllowDuplicateUserId() {
        // Given two owners with the same userId
        Owner owner1 = new Owner();
        owner1.setFirstName("Test1");
        owner1.setLastName("User1");
        owner1.setAddress("123 Test St");
        owner1.setCity("Test City");
        owner1.setTelephone("1234567890");
        owner1.setUserId("duplicateuser");
        ownerRepository.save(owner1);

        Owner owner2 = new Owner();
        owner2.setFirstName("Test2");
        owner2.setLastName("User2");
        owner2.setAddress("456 Test Ave");
        owner2.setCity("Another City");
        owner2.setTelephone("0987654321");
        owner2.setUserId("duplicateuser"); // Same userId

        // When/Then
        assertThrows(DataIntegrityViolationException.class, () -> ownerRepository.save(owner2));
    }

    @Test
    void shouldNotAllowChangingUserIdAfterAssignment() {
        // Given an owner with a userId
        Owner owner = new Owner();
        owner.setFirstName("Test");
        owner.setLastName("User");
        owner.setAddress("123 Test St");
        owner.setCity("Test City");
        owner.setTelephone("1234567890");
        owner.setUserId("originaluser");
        owner = ownerRepository.save(owner);

        // When trying to change userId
        String originalUserId = owner.getUserId();
        assertThrows(IllegalArgumentException.class, () -> owner.setUserId("newuser"));

        // Verify userId remains unchanged in the entity (though not persisted due to exception)
        assertThat(owner.getUserId()).isEqualTo(originalUserId);
    }

    @Test
    void shouldAllowSettingUserIdOnce() {
        Owner owner = new Owner();
        owner.setFirstName("Test");
        owner.setLastName("User");
        owner.setAddress("123 Test St");
        owner.setCity("Test City");
        owner.setTelephone("1234567890");
        owner.setUserId("firstsetuser");
        owner = ownerRepository.save(owner);

        assertThat(owner.getUserId()).isEqualTo("firstsetuser");

        // Attempt to set the same userId again (should be allowed as it's not a change)
        owner.setUserId("firstsetuser");
        ownerRepository.save(owner);
        assertThat(owner.getUserId()).isEqualTo("firstsetuser");
    }
}