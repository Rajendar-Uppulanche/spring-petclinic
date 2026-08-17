package com.example.petclinic.service;

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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitService visitService;

    private Visit visit1;
    private Visit visit2;

    @BeforeEach
    void setUp() {
        visit1 = new Visit();
        visit1.setId(1);
        visit1.setVisitDate(LocalDate.of(2023, 1, 1));
        visit1.setDescription("Routine checkup");

        visit2 = new Visit();
        visit2.setId(2);
        visit2.setVisitDate(LocalDate.of(2023, 1, 15));
        visit2.setDescription("Vaccination");
    }

    @Test
    void findAllVisits() {
        when(visitRepository.findAll()).thenReturn(Arrays.asList(visit1, visit2));
        List<Visit> visits = visitService.findAllVisits();
        assertThat(visits).hasSize(2);
        verify(visitRepository, times(1)).findAll();
    }

    @Test
    void findVisitById() {
        when(visitRepository.findById(1)).thenReturn(Optional.of(visit1));
        Optional<Visit> foundVisit = visitService.findVisitById(1);
        assertThat(foundVisit).isPresent();
        assertThat(foundVisit.get().getDescription()).isEqualTo("Routine checkup");
        verify(visitRepository, times(1)).findById(1);
    }

    @Test
    void saveVisit() {
        when(visitRepository.save(any(Visit.class))).thenReturn(visit1);
        Visit savedVisit = visitService.saveVisit(new Visit());
        assertThat(savedVisit).isEqualTo(visit1);
        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void deleteVisit() {
        visitService.deleteVisit(1);
        verify(visitRepository, times(1)).deleteById(1);
    }

    @Test
    void findVisitsByPetId() {
        when(visitRepository.findByPetId(anyInt())).thenReturn(Arrays.asList(visit1));
        List<Visit> visits = visitService.findVisitsByPetId(100);
        assertThat(visits).hasSize(1);
        verify(visitRepository, times(1)).findByPetId(anyInt());
    }

    @Test
    void findFilteredVisits() {
        when(visitRepository.findFilteredVisits(any(), any(), any(), any(), any(), any()))
                .thenReturn(Arrays.asList(visit1));

        List<Visit> visits = visitService.findFilteredVisits(
                1, 1, 1, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31), "checkup"
        );
        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getDescription()).isEqualTo("Routine checkup");
        verify(visitRepository, times(1)).findFilteredVisits(
                any(), any(), any(), any(), any(), any()
        );
    }
}