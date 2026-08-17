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
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceTests {

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
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");

        visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
    }

    @Test
    void testFindVisits_noFilters() {
        when(visitRepository.findFilteredVisits(any(), any(), any(), any(), any(), any()))
                .thenReturn(Arrays.asList(visit1, visit2));

        Collection<Visit> visits = visitService.findVisits(null, null, null, null, null, null);
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    void testFindVisits_withPetIdFilter() {
        when(visitRepository.findFilteredVisits(eq(1), any(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(visit1));

        Collection<Visit> visits = visitService.findVisits(1, null, null, null, null, null);
        assertThat(visits).hasSize(1);
        assertThat(visits).containsExactly(visit1);
    }

    @Test
    void testFindVisits_withDateRangeFilter() {
        when(visitRepository.findFilteredVisits(any(), any(), any(), eq(LocalDate.of(2023, 2, 1)), eq(LocalDate.of(2023, 2, 28)), any()))
                .thenReturn(Collections.singletonList(visit2));

        Collection<Visit> visits = visitService.findVisits(null, null, null, LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 28), null);
        assertThat(visits).hasSize(1);
        assertThat(visits).containsExactly(visit2);
    }

    @Test
    void testFindVisits_noMatch() {
        when(visitRepository.findFilteredVisits(any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        Collection<Visit> visits = visitService.findVisits(100, null, null, null, null, null);
        assertThat(visits).isEmpty();
    }
}