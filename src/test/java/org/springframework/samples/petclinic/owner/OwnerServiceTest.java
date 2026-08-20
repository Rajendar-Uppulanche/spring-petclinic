package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    private Owner owner;
    private Pet pet1;
    private Pet pet2;
    private PetType catType;
    private PetType dogType;

    @BeforeEach
    void setUp() {
        catType = new PetType();
        catType.setId(1);
        catType.setName("cat");

        dogType = new PetType();
        dogType.setId(2);
        dogType.setName("dog");

        pet1 = new Pet();
        pet1.setId(10);
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2020, 1, 1));
        pet1.setType(catType);

        Visit visit1 = new Visit();
        visit1.setId(100);
        visit1.setDate(LocalDate.now());
        visit1.setDescription("Routine checkup");
        pet1.addVisit(visit1);

        Visit visit2 = new Visit();
        visit2.setId(101);
        visit2.setDate(LocalDate.now().minusDays(30));
        visit2.setDescription("Vaccination");
        pet1.addVisit(visit2);

        pet2 = new Pet();
        pet2.setId(11);
        pet2.setName("Max");
        pet2.setBirthDate(LocalDate.of(2019, 5, 15));
        pet2.setType(dogType);
        // No visits for pet2

        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("George");
        owner.setLastName("Franklin");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");
        owner.setPetsInternal(new HashSet<>(Arrays.asList(pet1, pet2)));
        pet1.setOwner(owner);
        pet2.setOwner(owner);
    }

    @Test
    void shouldFindOwnerDetailsByIdWithVisitCounts() {
        when(ownerRepository.findOwnerWithPetsAndVisits(1)).thenReturn(Optional.of(owner));

        OwnerDetailsDTO ownerDetails = ownerService.findOwnerDetailsById(1);

        assertThat(ownerDetails).isNotNull();
        assertThat(ownerDetails.getId()).isEqualTo(1);
        assertThat(ownerDetails.getFirstName()).isEqualTo("George");
        assertThat(ownerDetails.getLastName()).isEqualTo("Franklin");
        assertThat(ownerDetails.getPets()).hasSize(2);

        PetDetailsDTO petDetails1 = ownerDetails.getPets().stream()
                .filter(p -> p.getId().equals(10))
                .findFirst().orElse(null);
        assertThat(petDetails1).isNotNull();
        assertThat(petDetails1.getName()).isEqualTo("Leo");
        assertThat(petDetails1.getTypeName()).isEqualTo("cat");
        assertThat(petDetails1.getVisitCount()).isEqualTo(2);

        PetDetailsDTO petDetails2 = ownerDetails.getPets().stream()
                .filter(p -> p.getId().equals(11))
                .findFirst().orElse(null);
        assertThat(petDetails2).isNotNull();
        assertThat(petDetails2.getName()).isEqualTo("Max");
        assertThat(petDetails2.getTypeName()).isEqualTo("dog");
        assertThat(petDetails2.getVisitCount()).isEqualTo(0);
    }

    @Test
    void shouldThrowExceptionWhenOwnerNotFound() {
        when(ownerRepository.findOwnerWithPetsAndVisits(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ownerService.findOwnerDetailsById(99));
    }

    @Test
    void shouldHandlePetWithNoType() {
        pet1.setType(null); // Simulate a pet with no type
        when(ownerRepository.findOwnerWithPetsAndVisits(1)).thenReturn(Optional.of(owner));

        OwnerDetailsDTO ownerDetails = ownerService.findOwnerDetailsById(1);
        PetDetailsDTO petDetails1 = ownerDetails.getPets().stream()
                .filter(p -> p.getId().equals(10))
                .findFirst().orElse(null);
        assertThat(petDetails1).isNotNull();
        assertThat(petDetails1.getTypeName()).isNull();
    }

    @Test
    void shouldHandleOwnerWithNoPets() {
        owner.setPetsInternal(Collections.emptySet());
        when(ownerRepository.findOwnerWithPetsAndVisits(1)).thenReturn(Optional.of(owner));

        OwnerDetailsDTO ownerDetails = ownerService.findOwnerDetailsById(1);
        assertThat(ownerDetails).isNotNull();
        assertThat(ownerDetails.getPets()).isEmpty();
    }
}
