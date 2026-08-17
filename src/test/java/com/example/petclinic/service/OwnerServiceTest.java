package com.example.petclinic.service;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PetType;
import com.example.petclinic.repository.OwnerRepository;
import com.example.petclinic.service.dto.OwnerDetailsDTO;
import com.example.petclinic.service.dto.PetDTO;
import com.example.petclinic.service.dto.PetVisitCountDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private VisitService visitService; // Mock VisitService

    @InjectMocks
    private OwnerService ownerService;

    private Owner owner;
    private Pet pet1;
    private Pet pet2;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1L);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Anytown");
        owner.setTelephone("555-1234");

        PetType dogType = new PetType();
        dogType.setId(1L);
        dogType.setName("dog");

        pet1 = new Pet();
        pet1.setId(10L);
        pet1.setName("Buddy");
        pet1.setBirthDate(LocalDate.of(2020, 1, 1));
        pet1.setOwner(owner);
        pet1.setType(dogType);

        pet2 = new Pet();
        pet2.setId(11L);
        pet2.setName("Lucy");
        pet2.setBirthDate(LocalDate.of(2021, 5, 10));
        pet2.setOwner(owner);
        pet2.setType(dogType);

        Set<Pet> pets = new HashSet<>();
        pets.add(pet1);
        pets.add(pet2);
        owner.setPets(pets);
    }

    @Test
    void findOwnerDetailsById_shouldReturnCorrectDetailsWithVisitCounts() {
        // Mock repository to return owner with pets
        when(ownerRepository.findByIdWithPets(1L)).thenReturn(Optional.of(owner));

        // Mock visit service to return visit counts for pets
        List<PetVisitCountDTO> mockVisitCounts = Arrays.asList(
            new PetVisitCountDTO(pet1.getId(), 3L), // Buddy has 3 visits
            new PetVisitCountDTO(pet2.getId(), 0L)  // Lucy has 0 visits (or not present in map, should default to 0)
        );
        when(visitService.countVisitsByPetForOwner(1L)).thenReturn(mockVisitCounts);

        Optional<OwnerDetailsDTO> result = ownerService.findOwnerDetailsById(1L);

        assertTrue(result.isPresent());
        OwnerDetailsDTO dto = result.get();

        assertEquals(owner.getId(), dto.getId());
        assertEquals(owner.getFirstName(), dto.getFirstName());
        assertEquals(owner.getLastName(), dto.getLastName());
        assertEquals(owner.getAddress(), dto.getAddress());
        assertEquals(owner.getCity(), dto.getCity());
        assertEquals(owner.getTelephone(), dto.getTelephone());
        assertEquals(2, dto.getPets().size());

        // Verify visit counts for each pet
        PetDTO buddyDto = dto.getPets().stream()
            .filter(p -> p.getId().equals(pet1.getId()))
            .findFirst().orElseThrow();
        assertEquals(3L, buddyDto.getVisitCount());

        PetDTO lucyDto = dto.getPets().stream()
            .filter(p -> p.getId().equals(pet2.getId()))
            .findFirst().orElseThrow();
        assertEquals(0L, lucyDto.getVisitCount()); // Should default to 0 if not explicitly in mockVisitCounts

        // Verify N+1 avoidance: visitService.countVisitsByPetForOwner should be called only once
        verify(visitService, times(1)).countVisitsByPetForOwner(1L);
        verify(ownerRepository, times(1)).findByIdWithPets(1L);
    }

    @Test
    void findOwnerDetailsById_noOwnerFound() {
        when(ownerRepository.findByIdWithPets(anyLong())).thenReturn(Optional.empty());

        Optional<OwnerDetailsDTO> result = ownerService.findOwnerDetailsById(99L);

        assertFalse(result.isPresent());
        verify(visitService, never()).countVisitsByPetForOwner(anyLong()); // Should not call visit service if owner not found
    }

    @Test
    void findOwnerDetailsById_ownerWithNoPets() {
        owner.setPets(new HashSet<>()); // Owner has no pets
        when(ownerRepository.findByIdWithPets(1L)).thenReturn(Optional.of(owner));
        when(visitService.countVisitsByPetForOwner(1L)).thenReturn(List.of()); // No visits for no pets

        Optional<OwnerDetailsDTO> result = ownerService.findOwnerDetailsById(1L);

        assertTrue(result.isPresent());
        OwnerDetailsDTO dto = result.get();
        assertTrue(dto.getPets().isEmpty());
        verify(visitService, times(1)).countVisitsByPetForOwner(1L);
    }
}