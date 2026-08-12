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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(value = VisitController.class,
		includeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Controller.class, type = FilterType.ASSIGNABLE_TYPE),
		excludeFilters = @ComponentScan.Filter(value = org.springframework.stereotype.Repository.class, type = FilterType.ASSIGNABLE_TYPE))
@DisabledInNativeImage
@DisabledInAotMode
class VisitControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	// Mocking OwnerRepository and VisitRepository
	// VisitService is implicitly tested via VisitController
	@org.mockito.MockBean
	private OwnerRepository owners;

	@org.mockito.MockBean
	private VisitRepository visits;

	@org.mockito.MockBean
	private VisitService visitService;

	@BeforeEach
	void init() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		owner.addPet(pet);
		pet.setId(TEST_PET_ID);
		// Mocking owner repository findById
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));

		// Mocking visit service for pagination and export
		VisitPage visitPage = new VisitPage(pet.getVisits(), pet.getVisits().size(), 1, 10);
		given(this.visitService.findVisitsByPetId(TEST_PET_ID, 1, 10, "appointmentDate", "desc"))
				.willReturn(visitPage);
		given(this.visitService.exportVisitsAsCsv(pet.getName(), pet.getVisits()))
				.willReturn(new byte[0]); // Return empty byte array for export test
		given(this.visitService.generateCsvFilename(pet.getName()))
				.willReturn("petclinic_visits_Buddy_2023-01-01.csv");

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
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "typeMismatch.visitDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void showPetVisits() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/visitList"))
			.andExpect(model().attributeExists("visits"))
			.andExpect(model().attributeExists("currentPage"))
			.andExpect(model().attributeExists("pageSize"))
			.andExpect(model().attributeExists("sort"))
			.andExpect(model().attributeExists("direction"));
	}

	@Test
	void showPetVisitsWithPaginationAndSorting() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits", TEST_OWNER_ID, TEST_PET_ID)
				.param("page", "2")
				.param("size", "25")
				.param("sort", "visitDate")
				.param("direction", "asc"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/visitList"));

		// Verify that visitService.findVisitsByPetId was called with correct parameters
		org.mockito.Mockito.verify(visitService).findVisitsByPetId(TEST_PET_ID, 2, 25, "visitDate", "asc");
	}

	@Test
	void exportPetVisits() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/export", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attributeExists("pet"));

		// Verify that visitService.exportVisitsAsCsv and generateCsvFilename were called
		org.mockito.Mockito.verify(visitService).exportVisitsAsCsv(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyCollection());
		org.mockito.Mockito.verify(visitService).generateCsvFilename(org.mockito.ArgumentMatchers.anyString());
	}

	@Test
	void exportPetVisits_NoVisits() throws Exception {
		Owner owner = new Owner();
		Pet pet = new Pet();
		owner.addPet(pet);
		pet.setId(TEST_PET_ID);
		// Ensure owner repository returns this owner
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));

		// Mock visitService to return empty visits for export
		given(this.visitService.exportVisitsAsCsv(pet.getName(), pet.getVisits()))
				.willReturn(new byte[0]);
		given(this.visitService.generateCsvFilename(pet.getName()))
				.willReturn("petclinic_visits_Buddy_2023-01-01.csv");

		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/export", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk());

		// Verify that visitService.exportVisitsAsCsv and generateCsvFilename were called
		org.mockito.Mockito.verify(visitService).exportVisitsAsCsv(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyCollection());
		org.mockito.Mockito.verify(visitService).generateCsvFilename(org.mockito.ArgumentMatchers.anyString());
	}

}
