package org.springframework.samples.petclinic.owner;

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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link VisitController}
 */
@WebMvcTest(VisitController.class)
class VisitControllerTests {

    private static final int TEST_OWNER_ID = 1;
    private static final int TEST_PET_ID = 7;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitRepository visits;

    @MockBean
    private PetRepository pets;

    @MockBean
    private OwnerRepository owners;

    private Owner george;
    private Pet samantha;

    @BeforeEach
    void setup() {
        george = new Owner();
        george.setId(TEST_OWNER_ID);
        george.setFirstName("George");
        george.setLastName("Franklin");

        samantha = new Pet();
        samantha.setId(TEST_PET_ID);
        samantha.setName("Samantha");
        samantha.setBirthDate(LocalDate.of(2017, 1, 1));
        samantha.setType(new PetType());
        samantha.setOwner(george); // Ensure owner is set for pet

        given(this.pets.findById(TEST_PET_ID)).willReturn(samantha);
        given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);
    }

    @Test
    void testInitNewVisitForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("pet"))
            .andExpect(model().attributeExists("visit"))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

    @Test
    void testProcessNewVisitFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
            .param("date", "2023-01-01")
            .param("description", "Test Visit"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    void testProcessNewVisitFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
            .param("date", "2023-01-01")
            .param("description", "")) // Empty description will cause error
            .andExpect(model().attributeHasErrors("visit"))
            .andExpect(model().attributeHasFieldErrors("visit", "description"))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

    @Test
    void testShowVisits() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/visits", TEST_OWNER_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/ownerDetails"));
    }

    // New tests for filtered visits API endpoint
    @Test
    void testGetFilteredVisitsNoFilters() throws Exception {
        Visit visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(samantha);

        Visit visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(samantha);

        List<Visit> allVisits = Arrays.asList(visit1, visit2);
        given(visits.findByOwnerIdAndFilters(eq(TEST_OWNER_ID), isNull(), isNull(), isNull())).willReturn(allVisits);

        mockMvc.perform(get("/api/owners/{ownerId}/visits", TEST_OWNER_ID)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].description").value("Routine checkup"))
            .andExpect(jsonPath("$[1].description").value("Vaccination"));
    }

    @Test
    void testGetFilteredVisitsWithDateRange() throws Exception {
        Visit visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(samantha);

        List<Visit> filteredByDate = Collections.singletonList(visit1);
        given(visits.findByOwnerIdAndFilters(eq(TEST_OWNER_ID), eq(LocalDate.of(2023, 1, 1)), eq(LocalDate.of(2023, 1, 31)), isNull())).willReturn(filteredByDate);

        mockMvc.perform(get("/api/owners/{ownerId}/visits", TEST_OWNER_ID)
            .param("fromDate", "2023-01-01")
            .param("toDate", "2023-01-31")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].description").value("Routine checkup"));
    }

    @Test
    void testGetFilteredVisitsWithKeyword() throws Exception {
        Visit visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(samantha);

        List<Visit> filteredByKeyword = Collections.singletonList(visit1);
        given(visits.findByOwnerIdAndFilters(eq(TEST_OWNER_ID), isNull(), isNull(), eq("routine"))).willReturn(filteredByKeyword);

        mockMvc.perform(get("/api/owners/{ownerId}/visits", TEST_OWNER_ID)
            .param("keyword", "routine")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].description").value("Routine checkup"));
    }

    @Test
    void testGetFilteredVisitsWithInvalidDateRange() throws Exception {
        mockMvc.perform(get("/api/owners/{ownerId}/visits", TEST_OWNER_ID)
            .param("fromDate", "2023-02-01")
            .param("toDate", "2023-01-01")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("From Date cannot be after To Date."));
    }

    @Test
    void testGetFilteredVisitsNoResults() throws Exception {
        given(visits.findByOwnerIdAndFilters(anyInt(), any(), any(), any())).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/owners/{ownerId}/visits", TEST_OWNER_ID)
            .param("fromDate", "2025-01-01")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(0));
    }
}
