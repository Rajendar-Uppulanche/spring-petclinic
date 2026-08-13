package org.springframework.samples.petclinic.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.util.PetAgeCalculator;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OwnerController.class)
class OwnerControllerAgeDisplayTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository owners;

    private Owner george;
    private final LocalDate TEST_CURRENT_DATE = LocalDate.now(); // Use actual current date for test setup

    @BeforeEach
    void setup() {
        george = new Owner();
        george.setId(1);
        george.setFirstName("George");
        george.setLastName("Franklin");
        george.setAddress("110 W. Liberty St.");
        george.setCity("Madison");
        george.setTelephone("6085551023");

        Pet max = new Pet();
        max.setId(1);
        max.setName("Max");
        max.setBirthDate(TEST_CURRENT_DATE.minusYears(2).minusMonths(3)); // 2 years, 3 months
        PetType dog = new PetType();
        dog.setName("dog");
        max.setType(dog);
        george.addPet(max);

        Pet fluffy = new Pet();
        fluffy.setId(2);
        fluffy.setName("Fluffy");
        fluffy.setBirthDate(TEST_CURRENT_DATE.minusMonths(7)); // 7 months
        PetType cat = new PetType();
        cat.setName("cat");
        fluffy.setType(cat);
        george.addPet(fluffy);

        Pet tiny = new Pet();
        tiny.setId(3);
        tiny.setName("Tiny");
        tiny.setBirthDate(TEST_CURRENT_DATE.minusDays(15)); // < 1 month
        PetType hamster = new PetType();
        hamster.setName("hamster");
        tiny.setType(hamster);
        george.addPet(tiny);

        given(this.owners.findById(1)).willReturn(george);
    }

    @Test
    void testShowOwnerWithPetAges() throws Exception {
        // Calculate expected age strings based on TEST_CURRENT_DATE
        String maxAgeString = PetAgeCalculator.formatAge(TEST_CURRENT_DATE.minusYears(2).minusMonths(3), TEST_CURRENT_DATE);
        String fluffyAgeString = PetAgeCalculator.formatAge(TEST_CURRENT_DATE.minusMonths(7), TEST_CURRENT_DATE);
        String tinyAgeString = PetAgeCalculator.formatAge(TEST_CURRENT_DATE.minusDays(15), TEST_CURRENT_DATE);

        mockMvc.perform(get("/owners/{ownerId}", 1))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/ownerDetails"))
            .andExpect(content().string(containsString("Max")))
            .andExpect(content().string(containsString("dog")))
            .andExpect(content().string(containsString(max.getBirthDate().toString() + "<span class=\"pet-age\">" + maxAgeString + "</span>")))
            .andExpect(content().string(containsString(fluffy.getBirthDate().toString() + "<span class=\"pet-age\">" + fluffyAgeString + "</span>")))
            .andExpect(content().string(containsString(tiny.getBirthDate().toString() + "<span class=\"pet-age\">" + tinyAgeString + "</span>")))
            .andExpect(content().string(containsString("class=\"pet-age\""))); // Check for CSS class
    }

    @Test
    void testShowOwnerWithNoPets() throws Exception {
        Owner noPetsOwner = new Owner();
        noPetsOwner.setId(2);
        noPetsOwner.setFirstName("No");
        noPetsOwner.setLastName("Pets");
        given(this.owners.findById(2)).willReturn(noPetsOwner);

        mockMvc.perform(get("/owners/{ownerId}", 2))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("owner"))
            .andExpect(view().name("owners/ownerDetails"))
            .andExpect(content().string(containsString("No Pets")));
    }
}
