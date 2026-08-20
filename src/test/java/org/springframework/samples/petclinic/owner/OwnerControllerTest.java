package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.visit.Visit;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link OwnerController} 
 */
@WebMvcTest(OwnerController.class)
class OwnerControllerTest {

	private static final int TEST_OWNER_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository owners;

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

		Pet max = new Pet();
		PetType dog = new PetType();
		dog.setName("dog");
		max.setId(1);
		max.setType(dog);
		max.setName("Max");
		max.setBirthDate(LocalDate.now());
		george.addPet(max);

		Pet fluffy = new Pet();
		PetType cat = new PetType();
		cat.setName("cat");
		fluffy.setId(2);
		fluffy.setType(cat);
		fluffy.setName("Fluffy");		
		fluffy.setBirthDate(LocalDate.now().minusYears(1));
		george.addPet(fluffy);

		Visit visit1 = new Visit();
		visit1.setDate(LocalDate.now());
		visit1.setDescription("routine checkup");
		max.addVisit(visit1);

		Visit visit2 = new Visit();
		visit2.setDate(LocalDate.now().plusDays(1));
		visit2.setDescription("vaccination");
		max.addVisit(visit2);

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);

		List<Object[]> petsWithVisitCounts = new ArrayList<>();
		petsWithVisitCounts.add(new Object[]{max, 2L});
		petsWithVisitCounts.add(new Object[]{fluffy, 0L});
		given(this.owners.findPetsWithVisitCountsByOwnerId(TEST_OWNER_ID)).willReturn(petsWithVisitCounts);

	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new")).andExpect(status().isOk()).andExpect(model().attributeExists("owner"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("address", "123 Caramel Street").param("city", "London").param("telephone", "01316761638"))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("city", "London")).andExpect(status().isOk()).andExpect(model().attributeHasErrors("owner"))
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
		given(this.owners.findByLastName("")).willReturn(Lists.newArrayList(george));
		mockMvc.perform(get("/owners")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormWithName() throws Exception {
		given(this.owners.findByLastName(george.getLastName())).willReturn(Lists.newArrayList(george));
		mockMvc.perform(get("/owners").param("lastName", "Franklin")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormNoOwnersFound() throws Exception {
		given(this.owners.findByLastName("Unknown")).willReturn(Collections.emptyList());
		mockMvc.perform(get("/owners").param("lastName", "Unknown")).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner")).andExpect(model().attributeHasFieldErrors("owner", "lastName"))
				.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormManyOwnersFound() throws Exception {
		Owner owner = new Owner();
		owner.setLastName("Franklin");
		given(this.owners.findByLastName(owner.getLastName()))
				.willReturn(Lists.newArrayList(george, new Owner()));

		mockMvc.perform(get("/owners").param("lastName", "Franklin")).andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList")).andExpect(model().attributeExists("selections"));
	}

	@Test
	void testInitUpdateOwnerForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(model().attributeExists("owner"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
				.param("lastName", "Franklin").param("address", "110 W. Liberty St.").param("city", "Madison")
				.param("telephone", "6085551023")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
				.param("lastName", "Franklin").param("city", "Madison")).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "address"))
				.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(model().attribute("owner", george)).andExpect(view().name("owners/ownerDetails"));
	}

	@Test
	void testShowOwnerWithPetVisitCounts() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("owner"))
				.andExpect(model().attribute("owner", george))
				.andExpect(view().name("owners/ownerDetails"))
				.andExpect(model().attribute("owner", org.hamcrest.Matchers.hasProperty("pets", org.hamcrest.Matchers.hasSize(2))))
				.andExpect(model().attribute("owner", org.hamcrest.Matchers.hasProperty("pets", org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.allOf(
						org.hamcrest.Matchers.hasProperty("name", org.hamcrest.Matchers.is("Max")),
						org.hamcrest.Matchers.hasProperty("visitCount", org.hamcrest.Matchers.is(2))
				)))))
				.andExpect(model().attribute("owner", org.hamcrest.Matchers.hasProperty("pets", org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.allOf(
						org.hamcrest.Matchers.hasProperty("name", org.hamcrest.Matchers.is("Fluffy")),
						org.hamcrest.Matchers.hasProperty("visitCount", org.hamcrest.Matchers.is(0))
				)))));
	}

}
