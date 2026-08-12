package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VisitController.class)
public class VisitControllerTests {

    private static final int TEST_OWNER_ID = 1;
    private static final int TEST_PET_ID = 1;
    private static final int TEST_VISIT_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository ownerRepository;

    @MockBean
    private VisitRepository visitRepository;

    private Owner george;
    private Pet pet;
    private Visit visit;

    @BeforeEach
    void setup() {
        george = new Owner();
        george.setId(TEST_OWNER_ID);
        george.setFirstName("George");
        george.setLastName("Franklin");

        pet = new Pet();
        pet.setId(TEST_PET_ID);
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.of(2000, 1, 1));
        pet.setType(new PetType());
        pet.getType().setName("cat");
        george.addPet(pet);

        visit = new Visit();
        visit.setId(TEST_VISIT_ID);
        visit.setDate(LocalDate.now().plusDays(1));
        visit.setDescription("Routine checkup");
        visit.setStatus(VisitStatus.SCHEDULED);
        visit.setPet(pet);
        pet.addVisit(visit);

        when(ownerRepository.findById(TEST_OWNER_ID)).thenReturn(Optional.of(george));
        when(visitRepository.findById(TEST_VISIT_ID)).thenReturn(Optional.of(visit));
        when(ownerRepository.save(any(Owner.class))).thenReturn(george);
        when(visitRepository.save(any(Visit.class))).thenReturn(visit);
    }

    @Test
    void testInitNewVisitForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("visit"))
            .andExpect(model().attribute("visit", org.hamcrest.Matchers.hasProperty("status", org.hamcrest.Matchers.is(VisitStatus.SCHEDULED))))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

    @Test
    void testProcessNewVisitFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
                .param("date", LocalDate.now().plusDays(2).toString())
                .param("description", "New visit description"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/owners/" + TEST_OWNER_ID))
            .andExpect(flash().attributeExists("message"));
    }

    @Test
    void testProcessNewVisitFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
                .param("date", LocalDate.now().minusDays(1).toString()) // Invalid date
                .param("description", "")) // Empty description
            .andExpect(model().attributeHasErrors("visit"))
            .andExpect(model().attributeHasFieldErrors("visit", "date"))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

    @Test
    void testInitUpdateVisitForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("visit"))
            .andExpect(model().attribute("visit", org.hamcrest.Matchers.hasProperty("id", org.hamcrest.Matchers.is(TEST_VISIT_ID))))
            .andExpect(model().attribute("visit", org.hamcrest.Matchers.hasProperty("status", org.hamcrest.Matchers.is(VisitStatus.SCHEDULED))))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

    @Test
    void testProcessUpdateVisitFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
                .param("date", LocalDate.now().plusDays(3).toString())
                .param("description", "Updated description")
                .param("status", VisitStatus.IN_PROGRESS.name()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/owners/" + TEST_OWNER_ID))
            .andExpect(flash().attributeExists("message"));
    }

    @Test
    void testProcessUpdateVisitFormInvalidTransition() throws Exception {
        // Try to transition from SCHEDULED to COMPLETED directly (invalid)
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
                .param("date", LocalDate.now().plusDays(3).toString())
                .param("description", "Updated description")
                .param("status", VisitStatus.COMPLETED.name()))
            .andExpect(model().attributeHasErrors("visit"))
            .andExpect(model().attributeHasFieldErrors("visit", "status"))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

    @Test
    void testProcessUpdateVisitFormInvalidStatusValue() throws Exception {
        // Try to set an invalid status string
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
                .param("date", LocalDate.now().plusDays(3).toString())
                .param("description", "Updated description")
                .param("status", "INVALID_STATUS"))
            .andExpect(model().attributeHasErrors("visit"))
            .andExpect(model().attributeHasFieldErrors("visit", "status"))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

    @Test
    void testProcessUpdateVisitFormCompletedToOtherStatus() throws Exception {
        // Set visit to COMPLETED first
        visit.setStatus(VisitStatus.COMPLETED);
        when(visitRepository.findById(TEST_VISIT_ID)).thenReturn(Optional.of(visit));

        // Try to change from COMPLETED to IN_PROGRESS (invalid)
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
                .param("date", LocalDate.now().plusDays(3).toString())
                .param("description", "Updated description")
                .param("status", VisitStatus.IN_PROGRESS.name()))
            .andExpect(model().attributeHasErrors("visit"))
            .andExpect(model().attributeHasFieldErrors("visit", "status"))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }

    @Test
    void testProcessUpdateVisitFormCancelledToOtherStatus() throws Exception {
        // Set visit to CANCELLED first
        visit.setStatus(VisitStatus.CANCELLED);
        when(visitRepository.findById(TEST_VISIT_ID)).thenReturn(Optional.of(visit));

        // Try to change from CANCELLED to IN_PROGRESS (invalid)
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
                .param("date", LocalDate.now().plusDays(3).toString())
                .param("description", "Updated description")
                .param("status", VisitStatus.IN_PROGRESS.name()))
            .andExpect(model().attributeHasErrors("visit"))
            .andExpect(model().attributeHasFieldErrors("visit", "status"))
            .andExpect(view().name("pets/createOrUpdateVisitForm"));
    }
}
