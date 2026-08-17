package com.example.petclinic.service;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PetType;
import com.example.petclinic.model.Visit;
import com.example.petclinic.repository.VisitRepository;
import com.example.petclinic.service.dto.PetVisitCountDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitService visitService;

    private Visit visit1;
    private Visit visit2;
    private Pet pet1;
    private Pet pet2;
    private Owner owner;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1L);
        owner.setFirstName("John");
        owner.setLastName("Doe");

        PetType dogType = new PetType();
        dogType.setId(1L);
        dogType.setName("dog");

        pet1 = new Pet();
        pet1.setId(10L);
        pet1.setName("Buddy");
        pet1.setOwner(owner);
        pet1.setType(dogType);

        pet2 = new Pet();
        pet2.setId(11L);
        pet2.setName("Lucy");
        pet2.setOwner(owner);
        pet2.setType(dogType);

        visit1 = new Visit();
        visit1.setId(100L);
        visit1.setDate(LocalDate.now());
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);

        visit2 = new Visit();
        visit2.setId(101L);
        visit2.setDate(LocalDate.now().minusDays(5));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);
    }

    @Test
    void findAll() {
        when(visitRepository.findAll()).thenReturn(Arrays.asList(visit1, visit2));
        List<Visit> visits = visitService.findAll();
        assertNotNull(visits);
        assertEquals(2, visits.size());
        verify(visitRepository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(visitRepository.findById(100L)).thenReturn(Optional.of(visit1));
        Optional<Visit> foundVisit = visitService.findById(100L);
        assertTrue(foundVisit.isPresent());
        assertEquals(visit1.getDescription(), foundVisit.get().getDescription());
        verify(visitRepository, times(1)).findById(100L);
    }

    @Test
    void save() {
        when(visitRepository.save(any(Visit.class))).thenReturn(visit1);
        Visit savedVisit = visitService.save(new Visit());
        assertNotNull(savedVisit);
        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void deleteById() {
        visitService.deleteById(100L);
        verify(visitRepository, times(1)).deleteById(100L);
    }

    @Test
    void countVisitsByPetForOwner_shouldReturnCorrectCounts() {
        // Scenario: pet1 has 2 visits, pet2 has 0 visits
        List<PetVisitCountDTO> mockCounts = Arrays.asList(
            new PetVisitCountDTO(pet1.getId(), 2L)
        );
        when(visitRepository.countVisitsByPetForOwner(owner.getId())).thenReturn(mockCounts);

        List<PetVisitCountDTO> result = visitService.countVisitsByPetForOwner(owner.getId());

        assertNotNull(result);
        assertEquals(1, result.size()); // Only pets with visits are returned by the query
        assertEquals(pet1.getId(), result.get(0).getPetId());
        assertEquals(2L, result.get(0).getVisitCount());

        verify(visitRepository, times(1)).countVisitsByPetForOwner(owner.getId());
    }

    @Test
    void countVisitsByPetForOwner_noVisits() {
        when(visitRepository.countVisitsByPetForOwner(owner.getId())).thenReturn(List.of());

        List<PetVisitCountDTO> result = visitService.countVisitsByPetForOwner(owner.getId());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(visitRepository, times(1)).countVisitsByPetForOwner(owner.getId());
    }
}