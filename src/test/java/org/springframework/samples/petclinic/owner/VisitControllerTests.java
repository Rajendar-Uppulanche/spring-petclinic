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
import org.springframework.boot.webmvc.test.context.MockMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(value = VisitController.class,
		includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
				classes = {OwnerRepository.class, VisitRepository.class}))
@DisabledInNativeImage
@DisabledInAotMode
class VisitControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OwnerRepository owners;

	@Autowired
	private VisitRepository visits;

	@BeforeEach
	void init() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		owner.addPet(pet);
		pet.setId(TEST_PET_ID);
		pet.setOwner(owner);
		owner.setId(TEST_OWNER_ID);
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
				.param("name", "George")
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Visit Description"))
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
				.param("name", "George")
				.param("date", LocalDate.now().toString())
				.param("description", "Visit Description"))
			.andExpect(model().attributeHasFieldErrors("visit", "date")),
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "typeMismatch.visitDate")),
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void showVisitList() throws Exception {
		mockMvc.perform(get("/visits"))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/visitList"))
			.andExpect(model().attributeExists("visits"));
	}

	@Test
	void showVisitListWithDateFilter() throws Exception {
		LocalDate fromDate = LocalDate.now().minusDays(10);
		LocalDate toDate = LocalDate.now().plusDays(10);
		given(this.visits.findByDateBetweenOrDescriptionContainingIgnoreCase(fromDate, toDate, null))
			.willReturn(new ArrayList<>());

		mockMvc.perform(get("/visits?fromDate=", fromDate.toString()).param("toDate", toDate.toString()))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/visitList"))
			.andExpect(model().attributeExists("visits"));
	}

	@Test
	void showVisitListWithKeywordFilter() throws Exception {
		String keyword = "check-up";
		given(this.visits.findByDateBetweenOrDescriptionContainingIgnoreCase(null, null, keyword))
			.willReturn(new ArrayList<>());

		mockMvc.perform(get("/visits?keyword=", keyword))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/visitList"))
			.andExpect(model().attributeExists("visits"));
	}

	@Test
	void showVisitListWithCombinedFilters() throws Exception {
		LocalDate fromDate = LocalDate.now().minusDays(10);
		LocalDate toDate = LocalDate.now().plusDays(10);
		String keyword = "check-up";
		given(this.visits.findByDateBetweenOrDescriptionContainingIgnoreCase(fromDate, toDate, keyword))
			.willReturn(new ArrayList<>());

		mockMvc.perform(get("/visits?fromDate=", fromDate.toString()).param("toDate", toDate.toString()).param("keyword", keyword))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/visitList"))
			.andExpect(model().attributeExists("visits"));
	}

	@Test
	void showVisitListWhenNoVisitsFound() throws Exception {
		given(this.visits.findAll()).willReturn(new ArrayList<>());

		mockMvc.perform(get("/visits"))
			.andExpect(status().isOk())
			.andExpect(view().name("visits/visitList"))
			.andExpect(model().attribute("visits", new ArrayList<>()));
	}

}
