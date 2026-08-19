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
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the {@link OwnerController}
 *
 * @author improved by M. Isvy
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

	private static final int TEST_OWNER_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository ownerRepository;

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
		leo.setName("Leo");
		leo.setBirthDate(LocalDate.of(2010, 9, 7));
		PetType cat = new PetType();
		cat.setName("cat");
		leo.setType(cat);
		leo.setOwner(george);

		Visit visit1 = new Visit();
		visit1.setDate(LocalDate.now());
		visit1.setDescription("routine checkup");
		leo.addVisit(visit1);

		george.addPet(leo);

		given(this.ownerRepository.findById(TEST_OWNER_ID)).willReturn(Optional.of(george));
		given(this.ownerRepository.findByLastName(george.getLastName())).willReturn(Arrays.asList(george));
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new")).andExpect(status().isOk()).andExpect(model().attributeExists("owner"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("address", "123 Main St").param("city", "Anytown").param("telephone", "0123456789"))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/owners/null"));
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("city", "Anytown")).andExpect(status().isOk()).andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "address"))
				.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testInitUpdateOwnerForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(model().attributeExists("owner"))
				.andExpect(model().attribute("owner", george))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
				.param("lastName", "Franklin").param("address", "110 W. Liberty St.")
				.param("city", "Madison").param("telephone", "6085551023"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
				.param("lastName", "Franklin").param("city", "Madison"))
				.andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "address"))
				.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get("/owners/find")).andExpect(status().isOk()).andExpect(model().attributeExists("owner"))
				.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormSuccess() throws Exception {
		given(this.ownerRepository.findByLastName("")).willReturn(new ArrayList<Owner>(Arrays.asList(george)));
		mockMvc.perform(get("/owners")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormHasErrors() throws Exception {
		given(this.ownerRepository.findByLastName("")).willReturn(Collections.emptyList());
		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
				.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormMultipleOwners() throws Exception {
		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");
		given(this.ownerRepository.findByLastName("")).willReturn(Arrays.asList(george, owner2));

		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(model().attributeExists("selections"))
				.andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(model().attribute("owner", george))
				.andExpect(view().name("owners/ownerDetails"));
	}

	// New test for FR-082, FR-083, NFR-066: Verify visit count is available in the model for the view
	@Test
	void testShowOwnerVisitCount() throws Exception {
		// Setup a pet with 2 visits
		Owner ownerWithMultipleVisits = new Owner();
		ownerWithMultipleVisits.setId(2);
		ownerWithMultipleVisits.setFirstName("Betty");
		ownerWithMultipleVisits.setLastName("Davis");
		ownerWithMultipleVisits.setAddress("110 W. Liberty St.");
		ownerWithMultipleVisits.setCity("Madison");
		ownerWithMultipleVisits.setTelephone("6085551023");

		Pet basil = new Pet();
		basil.setName("Basil");
		basil.setBirthDate(LocalDate.of(2010, 9, 7));
		PetType dog = new PetType();
		dog.setName("dog");
		basil.setType(dog);
		basil.setOwner(ownerWithMultipleVisits);

		Visit visitA = new Visit();
		visitA.setDate(LocalDate.now().minusDays(10));
		visitA.setDescription("checkup A");
		basil.addVisit(visitA);

		Visit visitB = new Visit();
		visitB.setDate(LocalDate.now().minusDays(5));
		visitB.setDescription("checkup B");
		basil.addVisit(visitB);

		ownerWithMultipleVisits.addPet(basil);

		// Setup a pet with 0 visits
		Pet jewel = new Pet();
		jewel.setName("Jewel");
		jewel.setBirthDate(LocalDate.of(2011, 2, 1));
		jewel.setType(dog);
		jewel.setOwner(ownerWithMultipleVisits);
		// No visits added for Jewel

		ownerWithMultipleVisits.addPet(jewel);

		given(this.ownerRepository.findById(2)).willReturn(Optional.of(ownerWithMultipleVisits));

		mockMvc.perform(get("/owners/{ownerId}", 2))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("owner"))
				.andExpect(model().attribute("owner", ownerWithMultipleVisits))
				.andExpect(view().name("owners/ownerDetails"))
				.andExpect(model().attribute("owner", hasProperty("pets", hasSize(2))))
				.andExpect(model().attribute("owner", hasProperty("pets", hasItem(
						allOf(
								hasProperty("name", is("Basil")),
								hasProperty("visitCount", is(2)) // Verify Basil has 2 visits
							)
				))))
				.andExpect(model().attribute("owner", hasProperty("pets", hasItem(
						allOf(
								hasProperty("name", is("Jewel")),
								hasProperty("visitCount", is(0)) // Verify Jewel has 0 visits
							)
				))));
	}

}
