package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.service.OwnerService;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.repository.PetRepository; // Import for PetWithVisitCount
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link OwnerController}
 *
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@WebMvcTest(OwnerController.class)
class OwnerControllerTest {

    private static final int TEST_OWNER_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @MockBean // Mock PetService
    private PetService petService;

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

        Pet leo = new Pet();
        leo.setId(1);
        leo.setName("Leo");
        leo.setBirthDate(LocalDate.of(2010, 9, 7));
        PetType cat = new PetType();
        cat.setId(1);
        cat.setName("cat");
        leo.setType(cat);
        leo.setOwner(george);
        Visit visit1 = new Visit();
        visit1.setId(1);
        visit1.setPet(leo);
        visit1.setDate(LocalDate.now());
        visit1.setDescription("routine checkup");
        Visit visit2 = new Visit();
        visit2.setId(2);
        visit2.setPet(leo);
        visit2.setDate(LocalDate.now().minusDays(10));
        visit2.setDescription("vaccination");
        leo.addVisit(visit1);
        leo.addVisit(visit2);

        Pet basil = new Pet();
        basil.setId(2);
        basil.setName("Basil");
        basil.setBirthDate(LocalDate.of(2012, 9, 6));
        PetType dog = new PetType();
        dog.setId(2);
        dog.setName("dog");
        basil.setType(dog);
        basil.setOwner(george);

        george.addPet(leo);
        george.addPet(basil);

        given(this.ownerService.findOwnerById(TEST_OWNER_ID)).willReturn(george);
        given(this.ownerService.findOwnerByLastName("Franklin")).willReturn(Arrays.asList(george));
        given(this.ownerService.findOwnerByLastName("")).willReturn(Arrays.asList(george));

        // Mock the new petService method
        List<PetRepository.PetWithVisitCount> mockPetsWithVisitCounts = new ArrayList<>();
        mockPetsWithVisitCounts.add(new PetRepository.PetWithVisitCount(leo, 2L));
        mockPetsWithVisitCounts.add(new PetRepository.PetWithVisitCount(basil, 0L));
        given(this.petService.findPetsByOwnerIdWithVisitCount(TEST_OWNER_ID)).willReturn(mockPetsWithVisitCounts);
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
            .param("telephone", "0123456789")
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/null")); // owner.id is null before save in mock
    }

    @Test
    void testProcessCreationFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/new")
            .param("firstName", "Joe")
            .param("lastName", "Bloggs")
            .param("city", "Anytown")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("owner"))
            .andExpect(model().attributeHasFieldErrors("owner", "address"))
            .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testInitUpdateOwnerForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(model().attribute("owner", george))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testProcessUpdateOwnerFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
            .param("firstName", "George")
            .param("lastName", "Franklin")
            .param("address", "110 W. Liberty St.")
            .param("city", "Madison")
            .param("telephone", "6085551023")
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"));
    }

    @Test
    void testProcessUpdateOwnerFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
            .param("firstName", "George")
            .param("lastName", "Franklin")
            .param("city", "Madison")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeHasErrors("owner"))
            .andExpect(model().attributeHasFieldErrors("owner", "address"))
            .andExpect(model().attributeHasFieldErrors("owner", "telephone"))
            .andExpect(view().name("owners/createOrUpdateOwnerForm"));
    }

    @Test
    void testProcessFindFormSuccess() throws Exception {
        mockMvc.perform(get("/owners.html")
            .param("lastName", "Franklin")
        )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
    }

    @Test
    void testProcessFindFormNoOwnersFound() throws Exception {
        given(this.ownerService.findOwnerByLastName("Unknown")).willReturn(Collections.emptyList());
        mockMvc.perform(get("/owners.html")
            .param("lastName", "Unknown")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeHasFieldErrors("owner", "lastName"))
            .andExpect(model().attribute("owner", new Owner()))
            .andExpect(view().name("owners/findOwners"));
    }

    @Test
    void testProcessFindFormManyOwnersFound() throws Exception {
        Owner owner2 = new Owner();
        owner2.setId(2);
        owner2.setFirstName("Jane");
        owner2.setLastName("Franklin");
        given(this.ownerService.findOwnerByLastName("Franklin")).willReturn(Arrays.asList(george, owner2));

        mockMvc.perform(get("/owners.html")
            .param("lastName", "Franklin")
        )
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("selections"))
            .andExpect(view().name("owners/ownersList"));
    }

    @Test
    void testShowOwner() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
            .andExpect(status().isOk())
            .andExpect(model().attribute("owner", george))
            .andExpect(model().attributeExists("petsWithVisitCounts")) // Verify new attribute
            .andExpect(view().name("owners/ownerDetails"));
    }

}