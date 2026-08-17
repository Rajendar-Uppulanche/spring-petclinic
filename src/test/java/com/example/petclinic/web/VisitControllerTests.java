package com.example.petclinic.web;

import com.example.petclinic.model.Veterinarian;
import com.example.petclinic.model.Visit;
import com.example.petclinic.service.VisitService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitController.class)
class VisitControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    private Visit visit1;
    private Visit visit2;
    private Veterinarian vet1;

    @BeforeEach
    void setUp() {
        vet1 = new Veterinarian("Dr. Smith", "smith@example.com");
        vet1.setId(1);

        visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setVeterinarian(vet1);

        visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setVeterinarian(vet1);
    }

    @Test
    void testGetFilteredVisits_noFilters() throws Exception {
        when(visitService.findVisits(any(), any(), any(), any(), any(), any()))
                .thenReturn(Arrays.asList(visit1, visit2));

        mockMvc.perform(get("/api/visits/filtered")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void testGetFilteredVisits_byVeterinarianId() throws Exception {
        when(visitService.findVisits(any(), any(), eq(vet1.getId()), any(), any(), any()))
                .thenReturn(Arrays.asList(visit1, visit2));

        mockMvc.perform(get("/api/visits/filtered")
                .param("veterinarianId", String.valueOf(vet1.getId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].veterinarian.id").value(vet1.getId()));
    }

    @Test
    void testGetFilteredVisits_byDateRange() throws Exception {
        when(visitService.findVisits(any(), any(), any(), eq(LocalDate.of(2023, 1, 1)), eq(LocalDate.of(2023, 1, 31)), any()))
                .thenReturn(Collections.singletonList(visit1));

        mockMvc.perform(get("/api/visits/filtered")
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-01-31")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testGetFilteredVisits_noContent() throws Exception {
        when(visitService.findVisits(any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/visits/filtered")
                .param("petId", "999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}