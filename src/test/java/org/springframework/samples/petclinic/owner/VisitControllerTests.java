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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import org.springframework.http.MediaType;

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

	@MockitoBean
	private VisitRepository visits;

	@MockitoBean
	private VisitStatusService visitStatusService;

	private Owner owner;
	private Pet pet;
	private Visit visit;

	@BeforeEach
	void init() {
		owner = new Owner();
		pet = new Pet();
		owner.addPet(pet);
		pet.setId(TEST_PET_ID);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));

		visit = new Visit();
		visit.setId(TEST_VISIT_ID);
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setDescription("Routine checkup");
		visit.setStatus(VisitStatus.SCHEDULED);
		given(this.visits.findById(TEST_VISIT_ID)).willReturn(Optional.of(visit));

		// Default behavior for visitStatusService for valid transitions
		given(visitStatusService.isValidTransition(any(VisitStatus.class), any(VisitStatus.class))).willReturn(true);
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
				.param("name", "George")
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Visit Description"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));

		// Verify that the saved visit has the default status
		verify(owners).save(any(Owner.class));
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
				.param("name", "George")
				.param("date", LocalDate.now().toString())
				.param("description", "Visit Description"))
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "typeMismatch.visitDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	// New tests for Visit Status functionality

	@Test
	void loadPetWithVisitSetsDefaultStatus() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("visit"))
			.andExpect(model().attribute("visit", org.hamcrest.Matchers.hasProperty("status", org.hamcrest.Matchers.is(VisitStatus.SCHEDULED))));
	}

	@Test
	void updateVisitStatusSuccess() throws Exception {
		Visit existingVisit = new Visit();
		existingVisit.setId(TEST_VISIT_ID);
		existingVisit.setStatus(VisitStatus.SCHEDULED);
		given(visits.findById(TEST_VISIT_ID)).willReturn(Optional.of(existingVisit));
		given(visitStatusService.isValidTransition(VisitStatus.SCHEDULED, VisitStatus.IN_PROGRESS)).willReturn(true);

		mockMvc.perform(put("/visits/{visitId}/status", TEST_VISIT_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content("\"IN_PROGRESS\""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("IN_PROGRESS"));

		verify(visits).save(any(Visit.class));
	}

	@Test
	void updateVisitStatusNotFound() throws Exception {
		given(visits.findById(999)).willReturn(Optional.empty());

		mockMvc.perform(put("/visits/{visitId}/status", 999)
				.contentType(MediaType.APPLICATION_JSON)
				.content("\"IN_PROGRESS\""))
			.andExpect(status().isNotFound());
	}

	@Test
	void updateVisitStatusInvalidTransition() throws Exception {
		Visit existingVisit = new Visit();
		existingVisit.setId(TEST_VISIT_ID);
		existingVisit.setStatus(VisitStatus.COMPLETED);
		given(visits.findById(TEST_VISIT_ID)).willReturn(Optional.of(existingVisit));
		given(visitStatusService.isValidTransition(VisitStatus.COMPLETED, VisitStatus.SCHEDULED)).willReturn(false);

		mockMvc.perform(put("/visits/{visitId}/status", TEST_VISIT_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content("\"SCHEDULED\""))
			.andExpect(status().isBadRequest());

		verify(visits).findById(TEST_VISIT_ID);
	}

	@Test
	void updateVisitStatusInvalidEnumValue() throws Exception {
		mockMvc.perform(put("/visits/{visitId}/status", TEST_VISIT_ID)
				.contentType(MediaType.APPLICATION_JSON)
				.content("\"INVALID_STATUS\""))
			.andExpect(status().isBadRequest());
	}

	@Test
	void visitStatusServiceValidTransitions() {
		VisitStatusService service = new VisitStatusService();
		org.junit.jupiter.api.Assertions.assertTrue(service.isValidTransition(VisitStatus.SCHEDULED, VisitStatus.IN_PROGRESS));
		org.junit.jupiter.api.Assertions.assertTrue(service.isValidTransition(VisitStatus.SCHEDULED, VisitStatus.CANCELLED));
		org.junit.jupiter.api.Assertions.assertTrue(service.isValidTransition(VisitStatus.IN_PROGRESS, VisitStatus.COMPLETED));
		org.junit.jupiter.api.Assertions.assertTrue(service.isValidTransition(VisitStatus.SCHEDULED, VisitStatus.SCHEDULED));
		org.junit.jupiter.api.Assertions.assertTrue(service.isValidTransition(VisitStatus.IN_PROGRESS, VisitStatus.IN_PROGRESS));
		org.junit.jupiter.api.Assertions.assertTrue(service.isValidTransition(VisitStatus.COMPLETED, VisitStatus.COMPLETED));
		org.junit.jupiter.api.Assertions.assertTrue(service.isValidTransition(VisitStatus.CANCELLED, VisitStatus.CANCELLED));
	}

	@Test
	void visitStatusServiceInvalidTransitions() {
		VisitStatusService service = new VisitStatusService();
		org.junit.jupiter.api.Assertions.assertFalse(service.isValidTransition(VisitStatus.CANCELLED, VisitStatus.SCHEDULED));
		org.junit.jupiter.api.Assertions.assertFalse(service.isValidTransition(VisitStatus.CANCELLED, VisitStatus.IN_PROGRESS));
		org.junit.jupiter.api.Assertions.assertFalse(service.isValidTransition(VisitStatus.CANCELLED, VisitStatus.COMPLETED));
		org.junit.jupiter.api.Assertions.assertFalse(service.isValidTransition(VisitStatus.COMPLETED, VisitStatus.SCHEDULED));
		org.junit.jupiter.api.Assertions.assertFalse(service.isValidTransition(VisitStatus.COMPLETED, VisitStatus.IN_PROGRESS));
		org.junit.jupiter.api.Assertions.assertFalse(service.isValidTransition(VisitStatus.COMPLETED, VisitStatus.CANCELLED));
		org.junit.jupiter.api.Assertions.assertFalse(service.isValidTransition(VisitStatus.SCHEDULED, VisitStatus.COMPLETED));
		org.junit.jupiter.api.Assertions.assertFalse(service.isValidTransition(VisitStatus.IN_PROGRESS, VisitStatus.SCHEDULED));
		org.junit.jupiter.api.Assertions.assertFalse(service.isValidTransition(VisitStatus.IN_PROGRESS, VisitStatus.CANCELLED));
	}
}