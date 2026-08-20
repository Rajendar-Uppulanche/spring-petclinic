package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
class PetControllerTests {

    private static final int TEST_OWNER_ID = 1;
    private static final int TEST_PET_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository ownerRepository;

    @MockBean
    private PetTypeRepository petTypeRepository;

    private Owner george;
    private PetType cat;

    @BeforeEach
    void setup() {
        george = new Owner();
        george.setId(TEST_OWNER_ID);
        george.setFirstName("George");
        george.setLastName("Bush");

        cat = new PetType();
        cat.setId(1);
        cat.setName("cat");

        Pet pet = new Pet();
        pet.setId(TEST_PET_ID);
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        pet.setType(cat);
        george.addPet(pet);

        when(ownerRepository.findById(anyInt())).thenReturn(Optional.of(george));
        when(petTypeRepository.findPetTypes()).thenReturn(java.util.Arrays.asList(cat));
    }

    @Test
    void testProcessCreationFormWithFutureBirthDate() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
                .param("name", "FuturePet")
                .param("birthDate", LocalDate.now().plusDays(1).toString())
                .param("type", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("pet"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
                .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testProcessUpdateFormWithFutureBirthDate() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
                .param("name", "Leo")
                .param("birthDate", LocalDate.now().plusDays(1).toString())
                .param("type", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("pet"))
                .andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
                .andExpect(view().name("pets/createOrUpdatePetForm"));
    }

    @Test
    void testProcessCreationFormWithValidBirthDate() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
                .param("name", "ValidPet")
                .param("birthDate", LocalDate.now().minusDays(1).toString())
                .param("type", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/" + TEST_OWNER_ID));
    }

    @Test
    void testProcessUpdateFormWithValidBirthDate() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
                .param("name", "LeoUpdated")
                .param("birthDate", LocalDate.now().minusDays(1).toString())
                .param("type", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/owners/" + TEST_OWNER_ID));
    }
}
