package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.owner.VisitService;
import org.springframework.samples.petclinic.owner.VisitStatus;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VisitRestController.class)
class VisitRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    private Owner owner1;
    private Pet pet1;
    private Vet vet1;
    private Visit visit1;
    private Visit visit2;

    @BeforeEach
    void setUp() {
        owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");

        pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setOwner(owner1);

        vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("James");
        vet1.setLastName("Carter");

        visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);
        visit1.setVeterinarian(vet1);
        visit1.setStatus(VisitStatus.SCHEDULED);

        visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.of(2023, 1, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);
        visit2.setVeterinarian(vet1);
        visit2.setStatus(VisitStatus.COMPLETED);
    }

    @Test
    void getFilteredVisits_noFilters() throws Exception {
        when(visitService.findVisits(null, null, null, null, null, null))
                .thenReturn(Arrays.asList(visit1, visit2));

        mockMvc.perform(get("/api/visits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getFilteredVisits_byOwnerId() throws Exception {
        when(visitService.findVisits(eq(owner1.getId()), any(), any(), any(), any(), any()))
                .thenReturn(Arrays.asList(visit1, visit2));

        mockMvc.perform(get("/api/visits").param("ownerId", String.valueOf(owner1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getFilteredVisits_byPetId() throws Exception {
        when(visitService.findVisits(any(), eq(pet1.getId()), any(), any(), any(), any()))
                .thenReturn(Arrays.asList(visit1, visit2));

        mockMvc.perform(get("/api/visits").param("petId", String.valueOf(pet1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getFilteredVisits_byVeterinarianId() throws Exception {
        when(visitService.findVisits(any(), any(), eq(vet1.getId()), any(), any(), any()))
                .thenReturn(Arrays.asList(visit1, visit2));

        mockMvc.perform(get("/api/visits").param("veterinarianId", String.valueOf(vet1.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getFilteredVisits_byStatus() throws Exception {
        when(visitService.findVisits(any(), any(), any(), any(), any(), eq(VisitStatus.SCHEDULED)))
                .thenReturn(Collections.singletonList(visit1));

        mockMvc.perform(get("/api/visits").param("status", VisitStatus.SCHEDULED.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getFilteredVisits_byDateRange() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 10);
        LocalDate endDate = LocalDate.of(2023, 1, 16);
        when(visitService.findVisits(any(), any(), any(), eq(startDate), eq(endDate), any()))
                .thenReturn(Collections.singletonList(visit1));

        mockMvc.perform(get("/api/visits")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getFilteredVisits_combinedFilters() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 10);
        LocalDate endDate = LocalDate.of(2023, 1, 25);
        when(visitService.findVisits(eq(owner1.getId()), eq(pet1.getId()), eq(vet1.getId()), eq(startDate), eq(endDate), eq(VisitStatus.COMPLETED)))
                .thenReturn(Collections.singletonList(visit2));

        mockMvc.perform(get("/api/visits")
                        .param("ownerId", String.valueOf(owner1.getId()))
                        .param("petId", String.valueOf(pet1.getId()))
                        .param("veterinarianId", String.valueOf(vet1.getId()))
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("status", VisitStatus.COMPLETED.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void getFilteredVisits_noResults() throws Exception {
        when(visitService.findVisits(any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/visits").param("ownerId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
