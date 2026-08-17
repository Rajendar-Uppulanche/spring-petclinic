package com.example.petclinic.web;

import com.example.petclinic.model.Veterinarian;
import com.example.petclinic.model.Visit;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitController.class)
class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    private ObjectMapper objectMapper;

    private Visit visit1;
    private Visit visit2;
    private Veterinarian vet1;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        vet1 = new Veterinarian();
        vet1.setId(10);
        vet1.setName("Dr. Test Vet");

        visit1 = new Visit();
        visit1.setId(1);
        visit1.setVisitDate(LocalDate.of(2023, 1, 1));
        visit1.setDescription("Routine checkup");
        visit1.setVeterinarian(vet1);

        visit2 = new Visit();
        visit2.setId(2);
        visit2.setVisitDate(LocalDate.of(2023, 1, 15));
        visit2.setDescription("Vaccination");
        visit2.setVeterinarian(vet1);
    }

    @Test
    void getAllVisits_noFilters() throws Exception {
        when(visitService.findFilteredVisits(null, null, null, null, null, null))
                .thenReturn(Arrays.asList(visit1, visit2));

        mockMvc.perform(get("/api/visits")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description", is("Routine checkup")));
    }

    @Test
    void getAllVisits_withFilters() throws Exception {
        when(visitService.findFilteredVisits(eq(1), eq(1), eq(10), eq(LocalDate.of(2023, 1, 1)), eq(LocalDate.of(2023, 1, 31)), eq("checkup")))
                .thenReturn(Collections.singletonList(visit1));

        mockMvc.perform(get("/api/visits")
                .param("petId", "1")
                .param("ownerId", "1")
                .param("veterinarianId", "10")
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-01-31")
                .param("descriptionKeyword", "checkup")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is("Routine checkup")));
    }

    @Test
    void getVisitById() throws Exception {
        when(visitService.findVisitById(1)).thenReturn(Optional.of(visit1));

        mockMvc.perform(get("/api/visits/{visitId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Routine checkup")));
    }

    @Test
    void createVisit() throws Exception {
        when(visitService.saveVisit(any(Visit.class))).thenReturn(visit1);

        mockMvc.perform(post("/api/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(visit1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description", is("Routine checkup")));
    }

    @Test
    void updateVisit() throws Exception {
        when(visitService.findVisitById(1)).thenReturn(Optional.of(visit1));
        when(visitService.saveVisit(any(Visit.class))).thenReturn(visit1);

        mockMvc.perform(put("/api/visits/{visitId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(visit1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Routine checkup")));
    }

    @Test
    void deleteVisit() throws Exception {
        when(visitService.findVisitById(1)).thenReturn(Optional.of(visit1));

        mockMvc.perform(delete("/api/visits/{visitId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}