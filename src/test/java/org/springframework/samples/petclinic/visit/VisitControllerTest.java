package org.springframework.samples.petclinic.visit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(VisitController.class)
class VisitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VisitService visitService;

    private Visit visit1;
    private Visit visit2;
    private Pet pet1;
    private Owner owner1;

    @BeforeEach
    void setUp() {
        owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");

        PetType catType = new PetType();
        catType.setName("cat");

        pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2010, 9, 7));
        pet1.setType(catType);
        pet1.setOwner(owner1);

        visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);

        visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);
    }

    @Test
    void testInitFindForm() throws Exception {
        when(visitService.findAllVisits()).thenReturn(Arrays.asList(visit1, visit2));

        mockMvc.perform(get("/visits/search"))
            .andExpect(status().isOk())
            .andExpect(view().name("visits/visitsList"))
            .andExpect(model().attributeExists("visitFilterCriteria"))
            .andExpect(model().attribute("visits", Arrays.asList(visit1, visit2)));
    }

    @Test
    void testProcessFindFormWithFilters() throws Exception {
        when(visitService.findVisitsByCriteria(any(VisitFilterCriteria.class))).thenReturn(Collections.singletonList(visit1));

        mockMvc.perform(get("/visits/search/results")
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-01-31")
                .param("petName", "leo")
                .param("ownerLastName", "franklin"))
            .andExpect(status().isOk())
            .andExpect(view().name("visits/visitsList"))
            .andExpect(model().attributeExists("visitFilterCriteria"))
            .andExpect(model().attribute("visits", Collections.singletonList(visit1)));
    }

    @Test
    void testProcessFindFormNoResults() throws Exception {
        when(visitService.findVisitsByCriteria(any(VisitFilterCriteria.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/visits/search/results")
                .param("petName", "nonexistent"))
            .andExpect(status().isOk())
            .andExpect(view().name("visits/visitsList"))
            .andExpect(model().attributeExists("visitFilterCriteria"))
            .andExpect(model().attribute("visits", Collections.emptyList()));
    }

    @Test
    void testProcessFindFormClearFilters() throws Exception {
        when(visitService.findVisitsByCriteria(any(VisitFilterCriteria.class))).thenReturn(Arrays.asList(visit1, visit2));

        mockMvc.perform(get("/visits/search/results"))
            .andExpect(status().isOk())
            .andExpect(view().name("visits/visitsList"))
            .andExpect(model().attributeExists("visitFilterCriteria"))
            .andExpect(model().attribute("visits", Arrays.asList(visit1, visit2)));
    }
}