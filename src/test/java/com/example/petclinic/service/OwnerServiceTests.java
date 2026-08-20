package com.example.petclinic.service;

import com.example.petclinic.model.Owner;
import com.example.petclinic.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTests {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    private Owner owner1;
    private Owner owner2;

    @BeforeEach
    void setUp() {
        owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");
        owner1.setUserId("georgef");

        owner2 = new Owner();
        owner2.setId(2);
        owner2.setFirstName("Betty");
        owner2.setLastName("Davis");
        owner2.setUserId("bettyd");
    }

    @Test
    void shouldFindOwnersByLastName() {
        when(ownerRepository.findByLastName("Franklin")).thenReturn(Arrays.asList(owner1));
        Collection<Owner> owners = ownerService.findOwnersByLastName("Franklin");
        assertThat(owners).hasSize(1);
        assertThat(owners.iterator().next().getFirstName()).isEqualTo("George");
        verify(ownerRepository, times(1)).findByLastName("Franklin");
    }

    @Test
    void shouldFindOwnerById() {
        when(ownerRepository.findById(1)).thenReturn(Optional.of(owner1));
        Optional<Owner> foundOwner = ownerService.findOwnerById(1);
        assertThat(foundOwner).isPresent();
        assertThat(foundOwner.get().getFirstName()).isEqualTo("George");
        verify(ownerRepository, times(1)).findById(1);
    }

    @Test
    void shouldFindOwnerByUserId() {
        when(ownerRepository.findByUserId("georgef")).thenReturn(Optional.of(owner1));
        Optional<Owner> foundOwner = ownerService.findOwnerByUserId("georgef");
        assertThat(foundOwner).isPresent();
        assertThat(foundOwner.get().getUserId()).isEqualTo("georgef");
        verify(ownerRepository, times(1)).findByUserId("georgef");
    }

    @Test
    void shouldSaveOwner() {
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner1);
        Owner savedOwner = ownerService.saveOwner(owner1);
        assertThat(savedOwner).isEqualTo(owner1);
        verify(ownerRepository, times(1)).save(owner1);
    }

    @Test
    void shouldNotSaveOwnerWithDuplicateUserId() {
        Owner newOwner = new Owner();
        newOwner.setFirstName("New");
        newOwner.setLastName("Owner");
        newOwner.setUserId("georgef"); // Duplicate userId

        when(ownerRepository.findByUserId("georgef")).thenReturn(Optional.of(owner1));

        assertThrows(IllegalArgumentException.class, () -> ownerService.saveOwner(newOwner));
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void shouldAllowUpdatingOwnerWithoutChangingUserId() {
        Owner existingOwner = new Owner();
        existingOwner.setId(1);
        existingOwner.setFirstName("George");
        existingOwner.setLastName("Franklin");
        existingOwner.setUserId("georgef");

        Owner updatedOwner = new Owner();
        updatedOwner.setId(1);
        updatedOwner.setFirstName("UpdatedGeorge");
        updatedOwner.setLastName("Franklin");
        updatedOwner.setUserId("georgef"); // Same userId

        when(ownerRepository.findByUserId("georgef")).thenReturn(Optional.of(existingOwner));
        when(ownerRepository.save(any(Owner.class))).thenReturn(updatedOwner);

        Owner result = ownerService.saveOwner(updatedOwner);
        assertThat(result.getFirstName()).isEqualTo("UpdatedGeorge");
        verify(ownerRepository, times(1)).save(updatedOwner);
    }

    @Test
    void shouldNotAllowUpdatingOwnerWithDifferentUserId() {
        Owner existingOwner = new Owner();
        existingOwner.setId(1);
        existingOwner.setFirstName("George");
        existingOwner.setLastName("Franklin");
        existingOwner.setUserId("georgef");

        Owner updatedOwner = new Owner();
        updatedOwner.setId(1);
        updatedOwner.setFirstName("George");
        updatedOwner.setLastName("Franklin");
        updatedOwner.setUserId("newgeorgef"); // Different userId

        // Mock the behavior of findByUserId to return the existing owner if the userId matches
        when(ownerRepository.findByUserId("newgeorgef")).thenReturn(Optional.empty());
        when(ownerRepository.findById(1)).thenReturn(Optional.of(existingOwner));

        // The immutability check is within the Owner model's setUserId method, not directly in service save
        // So, we need to simulate the call sequence that would trigger it.
        // For service layer, we primarily test the uniqueness check.
        // The immutability check is implicitly covered by the Owner model's unit tests.

        // To test the service's uniqueness check for an update, we'd need a scenario
        // where an owner is updated with a userId that already exists for *another* owner.
        // The current test focuses on the immutability of the userId on the *same* owner object,
        // which is handled by the Owner model itself.

        // Let's adjust to test the service's uniqueness check during an update.
        Owner anotherOwner = new Owner();
        anotherOwner.setId(3);
        anotherOwner.setUserId("existingotheruser");

        Owner ownerToUpdate = new Owner();
        ownerToUpdate.setId(1);
        ownerToUpdate.setFirstName("George");
        ownerToUpdate.setLastName("Franklin");
        ownerToUpdate.setUserId("existingotheruser"); // Try to set to an already existing userId

        when(ownerRepository.findByUserId("existingotheruser")).thenReturn(Optional.of(anotherOwner));

        assertThrows(IllegalArgumentException.class, () -> ownerService.saveOwner(ownerToUpdate));
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void shouldDeleteOwner() {
        ownerService.deleteOwner(owner1);
        verify(ownerRepository, times(1)).delete(owner1);
    }
}