package com.example.service;

import com.example.model.Specialty;
import com.example.model.Vet;
import com.example.repository.SpecialtyRepository;
import com.example.repository.VetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VetServiceTest {

    @Mock
    private VetRepository vetRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private VetService vetService;

    private Vet vet1;
    private Vet vet2;
    private Specialty radiology;
    private Specialty surgery;
    private Specialty dentistry;

    @BeforeEach
    void setUp() {
        radiology = new Specialty("Radiology");
        radiology.setId(1L);
        surgery = new Specialty("Surgery");
        surgery.setId(2L);
        dentistry = new Specialty("Dentistry");
        dentistry.setId(3L);

        vet1 = new Vet("James", "Carter");
        vet1.setId(10L);
        vet1.addSpecialty(radiology);
        vet1.addSpecialty(surgery);

        vet2 = new Vet("Helen", "Leary");
        vet2.setId(20L);
        vet2.addSpecialty(dentistry);
    }

    @Test
    void whenFindAllVets_thenReturnAllVets() {
        List<Vet> allVets = Arrays.asList(vet1, vet2);
        when(vetRepository.findAll()).thenReturn(allVets);
        List<Vet> foundVets = vetService.findAllVets();
        assertThat(foundVets).hasSize(2);
        assertThat(foundVets).containsExactlyInAnyOrder(vet1, vet2);
        verify(vetRepository, times(1)).findAll();
    }

    @Test
    void whenFindVetsBySpecialty_thenReturnFilteredVets() {
        when(vetRepository.findBySpecialtyName("Radiology")).thenReturn(Collections.singletonList(vet1));
        List<Vet> foundVets = vetService.findVetsBySpecialty("Radiology");
        assertThat(foundVets).hasSize(1);
        assertThat(foundVets).containsExactly(vet1);
        verify(vetRepository, times(1)).findBySpecialtyName("Radiology");
        verify(vetRepository, never()).findAll();
    }

    @Test
    void whenFindVetsBySpecialty_withAllKeyword_thenReturnAllVets() {
        List<Vet> allVets = Arrays.asList(vet1, vet2);
        when(vetRepository.findAll()).thenReturn(allVets);
        List<Vet> foundVets = vetService.findVetsBySpecialty("all");
        assertThat(foundVets).hasSize(2);
        assertThat(foundVets).containsExactlyInAnyOrder(vet1, vet2);
        verify(vetRepository, times(1)).findAll();
        verify(vetRepository, never()).findBySpecialtyName(anyString());
    }

    @Test
    void whenFindVetsBySpecialty_withEmptyOrNullSpecialty_thenReturnAllVets() {
        List<Vet> allVets = Arrays.asList(vet1, vet2);
        when(vetRepository.findAll()).thenReturn(allVets);
        List<Vet> foundVetsNull = vetService.findVetsBySpecialty(null);
        List<Vet> foundVetsEmpty = vetService.findVetsBySpecialty("");
        List<Vet> foundVetsBlank = vetService.findVetsBySpecialty("   ");
        assertThat(foundVetsNull).hasSize(2);
        assertThat(foundVetsEmpty).hasSize(2);
        assertThat(foundVetsBlank).hasSize(2);
        verify(vetRepository, times(3)).findAll();
        verify(vetRepository, never()).findBySpecialtyName(anyString());
    }

    @Test
    void whenFindAllDistinctSpecialties_thenReturnAllUniqueSpecialties() {
        Set<Specialty> distinctSpecialties = new HashSet<>(Arrays.asList(radiology, surgery, dentistry));
        when(vetRepository.findAllDistinctSpecialties()).thenReturn(distinctSpecialties);
        Set<Specialty> foundSpecialties = vetService.findAllDistinctSpecialties();
        assertThat(foundSpecialties).hasSize(3);
        assertThat(foundSpecialties).containsExactlyInAnyOrder(radiology, surgery, dentistry);
        verify(vetRepository, times(1)).findAllDistinctSpecialties();
    }

    @Test
    void whenAddSpecialtyToVet_existingSpecialty_thenVetIsUpdated() {
        when(vetRepository.findById(vet1.getId())).thenReturn(Optional.of(vet1));
        when(specialtyRepository.findByName("Dentistry")).thenReturn(Optional.of(dentistry));
        when(vetRepository.save(any(Vet.class))).thenReturn(vet1);
        Vet updatedVet = vetService.addSpecialtyToVet(vet1.getId(), "Dentistry");
        assertThat(updatedVet.getSpecialties()).contains(radiology, surgery, dentistry);
        verify(vetRepository, times(1)).findById(vet1.getId());
        verify(specialtyRepository, times(1)).findByName("Dentistry");
        verify(specialtyRepository, never()).save(any(Specialty.class));
        verify(vetRepository, times(1)).save(vet1);
    }

    @Test
    void whenAddSpecialtyToVet_newSpecialty_thenSpecialtyAndVetAreUpdated() {
        Specialty newSpecialty = new Specialty("Cardiology");
        newSpecialty.setId(4L);
        when(vetRepository.findById(vet1.getId())).thenReturn(Optional.of(vet1));
        when(specialtyRepository.findByName("Cardiology")).thenReturn(Optional.empty());
        when(specialtyRepository.save(any(Specialty.class))).thenReturn(newSpecialty);
        when(vetRepository.save(any(Vet.class))).thenReturn(vet1);
        Vet updatedVet = vetService.addSpecialtyToVet(vet1.getId(), "Cardiology");
        assertThat(updatedVet.getSpecialties()).contains(radiology, surgery, newSpecialty);
        verify(vetRepository, times(1)).findById(vet1.getId());
        verify(specialtyRepository, times(1)).findByName("Cardiology");
        verify(specialtyRepository, times(1)).save(any(Specialty.class));
        verify(vetRepository, times(1)).save(vet1);
    }

    @Test
    void whenAddSpecialtyToVet_vetNotFound_thenThrowException() {
        when(vetRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> {
            vetService.addSpecialtyToVet(99L, "Dentistry");
        });
        verify(vetRepository, times(1)).findById(99L);
        verify(specialtyRepository, never()).findByName(anyString());
        verify(specialtyRepository, never()).save(any(Specialty.class));
        verify(vetRepository, never()).save(any(Vet.class));
    }

    @Test
    void whenSaveVet_thenVetIsSaved() {
        when(vetRepository.save(any(Vet.class))).thenReturn(vet1);
        Vet savedVet = vetService.saveVet(new Vet("Test", "Vet"));
        assertThat(savedVet).isEqualTo(vet1);
        verify(vetRepository, times(1)).save(any(Vet.class));
    }

    @Test
    void whenFindVetById_thenVetIsReturned() {
        when(vetRepository.findById(10L)).thenReturn(Optional.of(vet1));
        Vet foundVet = vetService.findVetById(10L);
        assertThat(foundVet).isEqualTo(vet1);
        verify(vetRepository, times(1)).findById(10L);
    }

    @Test
    void whenDeleteVet_thenVetIsDeleted() {
        vetService.deleteVet(10L);
        verify(vetRepository, times(1)).deleteById(10L);
    }
}