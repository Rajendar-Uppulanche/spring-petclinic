package com.example.petclinic.web;

import com.example.petclinic.model.Owner;
import com.example.petclinic.service.OwnerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Owner owner1;
    private Owner owner2;

    @BeforeEach
    void setUp() {
        owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");
        owner1.setAddress("110 W. Liberty St.");
        owner1.setCity("Madison");
        owner1.setTelephone("6085551023");
        owner1.setUserId("georgef");

        owner2 = new Owner();
        owner2.setId(2);
        owner2.setFirstName("Betty");
        owner2.setLastName("Davis");
        owner2.setAddress("638 Cardinal Ave.");
        owner2.setCity("Sun Prairie");
        owner2.setTelephone("6085551749");
        owner2.setUserId("bettyd");
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STAFF"})
    void shouldGetOwnerByUserId() throws Exception {
        when(ownerService.findOwnerByUserId("georgef")).thenReturn(Optional.of(owner1));

        mockMvc.perform(get("/owners/by-user-id/georgef"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("George"))
                .andExpect(jsonPath("$.userId").value("georgef"));

        verify(ownerService, times(1)).findOwnerByUserId("georgef");
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void shouldNotGetOwnerByUserIdForUnauthorizedUser() throws Exception {
        mockMvc.perform(get("/owners/by-user-id/georgef"))
                .andExpect(status().isForbidden());

        verify(ownerService, never()).findOwnerByUserId(anyString());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STAFF"})
    void shouldReturnNotFoundForNonExistingOwnerByUserId() throws Exception {
        when(ownerService.findOwnerByUserId(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/owners/by-user-id/nonexistent"))
                .andExpect(status().isNotFound());

        verify(ownerService, times(1)).findOwnerByUserId("nonexistent");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateOwnerWithUserId() throws Exception {
        Owner newOwner = new Owner();
        newOwner.setFirstName("New");
        newOwner.setLastName("Owner");
        newOwner.setAddress("123 Main St");
        newOwner.setCity("Anytown");
        newOwner.setTelephone("1112223333");
        newOwner.setUserId("newownerid");

        when(ownerService.saveOwner(any(Owner.class))).thenReturn(newOwner);

        mockMvc.perform(post("/owners").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOwner)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("newownerid"));

        verify(ownerService, times(1)).saveOwner(any(Owner.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotCreateOwnerWithDuplicateUserId() throws Exception {
        Owner newOwner = new Owner();
        newOwner.setFirstName("New");
        newOwner.setLastName("Owner");
        newOwner.setAddress("123 Main St");
        newOwner.setCity("Anytown");
        newOwner.setTelephone("1112223333");
        newOwner.setUserId("georgef"); // Duplicate userId

        when(ownerService.saveOwner(any(Owner.class))).thenThrow(new IllegalArgumentException("An owner with this user ID already exists."));

        mockMvc.perform(post("/owners").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOwner)))
                .andExpect(status().isConflict());

        verify(ownerService, times(1)).saveOwner(any(Owner.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateOwnerWithoutChangingUserId() throws Exception {
        Owner updatedOwner = new Owner();
        updatedOwner.setId(1);
        updatedOwner.setFirstName("UpdatedGeorge");
        updatedOwner.setLastName("Franklin");
        updatedOwner.setAddress("110 W. Liberty St.");
        updatedOwner.setCity("Madison");
        updatedOwner.setTelephone("6085551023");
        updatedOwner.setUserId("georgef"); // Same userId

        when(ownerService.findOwnerById(1)).thenReturn(Optional.of(owner1));
        when(ownerService.saveOwner(any(Owner.class))).thenReturn(updatedOwner);

        mockMvc.perform(put("/owners/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOwner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedGeorge"))
                .andExpect(jsonPath("$.userId").value("georgef"));

        verify(ownerService, times(1)).findOwnerById(1);
        verify(ownerService, times(1)).saveOwner(any(Owner.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotUpdateOwnerWithDifferentUserId() throws Exception {
        Owner updatedOwner = new Owner();
        updatedOwner.setId(1);
        updatedOwner.setFirstName("UpdatedGeorge");
        updatedOwner.setLastName("Franklin");
        updatedOwner.setAddress("110 W. Liberty St.");
        updatedOwner.setCity("Madison");
        updatedOwner.setTelephone("6085551023");
        updatedOwner.setUserId("newgeorgef"); // Different userId

        when(ownerService.findOwnerById(1)).thenReturn(Optional.of(owner1));
        when(ownerService.saveOwner(any(Owner.class))).thenThrow(new IllegalArgumentException("User ID cannot be changed once assigned."));

        mockMvc.perform(put("/owners/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOwner)))
                .andExpect(status().isConflict());

        verify(ownerService, times(1)).findOwnerById(1);
        verify(ownerService, times(1)).saveOwner(any(Owner.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldNotUpdateOwnerWithDuplicateUserIdForAnotherOwner() throws Exception {
        Owner updatedOwner = new Owner();
        updatedOwner.setId(1);
        updatedOwner.setFirstName("UpdatedGeorge");
        updatedOwner.setLastName("Franklin");
        updatedOwner.setAddress("110 W. Liberty St.");
        updatedOwner.setCity("Madison");
        updatedOwner.setTelephone("6085551023");
        updatedOwner.setUserId("bettyd"); // userId of owner2

        when(ownerService.findOwnerById(1)).thenReturn(Optional.of(owner1));
        when(ownerService.saveOwner(any(Owner.class))).thenThrow(new IllegalArgumentException("An owner with this user ID already exists."));

        mockMvc.perform(put("/owners/1").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOwner)))
                .andExpect(status().isConflict());

        verify(ownerService, times(1)).findOwnerById(1);
        verify(ownerService, times(1)).saveOwner(any(Owner.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteOwner() throws Exception {
        when(ownerService.findOwnerById(1)).thenReturn(Optional.of(owner1));
        doNothing().when(ownerService).deleteOwner(owner1);

        mockMvc.perform(delete("/owners/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(ownerService, times(1)).findOwnerById(1);
        verify(ownerService, times(1)).deleteOwner(owner1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenDeletingNonExistingOwner() throws Exception {
        when(ownerService.findOwnerById(99)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/owners/99").with(csrf()))
                .andExpect(status().isNotFound());

        verify(ownerService, times(1)).findOwnerById(99);
        verify(ownerService, never()).deleteOwner(any(Owner.class));
    }
}