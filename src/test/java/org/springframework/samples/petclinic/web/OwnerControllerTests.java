package org.springframework.samples.petclinic.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link OwnerController} @Controller
 *
 * @author بودن
 */
@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

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
		Pet max = new Pet();
		PetType cat = new PetType();
		cat.setName("cat");
		max.setId(TEST_PET_ID);
		max.setType(cat);
		max.setName("Max");
		max.setBirthDate(LocalDate.now());
		george.addPet(max);
		given(this.clinicService.findOwnerById(TEST_OWNER_ID)).willReturn(george);
		given(this.clinicService.findPetById(TEST_PET_ID)).willReturn(max);
		given(this.clinicService.findPetTypes()).willReturn(List.of(cat));
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
			.andExpect(model().attributeHasFieldErrors("owner", "address")).andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void testProcessFindFormSuccess() throws Exception {
		given(this.clinicService.findOwnerByLastName("")).willReturn(List.of(george));
		mockMvc.perform(get("/owners")).andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormWithName() throws Exception {
		given(this.clinicService.findOwnerByLastName(george.getLastName())).willReturn(List.of(george));
		mockMvc.perform(get("/owners").param("lastName", george.getLastName())).andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormNoOwnersFound() throws Exception {
		given(this.clinicService.findOwnerByLastName("Franklin")).willReturn(Collections.emptyList());
		mockMvc.perform(get("/owners").param("lastName", "Franklin")).andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "notFound"))
			.andExpect(view().name("owners/findOwners"));
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
			.param("telephone", "6085551023")).andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
			.param("lastName", "Franklin").param("address", "110 W. Liberty St.").param("city", "Madison")
			.param("telephone", "")).andExpect(status().isOk()).andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID)).andExpect(status().isOk())
			.andExpect(model().attribute("owner", george)).andExpect(view().name("owners/ownerDetails"));
	}

	@Test
	void testAddVisit() throws Exception {
		Pet pet = george.getPet("Max");
		Visit visit = new Visit();
		pet.addVisit(visit);
		given(this.clinicService.findPetById(TEST_PET_ID)).willReturn(pet);
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk()).andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void testPetGetNameReturnsEmptyStringWhenNameIsNull() {
		Pet pet = new Pet();
		pet.setName(null);
		Assertions.assertThat(pet.getName()).isEqualTo("");
	}

}
