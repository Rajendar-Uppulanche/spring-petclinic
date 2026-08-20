package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OwnerController.class)
class OwnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @MockBean
    private OwnerRepository ownerRepository; // Needed for @ModelAttribute("owner") in OwnerController
    @MockBean
    private PetTypeRepository petTypeRepository; // Needed for PetController, but not directly used here

    @Test
    void testShowOwnerDetailsWithVisitCounts() throws Exception {
        // Mock DTOs
        PetDetailsDTO pet1 = new PetDetailsDTO();
        pet1.setId(10);
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2020, 1, 1));
        pet1.setTypeName("cat");
        pet1.setVisitCount(2);

        PetDetailsDTO pet2 = new PetDetailsDTO();
        pet2.setId(11);
        pet2.setName("Max");
        pet2.setBirthDate(LocalDate.of(2019, 5, 15));
        pet2.setTypeName("dog");
        pet2.setVisitCount(0);

        OwnerDetailsDTO ownerDetails = new OwnerDetailsDTO();
        ownerDetails.setId(1);
        ownerDetails.setFirstName("George");
        ownerDetails.setLastName("Franklin");
        ownerDetails.setAddress("110 W. Liberty St.");
        ownerDetails.setCity("Madison");
        ownerDetails.setTelephone("6085551023");
        ownerDetails.setPets(Arrays.asList(pet1, pet2));

        when(ownerService.findOwnerDetailsById(anyInt())).thenReturn(ownerDetails);

        mockMvc.perform(get("/owners/{ownerId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownerDetails"))
                .andExpect(model().attributeExists("owner"))
                .andExpect(model().attribute("owner", ownerDetails));
    }

    @Test
    void testShowOwnerDetailsWithNoPets() throws Exception {
        OwnerDetailsDTO ownerDetails = new OwnerDetailsDTO();
        ownerDetails.setId(1);
        ownerDetails.setFirstName("George");
        ownerDetails.setLastName("Franklin");
        ownerDetails.setAddress("110 W. Liberty St.");
        ownerDetails.setCity("Madison");
        ownerDetails.setTelephone("6085551023");
        ownerDetails.setPets(Collections.emptyList());

        when(ownerService.findOwnerDetailsById(anyInt())).thenReturn(ownerDetails);

        mockMvc.perform(get("/owners/{ownerId}", 1))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownerDetails"))
                .andExpect(model().attributeExists("owner"))
                .andExpect(model().attribute("owner", ownerDetails));
    }

    @Test
    void testShowOwnerDetailsOwnerNotFound() throws Exception {
        when(ownerService.findOwnerDetailsById(anyInt())).thenThrow(new IllegalArgumentException("Owner not found"));

        mockMvc.perform(get("/owners/{ownerId}", 99))
                .andExpect(status().isBadRequest()); // Or 404 depending on error handling
    }
}
