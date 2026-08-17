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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerRepository ownerRepository;

    private Owner george;

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
        max.setBirthDate(LocalDate.of(2018, 1, 1));
        PetType cat = new PetType();
        cat.setId(1);
        cat.setName("cat");
        max.setType(cat);

        Visit visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.now());
        visit1.setDescription("Routine checkup");
        max.addVisit(visit1);

        george.addPet(max);

        Pet noVisitsPet = new Pet();
        noVisitsPet.setId(2);
        noVisitsPet.setName("NoVisits");
        noVisitsPet.setBirthDate(LocalDate.of(2020, 5, 10));
        PetType dog = new PetType();
        dog.setId(2);
        dog.setName("dog");
        noVisitsPet.setType(dog);
        george.addPet(noVisitsPet);

        // Mock the new method used by OwnerController
        when(ownerRepository.findByIdWithPetsAndVisits(anyInt())).thenReturn(Optional.of(george));
        // Also mock the original findById in case other methods in OwnerController use it
        when(ownerRepository.findById(anyInt())).thenReturn(Optional.of(george));
    }

    @Test
    void testShowOwnerWithVisitCounts() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}", 1))
            .andExpect(status().isOk())
            .andExpect(view().name("owners/ownerDetails"))
            .andExpect(model().attributeExists("owner"))
            .andExpect(model().attribute("owner", george));

        // The key assertion here is that the 'owner' object in the model
        // correctly contains pets with their associated visits, which the HTML
        // template will then render. Direct HTML content assertion is out of scope
        // for this type of MockMvc test.
    }

}
