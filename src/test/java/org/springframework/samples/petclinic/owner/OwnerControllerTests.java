package org.springframework.samples.petclinic.owner;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for {@link OwnerController}
 *
 * @author&lt;a href="mailto:michael.isvy@gmail.com"&gt;Michael Isvy&lt;/a&gt;
 * @author Brian Clozel
 */
@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

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
		max.setName("Max");		max.setBirthDate(LocalDate.now());
		george.setPetsInternal(new HashSet<>());
		george.addPet(max);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(george);
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new")).andExpect(status().isOk()).andExpect(model().attributeExists("owner"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("address", "123 Main St.").param("city", "Herndon").param("telephone", "7031231234"))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("city", "Herndon").param("telephone", "")).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner")).andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get("/owners/find")).andExpect(status().isOk()).andExpect(model().attributeExists("owner"))
				.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormSuccess() throws Exception {
		given(this.owners.findByLastName("")).willReturn(List.of(george));
		mockMvc.perform(get("/owners")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormHasErrors() throws Exception {
		mockMvc.perform(get("/owners").param("lastName", "")).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner")).andExpect(model().attributeHasFieldErrors("owner", "lastName"))
				.andExpect(view().name("owners/findOwners"));
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
		owner.setId(2);
		owner.setFirstName("Joe");
		owner.setLastName("Bloggs");
		given(this.owners.findByLastName("Franklin")).willReturn(List.of(george, owner));

		mockMvc.perform(get("/owners").param("lastName", "Franklin")).andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList")).andExpect(model().attribute("selections", hasProperty("size", is(2))));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
				.andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
				.andExpect(view().name("owners/ownerDetails"));
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
				.param("lastName", "Franklin").param("address", "110 W. Liberty St.").param("city", "Madison")
				.param("telephone", "")).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner")).andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testSearchOwnersByLastName() throws Exception {
		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");

		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("da"))
				.willReturn(List.of(owner2, george)); // Assuming 'george' also matches 'da' for testing purposes

		mockMvc.perform(get("/owners/search").param("lastName", "da"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("selections"))
				.andExpect(model().attribute("selections", hasProperty("size", is(2))));

		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("nonexistent"))
				.willReturn(Collections.emptyList());

		mockMvc.perform(get("/owners/search").param("lastName", "nonexistent"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("selections"))
				.andExpect(model().attribute("selections", hasProperty("size", is(0))));

		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(""))
				.willReturn(List.of(george, owner2)); // All owners if empty search

		mockMvc.perform(get("/owners/search").param("lastName", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("selections"))
				.andExpect(model().attribute("selections", hasProperty("size", is(2))));
	}

}
