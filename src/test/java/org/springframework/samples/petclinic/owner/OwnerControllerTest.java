package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.owner.dto.PetVisitCountDto;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OwnerController.class)
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService; // Mock the service layer

    private Owner george;
    private PetVisitCountDto leoDto;
    private PetVisitCountDto maxDto;

    @BeforeEach
    void setup() {
        george = new Owner();
        george.setId(1);
        george.setFirstName("George");
        george.setLastName("Franklin");
        george.setAddress("110 W. Liberty St.");
        george.setCity("Madison");
        george.setTelephone("6085551023");

        leoDto = new PetVisitCountDto(1, "Leo", LocalDate.of(2000, 9, 7), "cat", 1);
        maxDto = new PetVisitCountDto(2, "Max", LocalDate.of(2007, 1, 1), "dog", 5);

        when(ownerService.findOwnerById(1)).thenReturn(Optional.of(george));
        when(ownerService.findPetsWithVisitCountsByOwnerId(1)).thenReturn(Arrays.asList(leoDto, maxDto));
    }

    @Test
    void testShowOwner() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}", 1))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(model().attribute("owner", george))
            .andExpect(model().attributeExists("petsWithVisitCounts"))
            .andExpect(model().attribute("petsWithVisitCounts", Arrays.asList(leoDto, maxDto)))
            .andExpect(view().name("owners/ownerDetails"));
    }

    @Test
    void testShowOwnerNotFound() throws Exception {
        when(ownerService.findOwnerById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/owners/{ownerId}", 999))
            .andExpect(status().isBadRequest()) // IllegalArgumentException leads to 400 Bad Request by default
            .andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    void testShowOwnerWithNoPets() throws Exception {
        Owner noPetsOwner = new Owner();
        noPetsOwner.setId(2);
        noPetsOwner.setFirstName("No");
        noPetsOwner.setLastName("Pets");
        when(ownerService.findOwnerById(2)).thenReturn(Optional.of(noPetsOwner));
        when(ownerService.findPetsWithVisitCountsByOwnerId(2)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/owners/{ownerId}", 2))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(model().attribute("owner", noPetsOwner))
            .andExpect(model().attributeExists("petsWithVisitCounts"))
            .andExpect(model().attribute("petsWithVisitCounts", Collections.emptyList()))
            .andExpect(view().name("owners/ownerDetails"));
    }
}