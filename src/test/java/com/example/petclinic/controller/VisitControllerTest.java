package com.example.petclinic.controller;

import com.example.petclinic.dto.OwnerContactInfoDTO;
import com.example.petclinic.dto.PreventiveCareDetailsDTO;
import com.example.petclinic.dto.VisitRequestDTO;
import com.example.petclinic.dto.VisitResponseDTO;
import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PreventiveCareDetails;
import com.example.petclinic.model.Visit;
import com.example.petclinic.model.VisitType;
import com.example.petclinic.service.PetService;
import com.example.petclinic.service.VisitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitController.class)
class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    @MockBean
    private PetService petService;

    private ObjectMapper objectMapper;

    private Pet testPet;
    private Owner testOwner;
    private Visit regularVisit;
    private Visit preventiveVisit;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

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

        regularVisit = new Visit();
        regularVisit.setId(1);
        regularVisit.setPet(testPet);
        regularVisit.setVisitDate(LocalDate.of(2023, 1, 1));
        regularVisit.setDescription("Routine checkup");
        regularVisit.setVisitType(VisitType.REGULAR);

        PreventiveCareDetails preventiveDetails = new PreventiveCareDetails();
        preventiveDetails.setVaccineName("Rabies");
        preventiveDetails.setDosage("1ml");
        preventiveDetails.setNextDueDate(LocalDate.of(2024, 1, 1));

        preventiveVisit = new Visit();
        preventiveVisit.setId(2);
        preventiveVisit.setPet(testPet);
        preventiveVisit.setVisitDate(LocalDate.of(2023, 2, 1));
        preventiveVisit.setDescription("Rabies vaccination");
        preventiveVisit.setVisitType(VisitType.PREVENTIVE);
        preventiveVisit.setPreventiveCareDetails(preventiveDetails);
    }

    @Test
    void createVisit_regularVisit_shouldReturnCreated() throws Exception {
        VisitRequestDTO requestDTO = new VisitRequestDTO();
        requestDTO.setPetId(testPet.getId());
        requestDTO.setVisitDate(LocalDate.of(2023, 1, 1));
        requestDTO.setDescription("Routine checkup");
        requestDTO.setVisitType(VisitType.REGULAR);

        when(petService.findById(testPet.getId())).thenReturn(Optional.of(testPet));
        when(visitService.saveVisit(any(Visit.class))).thenReturn(regularVisit);

        mockMvc.perform(post("/api/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(regularVisit.getId()))
            .andExpect(jsonPath("$.visitType").value(VisitType.REGULAR.name()))
            .andExpect(jsonPath("$.preventiveCareDetails").doesNotExist());
    }

    @Test
    void createVisit_preventiveVisit_shouldReturnCreated() throws Exception {
        PreventiveCareDetailsDTO detailsDTO = new PreventiveCareDetailsDTO();
        detailsDTO.setVaccineName("Rabies");
        detailsDTO.setDosage("1ml");
        detailsDTO.setNextDueDate(LocalDate.of(2024, 1, 1));

        VisitRequestDTO requestDTO = new VisitRequestDTO();
        requestDTO.setPetId(testPet.getId());
        requestDTO.setVisitDate(LocalDate.of(2023, 2, 1));
        requestDTO.setDescription("Rabies vaccination");
        requestDTO.setVisitType(VisitType.PREVENTIVE);
        requestDTO.setPreventiveCareDetails(detailsDTO);

        when(petService.findById(testPet.getId())).thenReturn(Optional.of(testPet));
        when(visitService.saveVisit(any(Visit.class))).thenReturn(preventiveVisit);

        mockMvc.perform(post("/api/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(preventiveVisit.getId()))
            .andExpect(jsonPath("$.visitType").value(VisitType.PREVENTIVE.name()))
            .andExpect(jsonPath("$.preventiveCareDetails.vaccineName").value("Rabies"));
    }

    @Test
    void createVisit_preventiveVisitMissingDetails_shouldReturnBadRequest() throws Exception {
        VisitRequestDTO requestDTO = new VisitRequestDTO();
        requestDTO.setPetId(testPet.getId());
        requestDTO.setVisitDate(LocalDate.of(2023, 2, 1));
        requestDTO.setDescription("Rabies vaccination");
        requestDTO.setVisitType(VisitType.PREVENTIVE);

        when(petService.findById(testPet.getId())).thenReturn(Optional.of(testPet));
        when(visitService.saveVisit(any(Visit.class)))
            .thenThrow(new IllegalArgumentException("Preventive care details are required"));

        mockMvc.perform(post("/api/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Preventive care details are required"));
    }

    @Test
    void updateVisit_shouldReturnOk() throws Exception {
        PreventiveCareDetailsDTO detailsDTO = new PreventiveCareDetailsDTO();
        detailsDTO.setVaccineName("Distemper");
        detailsDTO.setDosage("0.5ml");
        detailsDTO.setNextDueDate(LocalDate.of(2025, 1, 1));

        VisitRequestDTO requestDTO = new VisitRequestDTO();
        requestDTO.setId(preventiveVisit.getId());
        requestDTO.setPetId(testPet.getId());
        requestDTO.setVisitDate(LocalDate.of(2023, 2, 1));
        requestDTO.setDescription("Distemper vaccination");
        requestDTO.setVisitType(VisitType.PREVENTIVE);
        requestDTO.setPreventiveCareDetails(detailsDTO);

        Visit updatedEntity = new Visit();
        updatedEntity.setId(preventiveVisit.getId());
        updatedEntity.setPet(testPet);
        updatedEntity.setVisitDate(requestDTO.getVisitDate());
        updatedEntity.setDescription(requestDTO.getDescription());
        updatedEntity.setVisitType(requestDTO.getVisitType());
        PreventiveCareDetails updatedDetails = new PreventiveCareDetails();
        updatedDetails.setVaccineName(detailsDTO.getVaccineName());
        updatedDetails.setDosage(detailsDTO.getDosage());
        updatedDetails.setNextDueDate(detailsDTO.getNextDueDate());
        updatedEntity.setPreventiveCareDetails(updatedDetails);

        when(visitService.findById(preventiveVisit.getId())).thenReturn(Optional.of(preventiveVisit));
        when(visitService.saveVisit(any(Visit.class))).thenReturn(updatedEntity);

        mockMvc.perform(put("/api/visits/{visitId}", preventiveVisit.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(preventiveVisit.getId()))
            .andExpect(jsonPath("$.description").value("Distemper vaccination"))
            .andExpect(jsonPath("$.preventiveCareDetails.vaccineName").value("Distemper"));
    }

    @Test
    void getVisitDetails_shouldReturnVisitWithOwnerInfo() throws Exception {
        VisitService.VisitDetailsWithOwnerInfo serviceResult = new VisitService.VisitDetailsWithOwnerInfo(regularVisit, testOwner);

        when(visitService.getVisitDetailsWithOwnerInfo(regularVisit.getId())).thenReturn(serviceResult);

        mockMvc.perform(get("/api/visits/{visitId}", regularVisit.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(regularVisit.getId()))
            .andExpect(jsonPath("$.petName").value(testPet.getName()))
            .andExpect(jsonPath("$.ownerContactInfo.ownerId").value(testOwner.getId()))
            .andExpect(jsonPath("$.ownerContactInfo.firstName").value(testOwner.getFirstName()))
            .andExpect(jsonPath("$.ownerContactInfo.telephone").value(testOwner.getTelephone()))
            .andExpect(jsonPath("$.ownerContactInfo.email").value(testOwner.getEmail()));
    }

    @Test
    void getVisitDetails_visitNotFound_shouldReturnNotFound() throws Exception {
        when(visitService.getVisitDetailsWithOwnerInfo(anyInt()))
            .thenThrow(new NoSuchElementException("Visit not found"));

        mockMvc.perform(get("/api/visits/{visitId}", 999)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Visit not found"));
    }
}