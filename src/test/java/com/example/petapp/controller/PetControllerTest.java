package com.example.petapp.controller;

import com.example.petapp.model.Pet;
import com.example.petapp.service.PetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PetController.class)
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // For LocalDate serialization
    }

    @Test
    void testCreatePet_validPet() throws Exception {
        Pet pet = new Pet("Buddy", LocalDate.of(2020, 1, 1));
        pet.setId(1L);

        when(petService.savePet(any(Pet.class))).thenReturn(pet);

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Buddy")))
                .andExpect(jsonPath("$.birthDate", is("2020-01-01")));
    }

    @Test
    void testCreatePet_futureBirthDate_shouldReturnBadRequest() throws Exception {
        Pet pet = new Pet("FutureDog", LocalDate.now().plusDays(1));

        // The controller's @Valid annotation will trigger validation before service call
        // and the @ExceptionHandler will format the error.
        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthDate", is("Pet birth date cannot be in the future.")));
    }

    @Test
    void testCreatePet_nullBirthDate_shouldReturnBadRequest() throws Exception {
        Pet pet = new Pet("NoDateDog", null);

        mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthDate", is("Pet birth date cannot be null.")));
    }

    @Test
    void testUpdatePet_futureBirthDate_shouldReturnBadRequest() throws Exception {
        Pet existingPet = new Pet("OldDog", LocalDate.of(2010, 5, 10));
        existingPet.setId(1L);
        when(petService.findPetById(1L)).thenReturn(Optional.of(existingPet));

        Pet updatedPetWithFutureDate = new Pet("OldDog", LocalDate.now().plusDays(1));
        updatedPetWithFutureDate.setId(1L);

        mockMvc.perform(put("/api/pets/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPetWithFutureDate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthDate", is("Pet birth date cannot be in the future.")));
    }
}