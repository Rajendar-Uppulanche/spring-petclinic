package com.example.petclinic.web;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.Veterinarian;
import com.example.petclinic.model.Visit;
import com.example.petclinic.service.OwnerService;
import com.example.petclinic.service.PetService;
import com.example.petclinic.service.VeterinarianService;
import com.example.petclinic.service.VisitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    @MockBean
    private OwnerService ownerService;
    @MockBean
    private VeterinarianService veterinarianService;

    private ObjectMapper objectMapper;

    private Visit visit1;
    private Visit visit2;
    private Pet pet1;
    private Owner owner1;
    private Veterinarian vet1;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

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
    void testGetAllVisits_noFilters() throws Exception {
        when(visitService.findFilteredVisits(null, null, null, null, null, null))
                .thenReturn(Arrays.asList(visit1, visit2));

        mockMvc.perform(get("/api/visits"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(visit1.getId())))
                .andExpect(jsonPath("$[1].id", is(visit2.getId())));

        verify(visitService, times(1)).findFilteredVisits(null, null, null, null, null, null);
    }

    @Test
    void testGetAllVisits_withFilters() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        when(visitService.findFilteredVisits(eq(pet1.getId()), eq(owner1.getId()), eq(vet1.getId()), eq(startDate), eq(endDate), eq("checkup")))
                .thenReturn(Collections.singletonList(visit1));

        mockMvc.perform(get("/api/visits")
                        .param("petId", pet1.getId().toString())
                        .param("ownerId", owner1.getId().toString())
                        .param("veterinarianId", vet1.getId().toString())
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("description", "checkup"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(visit1.getId())))
                .andExpect(jsonPath("$[0].description", is("Annual checkup")));

        verify(visitService, times(1)).findFilteredVisits(eq(pet1.getId()), eq(owner1.getId()), eq(vet1.getId()), eq(startDate), eq(endDate), eq("checkup"));
    }

    @Test
    void testGetVisitById() throws Exception {
        when(visitService.findVisitById(1)).thenReturn(Optional.of(visit1));

        mockMvc.perform(get("/api/visits/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(visit1.getId())))
                .andExpect(jsonPath("$.description", is(visit1.getDescription())));

        verify(visitService, times(1)).findVisitById(1);
    }

    @Test
    void testGetVisitById_notFound() throws Exception {
        when(visitService.findVisitById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/visits/{id}", 99))
                .andExpect(status().isNotFound());

        verify(visitService, times(1)).findVisitById(99);
    }

    @Test
    void testCreateVisit() throws Exception {
        Visit newVisit = new Visit();
        newVisit.setDate(LocalDate.of(2023, 5, 1));
        newVisit.setDescription("New visit");
        newVisit.setPet(pet1);
        newVisit.setVeterinarian(vet1);

        Visit savedVisit = new Visit();
        savedVisit.setId(3);
        savedVisit.setDate(newVisit.getDate());
        savedVisit.setDescription(newVisit.getDescription());
        savedVisit.setPet(newVisit.getPet());
        savedVisit.setVeterinarian(newVisit.getVeterinarian());

        when(visitService.saveVisit(any(Visit.class))).thenReturn(savedVisit);

        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newVisit)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(savedVisit.getId())))
                .andExpect(jsonPath("$.description", is(newVisit.getDescription())));

        verify(visitService, times(1)).saveVisit(any(Visit.class));
    }

    @Test
    void testUpdateVisit() throws Exception {
        Visit updatedVisit = new Visit();
        updatedVisit.setId(1);
        updatedVisit.setDate(LocalDate.of(2023, 1, 16));
        updatedVisit.setDescription("Updated checkup");
        updatedVisit.setPet(pet1);
        updatedVisit.setVeterinarian(vet1);

        when(visitService.findVisitById(1)).thenReturn(Optional.of(visit1));
        when(visitService.saveVisit(any(Visit.class))).thenReturn(updatedVisit);

        mockMvc.perform(put("/api/visits/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedVisit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedVisit.getId())))
                .andExpect(jsonPath("$.description", is(updatedVisit.getDescription())));

        verify(visitService, times(1)).findVisitById(1);
        verify(visitService, times(1)).saveVisit(any(Visit.class));
    }

    @Test
    void testUpdateVisit_notFound() throws Exception {
        Visit updatedVisit = new Visit();
        updatedVisit.setId(99);
        updatedVisit.setDate(LocalDate.of(2023, 1, 16));
        updatedVisit.setDescription("Updated checkup");
        updatedVisit.setPet(pet1);
        updatedVisit.setVeterinarian(vet1);

        when(visitService.findVisitById(99)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/visits/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedVisit)))
                .andExpect(status().isNotFound());

        verify(visitService, times(1)).findVisitById(99);
        verify(visitService, never()).saveVisit(any(Visit.class));
    }

    @Test
    void testDeleteVisit() throws Exception {
        when(visitService.findVisitById(1)).thenReturn(Optional.of(visit1));
        doNothing().when(visitService).deleteVisit(1);

        mockMvc.perform(delete("/api/visits/{id}", 1))
                .andExpect(status().isNoContent());

        verify(visitService, times(1)).findVisitById(1);
        verify(visitService, times(1)).deleteVisit(1);
    }

    @Test
    void testDeleteVisit_notFound() throws Exception {
        when(visitService.findVisitById(99)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/visits/{id}", 99))
                .andExpect(status().isNotFound());

        verify(visitService, times(1)).findVisitById(99);
        verify(visitService, never()).deleteVisit(anyInt());
    }

    @Test
    void testGetAllPets() throws Exception {
        when(petService.findAllPets()).thenReturn(Collections.singletonList(pet1));

        mockMvc.perform(get("/api/visits/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(pet1.getName())));
    }

    @Test
    void testGetAllOwners() throws Exception {
        when(ownerService.findAllOwners()).thenReturn(Collections.singletonList(owner1));

        mockMvc.perform(get("/api/visits/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is(owner1.getFirstName())));
    }

    @Test
    void testGetAllVeterinarians() throws Exception {
        when(veterinarianService.findAllVeterinarians()).thenReturn(Collections.singletonList(vet1));

        mockMvc.perform(get("/api/visits/veterinarians"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(vet1.getName())));
    }
}