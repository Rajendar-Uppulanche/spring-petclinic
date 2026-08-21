package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PetTypeRepository petTypeRepository;

    @InjectMocks
    private PetService petService;

    private Owner testOwner;
    private Pet testPetWithVisits;
    private Pet testPetWithoutVisits;
    private PetType testPetType;

    @BeforeEach
    void setUp() {
        testPetType = new PetType();
        testPetType.setId(1);
        testPetType.setName("dog");

        testOwner = new Owner();
        testOwner.setId(1);
        testOwner.setFirstName("George");
        testOwner.setLastName("Franklin");

        testPetWithVisits = new Pet();
        testPetWithVisits.setId(1);
        testPetWithVisits.setName("Leo");
        testPetWithVisits.setBirthDate(LocalDate.of(2000, 1, 1));
        testPetWithVisits.setType(testPetType);
        testPetWithVisits.setOwner(testOwner);
        Visit visit = new Visit();
        visit.setId(1);
        visit.setDate(LocalDate.now());
        visit.setDescription("Routine checkup");
        testPetWithVisits.addVisit(visit);

        testPetWithoutVisits = new Pet();
        testPetWithoutVisits.setId(2);
        testPetWithoutVisits.setName("Max");
        testPetWithoutVisits.setBirthDate(LocalDate.of(2001, 2, 2));
        testPetWithoutVisits.setType(testPetType);
        testPetWithoutVisits.setOwner(testOwner);

        testOwner.addPet(testPetWithVisits);
        testOwner.addPet(testPetWithoutVisits);
    }

    @Test
    void shouldReturnTrueWhenPetHasVisits() {
        when(ownerRepository.findById(testOwner.getId())).thenReturn(Optional.of(testOwner));

        boolean hasVisits = petService.hasVisits(testPetWithVisits.getId(), testOwner.getId());

        assertThat(hasVisits).isTrue();
    }

    @Test
    void shouldReturnFalseWhenPetHasNoVisits() {
        when(ownerRepository.findById(testOwner.getId())).thenReturn(Optional.of(testOwner));

        boolean hasVisits = petService.hasVisits(testPetWithoutVisits.getId(), testOwner.getId());

        assertThat(hasVisits).isFalse();
    }

    @Test
    void shouldReturnFalseForNewPet() {
        boolean hasVisits = petService.hasVisits(null, testOwner.getId());
        assertThat(hasVisits).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenOwnerNotFound() {
        when(ownerRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> petService.hasVisits(testPetWithVisits.getId(), 999));
    }

    @Test
    void shouldThrowExceptionWhenPetNotFound() {
        when(ownerRepository.findById(testOwner.getId())).thenReturn(Optional.of(testOwner));

        assertThrows(IllegalArgumentException.class, () -> petService.hasVisits(999, testOwner.getId()));
    }

    @Test
    void shouldFindPetTypes() {
        when(petTypeRepository.findPetTypes()).thenReturn(Collections.singletonList(testPetType));
        Collection<PetType> petTypes = petService.findPetTypes();
        assertThat(petTypes).containsExactly(testPetType);
    }

    @Test
    void shouldFindOwnerById() {
        when(ownerRepository.findById(testOwner.getId())).thenReturn(Optional.of(testOwner));
        Owner foundOwner = petService.findOwnerById(testOwner.getId());
        assertThat(foundOwner).isEqualTo(testOwner);
    }

    @Test
    void shouldFindPetById() {
        when(ownerRepository.findById(testOwner.getId())).thenReturn(Optional.of(testOwner));
        Pet foundPet = petService.findPetById(testPetWithVisits.getId(), testOwner.getId());
        assertThat(foundPet).isEqualTo(testPetWithVisits);
    }
}
