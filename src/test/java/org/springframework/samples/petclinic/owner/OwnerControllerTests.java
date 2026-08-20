package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link OwnerController} @Controller
 *
 * @author بودن
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
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
				.param("address", "123 Caramel Street").param("city", "London").param("telephone", "01316761638")
				.with(csrf())).andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("city", "London").param("telephone", "").with(csrf())).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner")).andExpect(model().attributeHasFieldErrors("owner", "address"))
				.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormTelephoneHasNonDigitsError() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("address", "123 Caramel Street").param("city", "London").param("telephone", "abc123def")
				.with(csrf())).andExpect(status().isOk()).andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormTelephoneTooShortError() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("address", "123 Caramel Street").param("city", "London").param("telephone", "12345")
				.with(csrf())).andExpect(status().isOk()).andExpect(model().attributeHasErrors("owner"))
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
		given(this.owners.findByLastName("")).willReturn(List.of(george));
		mockMvc.perform(get("/owners")).andExpect(status().isFound()).andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormByLastName() throws Exception {
		given(this.owners.findByLastName(george.getLastName())).willReturn(List.of(george));
		mockMvc.perform(get("/owners").param("lastName", george.getLastName()))
				.andExpect(status().isFound()).andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormHomeless() throws Exception {
		given(this.owners.findByLastName("")).willReturn(Collections.emptyList());
		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormManyOwners() throws Exception {
		List<Owner> owners = List.of(george, new Owner());
		given(this.owners.findByLastName("")).willReturn(owners);
		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testInitUpdateOwnerForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(model().attributeExists("owner")).andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
				.param("lastName", "Franklin").param("address", "110 W. Liberty St.").param("city", "Madison")
				.param("telephone", "6085551023").with(csrf())).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
				.param("lastName", "Franklin").param("address", "110 W. Liberty St.").param("city", "Madison")
				.param("telephone", "").with(csrf())).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner")).andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormTelephoneHasNonDigitsError() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
				.param("lastName", "Franklin").param("address", "110 W. Liberty St.").param("city", "Madison")
				.param("telephone", "abc123def").with(csrf())).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner")).andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormTelephoneTooShortError() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
				.param("lastName", "Franklin").param("address", "110 W. Liberty St.").param("city", "Madison")
				.param("telephone", "12345").with(csrf())).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("owner")).andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(model().attributeExists("owner")).andExpect(view().name("owners/ownerDetails"));
	}

}