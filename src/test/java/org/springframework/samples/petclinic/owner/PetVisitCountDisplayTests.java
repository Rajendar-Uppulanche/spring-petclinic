package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OwnerController.class)
class PetVisitCountDisplayTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private Owner george;
    private Pet pet1;
    private Pet pet2;

    @BeforeEach
    void setup() {
        george = new Owner();
        george.setId(1);
        george.setFirstName("George");
        george.setLastName("Franklin");
        george.setAddress("110 W. Liberty St.");
        george.setCity("Madison");
        george.setTelephone("6085551023");

        PetType cat = new PetType();
        cat.setName("cat");

        pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2000, 9, 7));
        pet1.setType(cat);
        george.addPet(pet1);

        pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Basil");
        pet2.setBirthDate(LocalDate.of(2002, 8, 6));
        pet2.setType(cat);
        george.addPet(pet2);

        Visit visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.now());
        visit1.setDescription("Routine checkup");
        pet1.addVisit(visit1);

        Visit visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.now().minusDays(10));
        visit2.setDescription("Vaccination");
        pet1.addVisit(visit2);

        // pet2 has no visits
    }

    @Test
    void testShowOwnerWithPetVisits() throws Exception {
        given(this.owners.findById(1)).willReturn(Optional.of(george));

        mockMvc.perform(get("/owners/{ownerId}", 1))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/ownerDetails"))
            .andExpect(content().string(containsString("Leo (2 visits)"))) // Pet with 2 visits
            .andExpect(content().string(containsString("Basil (0 visits)"))) // Pet with 0 visits
            .andExpect(content().string(containsString("style=\"color: #6C757D; font-style: italic;\""))); // Styling check
    }

    @Test
    void testShowOwnerWithNoPets() throws Exception {
        Owner ownerWithoutPets = new Owner();
        ownerWithoutPets.setId(2);
        ownerWithoutPets.setFirstName("No");
        ownerWithoutPets.setLastName("Pets");
        ownerWithoutPets.setAddress("123 Main St");
        ownerWithoutPets.setCity("Anytown");
        ownerWithoutPets.setTelephone("1234567890");

        given(this.owners.findById(2)).willReturn(Optional.of(ownerWithoutPets));

        mockMvc.perform(get("/owners/{ownerId}", 2))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/ownerDetails"))
            .andExpect(content().string(containsString("Pets and Visits")));
    }

    @Test
    void testShowOwnerWithPetHavingNoVisitsExplicitly() throws Exception {
        given(this.owners.findById(1)).willReturn(Optional.of(george));

        mockMvc.perform(get("/owners/{ownerId}", 1))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Basil (0 visits)")));
    }
}
