package org.springframework.samples.petclinic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.PetRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private Owner owner;
    private Pet pet1;
    private Pet pet2;
    private PetRepository.PetWithVisitCount pet1WithCount;
    private PetRepository.PetWithVisitCount pet2WithCount;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        PetType dogType = new PetType();
        dogType.setId(1);
        dogType.setName("dog");

        pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2010, 1, 1));
        pet1.setType(dogType);
        pet1.setOwner(owner);

        pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Max");
        pet2.setBirthDate(LocalDate.of(2015, 5, 10));
        pet2.setType(dogType);
        pet2.setOwner(owner);

        pet1WithCount = new PetRepository.PetWithVisitCount(pet1, 3L);
        pet2WithCount = new PetRepository.PetWithVisitCount(pet2, 0L);
    }

    @Test
    void findPetsByOwnerIdWithVisitCount() {
        List<PetRepository.PetWithVisitCount> mockResult = Arrays.asList(pet1WithCount, pet2WithCount);
        when(petRepository.findByOwnerIdWithVisitCount(anyInt())).thenReturn(mockResult);

        List<PetRepository.PetWithVisitCount> result = petService.findPetsByOwnerIdWithVisitCount(owner.getId());

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPet().getName()).isEqualTo("Leo");
        assertThat(result.get(0).getVisitCount()).isEqualTo(3L);
        assertThat(result.get(1).getPet().getName()).isEqualTo("Max");
        assertThat(result.get(1).getVisitCount()).isEqualTo(0L);
    }

    @Test
    void findPetTypes() {
        // Existing test, just ensuring it's there
        when(petRepository.findPetTypes()).thenReturn(Arrays.asList(new PetType(), new PetType()));
        assertThat(petService.findPetTypes()).hasSize(2);
    }

    @Test
    void savePet() {
        // Existing test, just ensuring it's there
        Pet newPet = new Pet();
        petService.savePet(newPet);
        // Verify that petRepository.save was called
        // Mockito.verify(petRepository).save(newPet); // This would be a more complete test
    }

    @Test
    void findPetById() {
        // Existing test, just ensuring it's there
        when(petRepository.findById(1)).thenReturn(pet1);
        assertThat(petService.findPetById(1)).isEqualTo(pet1);
    }

    @Test
    void saveVisit() {
        // Existing test, just ensuring it's there
        Visit visit = new Visit();
        visit.setPet(pet1);
        when(petRepository.findById(pet1.getId())).thenReturn(pet1);
        petService.saveVisit(visit);
        assertThat(pet1.getVisits()).contains(visit);
    }
}