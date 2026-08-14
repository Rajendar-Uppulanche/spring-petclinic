package com.example.petclinic.service;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PreventiveCareDetails;
import com.example.petclinic.model.Visit;
import com.example.petclinic.model.VisitType;
import com.example.petclinic.repository.OwnerRepository;
import com.example.petclinic.repository.PetRepository;
import com.example.petclinic.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private VisitService visitService;

    private Pet testPet;
    private Owner testOwner;

    @BeforeEach
    void setUp() {
        testOwner = new Owner();
        testOwner.setId(1);
        testOwner.setFirstName("John");
        testOwner.setLastName("Doe");
        testOwner.setTelephone("123-456-7890");
        testOwner.setEmail("john.doe@example.com");

        testPet = new Pet();
        testPet.setId(1);
        testPet.setName("Buddy");
        testPet.setOwner(testOwner);
    }

    @Test
    void saveVisit_regularVisit_shouldSaveSuccessfully() {
        Visit regularVisit = new Visit();
        regularVisit.setVisitType(VisitType.REGULAR);
        regularVisit.setPet(testPet);
        regularVisit.setVisitDate(LocalDate.now());
        regularVisit.setDescription("Routine checkup");

        when(visitRepository.save(any(Visit.class))).thenReturn(regularVisit);

        Visit savedVisit = visitService.saveVisit(regularVisit);

        assertNotNull(savedVisit);
        assertEquals(VisitType.REGULAR, savedVisit.getVisitType());
        assertNull(savedVisit.getPreventiveCareDetails());
        verify(visitRepository, times(1)).save(regularVisit);
    }

    @Test
    void saveVisit_preventiveVisit_shouldSaveSuccessfullyWithDetails() {
        PreventiveCareDetails details = new PreventiveCareDetails();
        details.setVaccineName("Rabies");
        details.setDosage("1ml");
        details.setNextDueDate(LocalDate.now().plusYears(1));

        Visit preventiveVisit = new Visit();
        preventiveVisit.setVisitType(VisitType.PREVENTIVE);
        preventiveVisit.setPet(testPet);
        preventiveVisit.setVisitDate(LocalDate.now());
        preventiveVisit.setDescription("Rabies vaccination");
        preventiveVisit.setPreventiveCareDetails(details);

        when(visitRepository.save(any(Visit.class))).thenReturn(preventiveVisit);

        Visit savedVisit = visitService.saveVisit(preventiveVisit);

        assertNotNull(savedVisit);
        assertEquals(VisitType.PREVENTIVE, savedVisit.getVisitType());
        assertNotNull(savedVisit.getPreventiveCareDetails());
        assertEquals("Rabies", savedVisit.getPreventiveCareDetails().getVaccineName());
        verify(visitRepository, times(1)).save(preventiveVisit);
    }

    @Test
    void saveVisit_preventiveVisit_shouldThrowExceptionIfDetailsMissing() {
        Visit preventiveVisit = new Visit();
        preventiveVisit.setVisitType(VisitType.PREVENTIVE);
        preventiveVisit.setPet(testPet);
        preventiveVisit.setVisitDate(LocalDate.now());
        preventiveVisit.setDescription("Rabies vaccination");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            visitService.saveVisit(preventiveVisit);
        });

        assertTrue(thrown.getMessage().contains("Preventive care details"));
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void saveVisit_preventiveVisit_shouldThrowExceptionIfVaccineNameMissing() {
        PreventiveCareDetails details = new PreventiveCareDetails();
        details.setDosage("1ml");
        details.setNextDueDate(LocalDate.now().plusYears(1));

        Visit preventiveVisit = new Visit();
        preventiveVisit.setVisitType(VisitType.PREVENTIVE);
        preventiveVisit.setPet(testPet);
        preventiveVisit.setVisitDate(LocalDate.now());
        preventiveVisit.setDescription("Rabies vaccination");
        preventiveVisit.setPreventiveCareDetails(details);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            visitService.saveVisit(preventiveVisit);
        });

        assertTrue(thrown.getMessage().contains("Preventive care details"));
        verify(visitRepository, never()).save(any(Visit.class));
    }

    @Test
    void getVisitDetailsWithOwnerInfo_shouldReturnDetails() {
        Visit visit = new Visit();
        visit.setId(1);
        visit.setPet(testPet);
        visit.setVisitDate(LocalDate.now());
        visit.setDescription("Checkup");
        visit.setVisitType(VisitType.REGULAR);

        when(visitRepository.findById(1)).thenReturn(Optional.of(visit));

        VisitService.VisitDetailsWithOwnerInfo result = visitService.getVisitDetailsWithOwnerInfo(1);

        assertNotNull(result);
        assertEquals(visit.getId(), result.getVisit().getId());
        assertEquals(testOwner.getId(), result.getOwner().getId());
        assertEquals(testOwner.getFirstName(), result.getOwner().getFirstName());
        assertEquals(testOwner.getEmail(), result.getOwner().getEmail());
    }

    @Test
    void getVisitDetailsWithOwnerInfo_visitNotFound_shouldThrowException() {
        when(visitRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            visitService.getVisitDetailsWithOwnerInfo(99);
        });
    }

    @Test
    void getVisitDetailsWithOwnerInfo_petNotFound_shouldThrowException() {
        Visit visit = new Visit();
        visit.setId(1);
        visit.setPet(null);

        when(visitRepository.findById(1)).thenReturn(Optional.of(visit));

        assertThrows(IllegalStateException.class, () -> {
            visitService.getVisitDetailsWithOwnerInfo(1);
        });
    }

    @Test
    void getVisitDetailsWithOwnerInfo_ownerNotFound_shouldThrowException() {
        Pet petWithoutOwner = new Pet();
        petWithoutOwner.setId(2);
        petWithoutOwner.setName("Max");
        petWithoutOwner.setOwner(null);

        Visit visit = new Visit();
        visit.setId(1);
        visit.setPet(petWithoutOwner);

        when(visitRepository.findById(1)).thenReturn(Optional.of(visit));

        assertThrows(IllegalStateException.class, () -> {
            visitService.getVisitDetailsWithOwnerInfo(1);
        });
    }
}