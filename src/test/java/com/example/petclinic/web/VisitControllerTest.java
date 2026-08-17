package com.example.petclinic.web;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
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
import java.util.List;
import java.util.Optional;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitController.class)
public class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    private ObjectMapper objectMapper;

    private Visit visit1;
    private Visit visit2;
    private Visit visit3;
    private Pet pet1;
    private Pet pet2;
    private Owner owner1;
    private Veterinarian vet1;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        owner1 = new Owner();
        owner1.setId(1L);
        owner1.setFirstName("John");
        owner1.setLastName("Doe");

        pet1 = new Pet();
        pet1.setId(1L);
        pet1.setName("Buddy");
        pet1.setOwner(owner1);

        pet2 = new Pet();
        pet2.setId(2L);
        pet2.setName("Lucy");
        pet2.setOwner(owner1);

        vet1 = new Veterinarian();
        vet1.setId(1L);
        vet1.setFirstName("Dr. James");
        vet1.setLastName("Carter");

        visit1 = new Visit();
        visit1.setId(1L);
        visit1.setVisitDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);
        visit1.setVeterinarian(vet1);

        visit2 = new Visit();
        visit2.setId(2L);
        visit2.setVisitDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);
        visit2.setVeterinarian(vet1);

        visit3 = new Visit();
        visit3.setId(3L);
        visit3.setVisitDate(LocalDate.of(2023, 3, 10));
        visit3.setDescription("Dental cleaning");
        visit3.setPet(pet2);
        visit3.setVeterinarian(vet1);
    }

    @Test
    void testGetVisits_noFilters() throws Exception {
        when(visitService.findVisitsByCriteria(
            isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(Arrays.asList(visit1, visit2, visit3));

        mockMvc.perform(get("/api/visits")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].description", is("Routine checkup")));
        verify(visitService, times(1)).findVisitsByCriteria(
            isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        );
    }

    @Test
    void testGetVisits_withFilters() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Long petId = 1L;
        Long ownerId = 1L;
        Long veterinarianId = 1L;
        String descriptionKeyword = "checkup";

        when(visitService.findVisitsByCriteria(
            eq(startDate), eq(endDate), eq(petId), eq(ownerId), eq(veterinarianId), eq(descriptionKeyword)
        )).thenReturn(Collections.singletonList(visit1));

        mockMvc.perform(get("/api/visits")
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-01-31")
                .param("petId", "1")
                .param("ownerId", "1")
                .param("veterinarianId", "1")
                .param("descriptionKeyword", "checkup")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].description", is("Routine checkup")));
        verify(visitService, times(1)).findVisitsByCriteria(
            eq(startDate), eq(endDate), eq(petId), eq(ownerId), eq(veterinarianId), eq(descriptionKeyword)
        );
    }

    @Test
    void testGetVisitById() throws Exception {
        when(visitService.findById(1L)).thenReturn(Optional.of(visit1));

        mockMvc.perform(get("/api/visits/{visitId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description", is("Routine checkup")));
    }

    @Test
    void testCreateVisit() throws Exception {
        when(visitService.save(any(Visit.class))).thenReturn(visit1);

        mockMvc.perform(post("/api/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(visit1)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.description", is("Routine checkup")));
    }

    @Test
    void testUpdateVisit() throws Exception {
        when(visitService.findById(1L)).thenReturn(Optional.of(visit1));
        when(visitService.save(any(Visit.class))).thenReturn(visit1);

        mockMvc.perform(put("/api/visits/{visitId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(visit1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description", is("Routine checkup")));
    }

    @Test
    void testDeleteVisit() throws Exception {
        when(visitService.findById(1L)).thenReturn(Optional.of(visit1));
        doNothing().when(visitService).deleteById(1L);

        mockMvc.perform(delete("/api/visits/{visitId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }
}
