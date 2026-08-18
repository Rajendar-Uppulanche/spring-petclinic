package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.dto.PetVisitCountDto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    private Owner owner;
    private PetVisitCountDto pet1Dto;
    private PetVisitCountDto pet2Dto;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        pet1Dto = new PetVisitCountDto(1, "Leo", LocalDate.of(2000, 9, 7), "cat", 1);
        pet2Dto = new PetVisitCountDto(2, "Max", LocalDate.of(2007, 1, 1), "dog", 5);
    }

    @Test
    void shouldFindOwnerById() {
        when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));

        Optional<Owner> foundOwner = ownerService.findOwnerById(1);

        assertThat(foundOwner).isPresent();
        assertThat(foundOwner.get().getLastName()).isEqualTo("Franklin");
    }

    @Test
    void shouldReturnEmptyOptionalWhenOwnerNotFound() {
        when(ownerRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<Owner> foundOwner = ownerService.findOwnerById(999);

        assertThat(foundOwner).isNotPresent();
    }

    @Test
    void shouldFindPetsWithVisitCountsByOwnerId() {
        List<PetVisitCountDto> expectedPets = Arrays.asList(pet1Dto, pet2Dto);
        when(ownerRepository.findPetsWithVisitCountsByOwnerId(1)).thenReturn(expectedPets);

        List<PetVisitCountDto> actualPets = ownerService.findPetsWithVisitCountsByOwnerId(1);

        assertThat(actualPets).hasSize(2);
        assertThat(actualPets).containsExactlyInAnyOrder(pet1Dto, pet2Dto);
    }

    @Test
    void shouldReturnEmptyListWhenNoPetsWithVisitCountsFound() {
        when(ownerRepository.findPetsWithVisitCountsByOwnerId(anyInt())).thenReturn(Collections.emptyList());

        List<PetVisitCountDto> actualPets = ownerService.findPetsWithVisitCountsByOwnerId(999);

        assertThat(actualPets).isEmpty();
    }

    @Test
    void shouldSaveOwner() {
        Owner newOwner = new Owner();
        newOwner.setFirstName("New");
        newOwner.setLastName("Owner");
        when(ownerRepository.save(newOwner)).thenReturn(newOwner);

        Owner savedOwner = ownerService.saveOwner(newOwner);

        assertThat(savedOwner).isEqualTo(newOwner);
    }
}