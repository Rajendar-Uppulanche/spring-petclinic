/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(VisitController.class)
@DisabledInNativeImage
@DisabledInAotMode
class VisitControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	private static final int TEST_VISIT_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	private Owner owner;

	private Pet pet;

	@BeforeEach
	void init() {
		owner = new Owner();
		pet = new Pet();
		owner.addPet(pet);
		pet.setId(TEST_PET_ID);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));
	}

	@Test
	void initNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Visit Description")
				.param("visitType", "Vaccination")) // Assuming VisitType is a String for now
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processNewVisitFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID).param("name",
					"George"))
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormHasErrorsWhenVisitDateIsNotInFuture() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", "Visit Description")
				.param("visitType", "Vaccination"))
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "typeMismatch.visitDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormMissingVisitType() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Visit Description"))
			.andExpect(model().attributeHasFieldErrors("visit", "visitType"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "visitType", "typeMismatch.visitType"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void initUpdateVisitForm() throws Exception {
		// Setup a visit to be updated
		Visit visit = new Visit();
		visit.setId(TEST_VISIT_ID);
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setDescription("Initial Description");
		// Assuming VisitType is an enum or class, setting a placeholder
		// visit.setVisitType(VisitType.fromValue("Checkup"));
		
		owner.getPet(TEST_PET_ID).addVisit(visit);
		
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			.andExpect(model().attributeExists("visit"));
	}

	@Test
	void processUpdateVisitFormSuccess() throws Exception {
		// Setup a visit to be updated
		Visit visit = new Visit();
		visit.setId(TEST_VISIT_ID);
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setDescription("Initial Description");
		// visit.setVisitType(VisitType.fromValue("Checkup"));
		
		owner.getPet(TEST_PET_ID).addVisit(visit);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
				.param("date", LocalDate.now().plusDays(1).toString()) // Same date
				.param("description", "Updated Description") // New description
				.param("visitType", "Checkup")) // Same visit type
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processUpdateVisitFormDateImmutable() throws Exception {
		// Setup a visit to be updated
		Visit visit = new Visit();
		visit.setId(TEST_VISIT_ID);
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setDescription("Initial Description");
		// visit.setVisitType(VisitType.fromValue("Checkup"));
		
		owner.getPet(TEST_PET_ID).addVisit(visit);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
				.param("date", LocalDate.now().plusDays(2).toString()) // Different date
				.param("description", "Updated Description")
				.param("visitType", "Checkup"))
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "immutable.field"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processUpdateVisitFormVisitTypeImmutable() throws Exception {
		// Setup a visit to be updated
		Visit visit = new Visit();
		visit.setId(TEST_VISIT_ID);
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setDescription("Initial Description");
		// visit.setVisitType(VisitType.fromValue("Checkup"));
		
		owner.getPet(TEST_PET_ID).addVisit(visit);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Updated Description")
				.param("visitType", "Vaccination")) // Different visit type
			.andExpect(model().attributeHasFieldErrors("visit", "visitType"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "visitType", "immutable.field"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processUpdateVisitFormFutureDateError() throws Exception {
		// Setup a visit to be updated
		Visit visit = new Visit();
		visit.setId(TEST_VISIT_ID);
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setDescription("Initial Description");
		// visit.setVisitType(VisitType.fromValue("Checkup"));
		
		owner.getPet(TEST_PET_ID).addVisit(visit);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
				.param("date", LocalDate.now().plusDays(2).toString()) // Future date
				.param("description", "Updated Description")
				.param("visitType", "Checkup"))
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "typeMismatch.visitDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processUpdateVisitFormMissingVisitTypeError() throws Exception {
		// Setup a visit to be updated
		Visit visit = new Visit();
		visit.setId(TEST_VISIT_ID);
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setDescription("Initial Description");
		// visit.setVisitType(VisitType.fromValue("Checkup"));
		
		owner.getPet(TEST_PET_ID).addVisit(visit);
		
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VISIT_ID)
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Updated Description")
				.param("visitType", "")) // Missing visit type
			.andExpect(model().attributeHasFieldErrors("visit", "visitType"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "visitType", "typeMismatch.visitType"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

}