package com.example.controller;

import com.example.model.Specialty;
import com.example.model.Vet;
import com.example.service.VetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VetController.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VetService vetService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void whenGetAllVets_noSpecialtyParam_thenReturnAllVets() throws Exception {
        List<Vet> allVets = Arrays.asList(vet1, vet2);
        when(vetService.findAllVets()).thenReturn(allVets);

        mockMvc.perform(get("/api/vets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is(vet1.getFirstName())))
                .andExpect(jsonPath("$[1].firstName", is(vet2.getFirstName())));
    }

    @Test
    void whenGetAllVets_withSpecialtyParam_thenReturnFilteredVets() throws Exception {
        List<Vet> radiologyVets = Collections.singletonList(vet1);
        when(vetService.findVetsBySpecialty("Radiology")).thenReturn(radiologyVets);

        mockMvc.perform(get("/api/vets?specialty=Radiology")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is(vet1.getFirstName())))
                .andExpect(jsonPath("$[0].specialties[0].name", is(radiology.getName())));
    }

    @Test
    void whenGetAllVets_withNonExistentSpecialty_thenReturnEmptyList() throws Exception {
        when(vetService.findVetsBySpecialty("NonExistent")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/vets?specialty=NonExistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void whenGetAllDistinctSpecialties_thenReturnAllSpecialties() throws Exception {
        Set<Specialty> distinctSpecialties = new HashSet<>(Arrays.asList(radiology, surgery, dentistry));
        when(vetService.findAllDistinctSpecialties()).thenReturn(distinctSpecialties);

        mockMvc.perform(get("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is(radiology.getName())))
                .andExpect(jsonPath("$[1].name", is(surgery.getName())))
                .andExpect(jsonPath("$[2].name", is(dentistry.getName())));
    }

    @Test
    void whenGetAllDistinctSpecialties_noSpecialties_thenReturnEmptyList() throws Exception {
        when(vetService.findAllDistinctSpecialties()).thenReturn(Collections.emptySet());

        mockMvc.perform(get("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void whenGetVetById_thenReturnVet() throws Exception {
        when(vetService.findVetById(10L)).thenReturn(vet1);

        mockMvc.perform(get("/api/vets/{id}", 10L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(vet1.getFirstName())));
    }
}