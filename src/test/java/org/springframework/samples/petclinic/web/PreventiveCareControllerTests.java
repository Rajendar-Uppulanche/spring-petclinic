package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.PreventiveCare;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.PreventiveCareService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PreventiveCareController.class)
class PreventiveCareControllerTests {

    private static final int TEST_OWNER_ID = 1;
    private static final int TEST_PET_ID = 1;
    private static final int TEST_PREVENTIVE_CARE_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @MockBean
    private PreventiveCareService preventiveCareService;

    private Pet pet;
    private Owner owner;

    @BeforeEach
    void setup() {
        owner = new Owner();
        owner.setId(TEST_OWNER_ID);
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        PetType cat = new PetType();
        cat.setId(1);
        cat.setName("cat");

        pet = new Pet();
        pet.setId(TEST_PET_ID);
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.of(2010, 9, 7));
        pet.setType(cat);
        pet.setOwner(owner);
        owner.addPet(pet);

        given(this.petService.findPetById(TEST_PET_ID)).willReturn(pet);
    }

    @Test
    void testInitNewPreventiveCareForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/preventivecares/new", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("preventiveCare"))
            .andExpect(view().name("pets/createOrUpdatePreventiveCareForm"));
    }

    @Test
    void testProcessNewPreventiveCareFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/preventivecares/new", TEST_OWNER_ID, TEST_PET_ID)
            .param("careDate", "2023/01/01")
            .param("type", "Vaccination")
            .param("description", "Rabies Vaccine")
            .param("notes", "Annual shot"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));

        verify(preventiveCareService).savePreventiveCare(org.mockito.ArgumentMatchers.any(PreventiveCare.class));
    }

    @Test
    void testProcessNewPreventiveCareFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/preventivecares/new", TEST_OWNER_ID, TEST_PET_ID)
            .param("careDate", "2023/01/01")
            .param("type", "") // Empty type
            .param("description", "Rabies Vaccine"))
            .andExpect(model().attributeHasErrors("preventiveCare"))
            .andExpect(model().attributeHasFieldErrors("preventiveCare", "type"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePreventiveCareForm"));
    }

    @Test
    void testInitUpdatePreventiveCareForm() throws Exception {
        PreventiveCare preventiveCare = new PreventiveCare();
        preventiveCare.setId(TEST_PREVENTIVE_CARE_ID);
        preventiveCare.setCareDate(LocalDate.of(2022, 10, 1));
        preventiveCare.setType("Treatment");
        preventiveCare.setDescription("Worming");
        given(this.preventiveCareService.findPreventiveCareById(TEST_PREVENTIVE_CARE_ID)).willReturn(preventiveCare);

        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/preventivecares/{preventiveCareId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_PREVENTIVE_CARE_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("preventiveCare"))
            .andExpect(model().attribute("preventiveCare", preventiveCare))
            .andExpect(view().name("pets/createOrUpdatePreventiveCareForm"));
    }

    @Test
    void testProcessUpdatePreventiveCareFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/preventivecares/{preventiveCareId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_PREVENTIVE_CARE_ID)
            .param("careDate", "2023/02/01")
            .param("type", "Vaccination")
            .param("description", "Updated Vaccine")
            .param("notes", "Updated notes"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));

        verify(preventiveCareService).savePreventiveCare(org.mockito.ArgumentMatchers.any(PreventiveCare.class));
    }

    @Test
    void testProcessUpdatePreventiveCareFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/preventivecares/{preventiveCareId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_PREVENTIVE_CARE_ID)
            .param("careDate", "2023/02/01")
            .param("type", "") // Empty type
            .param("description", "Updated Vaccine"))
            .andExpect(model().attributeHasErrors("preventiveCare"))
            .andExpect(model().attributeHasFieldErrors("preventiveCare", "type"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdatePreventiveCareForm"));
    }

    @Test
    void testDeletePreventiveCare() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/preventivecares/{preventiveCareId}/delete", TEST_OWNER_ID, TEST_PET_ID, TEST_PREVENTIVE_CARE_ID))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));

        verify(preventiveCareService).deletePreventiveCare(TEST_PREVENTIVE_CARE_ID);
    }
}
