package com.example.petclinic.web;

import com.example.petclinic.model.Owner;
import com.example.petclinic.service.ClinicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for the {@link OwnerController}
 *
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

    private static final int TEST_OWNER_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClinicService clinicService;

    private Owner george;

    @BeforeEach
    void setup() {
        george = new Owner();
        george.setId(TEST_OWNER_ID);
        george.setFirstName("George");
        george.setLastName("Franklin");
        george.setAddress("110 W. Liberty St.");
        george.setCity("Madison");
        george.setTelephone("6085551023");
        given(this.clinicService.findOwnerById(TEST_OWNER_ID)).willReturn(george);
    }

    @Test
    void testInitCreationForm() throws Exception {
        mockMvc.perform(get("/owners/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testProcessCreationFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/new")
                .param("firstName", "Joe")
                .param("lastName", "Bloggs")
                .param("address", "123 Main St")
                .param("city", "Anytown")
                .param("telephone", "1234567890") // Valid telephone
                .with(request -> {
                    request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    return request;
                }))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/null")); // Assuming ID is generated after save
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/new")
                .param("firstName", "Joe")
                .param("lastName", "Bloggs")
                .param("address", "123 Main St")
                .param("city", "Anytown")
                .param("telephone", "invalid") // Invalid telephone
                .with(request -> {
                    request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testInitFindForm() throws Exception {
        mockMvc.perform(get("/owners/find"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void testProcessFindFormSuccess() throws Exception {
        given(this.clinicService.findOwnerByLastName("")).willReturn(new java.util.ArrayList<Owner>());
        given(this.clinicService.findOwnerByLastName("Franklin")).willReturn(java.util.Arrays.asList(george));

        mockMvc.perform(get("/owners")
                .param("lastName", "Franklin"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
    }

    @Test
    void testProcessFindFormManyOwners() throws Exception {
        Owner owner = new Owner();
        owner.setLastName("Franklin");
        given(this.clinicService.findOwnerByLastName(owner.getLastName())).willReturn(java.util.Arrays.asList(george, new Owner()));

        mockMvc.perform(get("/owners")
                .param("lastName", "Franklin"))
                .andExpect(status().isOk())
                .andExpect(view().name("owners/ownersList"));
    }

    @Test
    void testProcessFindFormNoOwners() throws Exception {
        given(this.clinicService.findOwnerByLastName("Nonexistent")).willReturn(new java.util.ArrayList<Owner>());

        mockMvc.perform(get("/owners")
                .param("lastName", "Nonexistent"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "lastName"))
                .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void testInitUpdateOwnerForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("owner"))
                .andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
                .andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testProcessUpdateOwnerFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
                .param("firstName", "George")
                .param("lastName", "Franklin")
                .param("address", "110 W. Liberty St.")
                .param("city", "Madison")
                .param("telephone", "6085551023") // Valid telephone
                .with(request -> {
                    request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    return request;
                }))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    void testProcessUpdateOwnerFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
                .param("firstName", "George")
                .param("lastName", "Franklin")
                .param("address", "110 W. Liberty St.")
                .param("city", "Madison")
                .param("telephone", "invalid") // Invalid telephone
                .with(request -> {
                    request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors("owner"))
                .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
                .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testShowOwner() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
                .andExpect(status().isOk())
                .andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
                .andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
                .andExpect(view().name("owners/ownerDetails"));
    }

}