/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 20.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.assertj.core.util.Lists;
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

import java.time.LocalDate;
import java.util.Collections;

/**
 * Test class for {@link OwnerController}
 *
 * @author&lt;a href="mailto:michael.gatto@gmail.com"&gt;Michael Gatto&lt;/a&gt;
 * @author Antoine Rey
 */
@WebMvcTest(OwnerController.class)
class OwnerControllerIntegrationTest {

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
		Pet max = new Pet();
		PetType dog = new PetType();
		dog.setName("dog");
		max.setId(1);
		max.setType(dog);
		max.setName("Max");
		max.setBirthDate(LocalDate.now());
		george.addPet(max);

		given(this.clinicService.findOwnerById(TEST_OWNER_ID)).willReturn(george);
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new")).andExpect(status().isOk()).andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
			.param("address", "123 Main Street").param("city", "Herndon").param("telephone", "0123456789"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
			.param("city", "Herndon").param("telephone", ""))).andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
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
		given(this.clinicService.findOwnerByLastName("")).willReturn(Lists.newArrayList(george, new Owner()));
		mockMvc.perform(get("/owners")).andExpect(status().isOk()).andExpect(view().name("owners/ownersList"));
	}

	@Test
	void testProcessFindFormByLastName() throws Exception {
		given(this.clinicService.findOwnerByLastName(george.getLastName()))
			.willReturn(Lists.newArrayList(george));
		mockMvc.perform(get("/owners").param("lastName", "Franklin")).andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	void testProcessFindFormNoOwnersFound() throws Exception {
		given(this.clinicService.findOwnerByLastName("Unknown")).willReturn(Collections.emptyList());
		mockMvc.perform(get("/owners").param("lastName", "Unknown")).andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(model().attributeHasErrors("owner")).andExpect(view().name("owners/findOwners"));
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
			.param("telephone", "6085551023"))).andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
			.param("lastName", "Franklin").param("address", "110 W. Liberty St.").param("city", "Madison")
			.param("telephone", ""))).andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID)).andExpect(status().isOk())
			.andExpect(model().attribute("owner", george)).andExpect(view().name("owners/ownerDetails"));
	}

	@Test
	void testDisplayPetVisitCount() throws Exception {
		PetType cat = new PetType();
		cat.setName("cat");

		Pet petWithVisits = new Pet();
		petWithVisits.setId(2);
		petWithVisits.setName("Whiskers");
		petWithVisits.setBirthDate(LocalDate.of(2018, 1, 1));
		petWithVisits.setType(cat);

		Visit visit1 = new Visit();
		visit1.setDate(LocalDate.now());
		visit1.setDescription("Routine checkup");
		petWithVisits.addVisit(visit1);

		Visit visit2 = new Visit();
		visit2.setDate(LocalDate.now().minusDays(10));
		visit2.setDescription("Vaccination");
		petWithVisits.addVisit(visit2);

		george.addPet(petWithVisits);

		Pet petWithoutVisits = new Pet();
		petWithoutVisits.setId(3);
		petWithoutVisits.setName("Shadow");
		petWithoutVisits.setBirthDate(LocalDate.of(2020, 5, 15));
		petWithoutVisits.setType(cat);
		// No visits added to petWithoutVisits

		george.addPet(petWithoutVisits);

		given(this.clinicService.findOwnerById(TEST_OWNER_ID)).willReturn(george);

		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID)).andExpect(status().isOk())
			.andExpect(model().attribute("owner", george)).andExpect(view().name("owners/ownerDetails"))
			.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
				.string(org.hamcrest.Matchers.containsString("Max (0 visits)")))
			.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
				.string(org.hamcrest.Matchers.containsString("Whiskers (2 visits)")))
			.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
				.string(org.hamcrest.Matchers.containsString("Shadow (0 visits)")));
	}

}