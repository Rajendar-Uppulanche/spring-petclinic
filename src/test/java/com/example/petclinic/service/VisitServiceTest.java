package com.example.petclinic.service;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.Veterinarian;
import com.example.petclinic.model.Visit;
import com.example.petclinic.repository.VisitRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    private Owner owner1;
    private Veterinarian vet1;

    @BeforeEach
    void setUp() {
        owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("John");
        owner1.setLastName("Doe");

        pet1 = new Pet();
        pet1.setId(10);
        pet1.setName("Whiskers");
        pet1.setOwner(owner1);

        vet1 = new Veterinarian();
        vet1.setId(100);
        vet1.setName("Dr. John Doe");

        visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Annual checkup");
        visit1.setPet(pet1);
        visit1.setVeterinarian(vet1);

        visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);
        visit2.setVeterinarian(vet1);
    }

    @Test
    void testFindAllVisits() {
        when(visitRepository.findAll()).thenReturn(Arrays.asList(visit1, visit2));
        List<Visit> visits = visitService.findAllVisits();
        assertThat(visits).hasSize(2);
        verify(visitRepository, times(1)).findAll();
    }

    @Test
    void testFindVisitById() {
        when(visitRepository.findById(1)).thenReturn(Optional.of(visit1));
        Optional<Visit> foundVisit = visitService.findVisitById(1);
        assertThat(foundVisit).isPresent();
        assertThat(foundVisit.get().getDescription()).isEqualTo("Annual checkup");
        verify(visitRepository, times(1)).findById(1);
    }

    @Test
    void testSaveVisit() {
        when(visitRepository.save(any(Visit.class))).thenReturn(visit1);
        Visit savedVisit = visitService.saveVisit(new Visit());
        assertThat(savedVisit).isNotNull();
        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void testDeleteVisit() {
        visitService.deleteVisit(1);
        verify(visitRepository, times(1)).deleteById(1);
    }

    @Test
    void testFindVisitsByPetId() {
        when(visitRepository.findByPetId(10)).thenReturn(Arrays.asList(visit1, visit2));
        List<Visit> visits = visitService.findVisitsByPetId(10);
        assertThat(visits).hasSize(2);
        verify(visitRepository, times(1)).findByPetId(10);
    }

    @Test
    void testFindFilteredVisits() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 1);
        String descriptionLike = "checkup";

        when(visitRepository.findFilteredVisits(
                pet1.getId(), owner1.getId(), vet1.getId(), startDate, endDate, descriptionLike))
                .thenReturn(Arrays.asList(visit1));

        List<Visit> visits = visitService.findFilteredVisits(
                pet1.getId(), owner1.getId(), vet1.getId(), startDate, endDate, descriptionLike);

        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getDescription()).isEqualTo("Annual checkup");
        verify(visitRepository, times(1)).findFilteredVisits(
                pet1.getId(), owner1.getId(), vet1.getId(), startDate, endDate, descriptionLike);
    }

    @Test
    void testFindFilteredVisits_noFilters() {
        when(visitRepository.findFilteredVisits(null, null, null, null, null, null))
                .thenReturn(Arrays.asList(visit1, visit2));

        List<Visit> visits = visitService.findFilteredVisits(null, null, null, null, null, null);

        assertThat(visits).hasSize(2);
        verify(visitRepository, times(1)).findFilteredVisits(null, null, null, null, null, null);
    }
}