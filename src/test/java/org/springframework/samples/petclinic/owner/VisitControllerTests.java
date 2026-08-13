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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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

	private Owner owner;

	private Pet pet;

	private Visit visit;

	@BeforeEach
	void init() {
		owner = new Owner();
		owner.setId(TEST_OWNER_ID);

		pet = new Pet();
		pet.setId(TEST_PET_ID);
		pet.setName("Leo");
		owner.addPet(pet);

		visit = new Visit();
		visit.setId(TEST_VISIT_ID);
		visit.setDate(LocalDate.now().plusDays(1));
		visit.setDescription("Test Visit");
		visit.setStatus(VisitStatus.SCHEDULED);

		Set<Visit> petVisits = new HashSet<>();
		petVisits.add(visit);
		given(pet.getVisits()).willReturn(petVisits);

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));
		given(this.visits.findById(TEST_VISIT_ID)).willReturn(Optional.of(visit));
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
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attributeExists("message"));
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

	@Test
	void updateVisitStatusSuccess() throws Exception {
		mockMvc
			.perform(put("/owners/{ownerId}/pets/{petId}/visits/{visitId}/status", TEST_OWNER_ID, TEST_PET_ID,
					TEST_VISIT_ID)
				.param("newStatus", "IN_PROGRESS"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"))
			.andExpect(flash().attribute("message", "Visit status updated successfully to IN_PROGRESS."));

		verify(visits, times(1)).save(visit);
		assertThat(visit.getStatus()).isEqualTo(VisitStatus.IN_PROGRESS);
	}

	@Test
	void updateVisitStatusInvalidValue() throws Exception {
		mockMvc
			.perform(put("/owners/{ownerId}/pets/{petId}/visits/{visitId}/status", TEST_OWNER_ID, TEST_PET_ID,
					TEST_VISIT_ID)
				.param("newStatus", "INVALID_STATUS"))
			.andExpect(status().isBadRequest());

		verify(visits, times(0)).save(visit);
	}

	@Test
	void updateVisitStatusInvalidTransition() throws Exception {
		visit.setStatus(VisitStatus.COMPLETED);
		given(this.visits.findById(TEST_VISIT_ID)).willReturn(Optional.of(visit));

		mockMvc
			.perform(put("/owners/{ownerId}/pets/{petId}/visits/{visitId}/status", TEST_OWNER_ID, TEST_PET_ID,
					TEST_VISIT_ID)
				.param("newStatus", "IN_PROGRESS"))
			.andExpect(status().isBadRequest());

		verify(visits, times(0)).save(visit);
		assertThat(visit.getStatus()).isEqualTo(VisitStatus.COMPLETED);
	}

	@Test
	void updateVisitStatusVisitNotFound() throws Exception {
		given(this.visits.findById(999)).willReturn(Optional.empty());

		mockMvc
			.perform(put("/owners/{ownerId}/pets/{petId}/visits/{visitId}/status", TEST_OWNER_ID, TEST_PET_ID, 999)
				.param("newStatus", "IN_PROGRESS"))
			.andExpect(status().isNotFound());

		verify(visits, times(0)).save(visit);
	}

	@Test
	void updateVisitStatusPetNotFound() throws Exception {
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));
		owner.getPets().clear();

		mockMvc
			.perform(put("/owners/{ownerId}/pets/{petId}/visits/{visitId}/status", TEST_OWNER_ID, 999, TEST_VISIT_ID)
				.param("newStatus", "IN_PROGRESS"))
			.andExpect(status().isNotFound());

		verify(visits, times(0)).save(visit);
	}

	@Test
	void updateVisitStatusOwnerNotFound() throws Exception {
		given(this.owners.findById(999)).willReturn(Optional.empty());

		mockMvc
			.perform(put("/owners/{ownerId}/pets/{petId}/visits/{visitId}/status", 999, TEST_PET_ID, TEST_VISIT_ID)
				.param("newStatus", "IN_PROGRESS"))
			.andExpect(status().isNotFound());

		verify(visits, times(0)).save(visit);
	}

	@Test
	void updateVisitStatusVisitDoesNotBelongToPet() throws Exception {
		Pet otherPet = new Pet();
		otherPet.setId(99);
		Visit otherVisit = new Visit();
		otherVisit.setId(TEST_VISIT_ID);
		otherVisit.setDate(LocalDate.now().plusDays(1));
		otherVisit.setDescription("Other Pet's Visit");
		otherVisit.setStatus(VisitStatus.SCHEDULED);
		Set<Visit> otherVisitSet = new HashSet<>();
		otherVisitSet.add(otherVisit);
		given(otherPet.getVisits()).willReturn(otherVisitSet);

		given(this.visits.findById(TEST_VISIT_ID)).willReturn(Optional.of(otherVisit));

		mockMvc
			.perform(put("/owners/{ownerId}/pets/{petId}/visits/{visitId}/status", TEST_OWNER_ID, TEST_PET_ID,
					TEST_VISIT_ID)
				.param("newStatus", "IN_PROGRESS"))
			.andExpect(status().isBadRequest());

		verify(visits, times(0)).save(otherVisit);
	}
}
