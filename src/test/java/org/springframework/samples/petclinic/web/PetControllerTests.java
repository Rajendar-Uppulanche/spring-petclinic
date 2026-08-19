package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PetController.class)
class PetControllerTests {

    private static final int TEST_OWNER_ID = 1;
    private static final int TEST_PET_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClinicService clinicService;

    private Owner george;
    private PetType cat;
    private PetType dog;

    @BeforeEach
    void setup() {
        george = new Owner();
        george.setId(TEST_OWNER_ID);
        george.setFirstName("George");
        george.setLastName("Bush");
        george.setAddress("110 W. Liberty St.");
        george.setCity("Madison");
        george.setTelephone("6085551023");

        cat = new PetType();
        cat.setId(1);
        cat.setName("cat");

        dog = new PetType();
        dog.setId(2);
        dog.setName("dog");

        when(this.clinicService.findOwnerById(TEST_OWNER_ID)).thenReturn(george);
        when(this.clinicService.findPetTypes()).thenReturn(Arrays.asList(cat, dog));
        when(this.clinicService.findPetById(TEST_PET_ID)).thenReturn(new Pet());
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("pet"))
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("name", "Leo")
            .param("birthDate", LocalDate.now().minusYears(1).toString())
            .param("type", "cat"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    void testProcessCreationFormHasErrorsFutureBirthDate() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("name", "Leo")
            .param("birthDate", LocalDate.now().plusDays(1).toString())
            .param("type", "cat"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testProcessCreationFormHasErrorsEmptyName() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
            .param("name", "")
            .param("birthDate", LocalDate.now().minusYears(1).toString())
            .param("type", "cat"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testInitUpdateForm() throws Exception {
        Pet pet = new Pet();
        pet.setId(TEST_PET_ID);
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.now().minusYears(1));
        pet.setType(cat);
        george.addPet(pet);
        when(this.clinicService.findPetById(TEST_PET_ID)).thenReturn(pet);

        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("pet"))
            .andExpect(model().attribute("pet", pet))
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        when(this.clinicService.findPetById(TEST_PET_ID)).thenReturn(new Pet());
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
            .param("name", "LeoUpdated")
            .param("birthDate", LocalDate.now().minusYears(2).toString())
            .param("type", "dog"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    void testProcessUpdateFormHasErrorsFutureBirthDate() throws Exception {
        when(this.clinicService.findPetById(TEST_PET_ID)).thenReturn(new Pet());
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
            .param("name", "LeoUpdated")
            .param("birthDate", LocalDate.now().plusDays(1).toString())
            .param("type", "dog"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testProcessUpdateFormHasErrorsEmptyName() throws Exception {
        when(this.clinicService.findPetById(TEST_PET_ID)).thenReturn(new Pet());
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
            .param("name", "")
            .param("birthDate", LocalDate.now().minusYears(2).toString())
            .param("type", "dog"))
            .andExpect(model().attributeHasErrors("pet"))
            .andExpect(model().attributeHasFieldErrors("pet", "name"))
            .andExpect(view().name("pets/createOrUpdatePetForm"));
    }
}