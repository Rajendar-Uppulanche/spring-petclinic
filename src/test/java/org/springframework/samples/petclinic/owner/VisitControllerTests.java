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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@MockitoBean
	private VisitRepository visits;

	private Owner owner;
	private Pet pet;

	@BeforeEach
	void init() {
		owner = new Owner();
		owner.setId(TEST_OWNER_ID);
		pet = new Pet();
		pet.setId(TEST_PET_ID);
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		pet.setType(new PetType());
		owner.addPet(pet);

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));
		given(this.owners.save(any(Owner.class))).willReturn(owner);
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
	void showVisitHistoryNoFilters() throws Exception {
		Visit visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.of(2023, 1, 1));
		visit1.setDescription("Routine checkup");
		visit1.setPet(pet);

		Visit visit2 = new Visit();
		visit2.setId(2);
		visit2.setDate(LocalDate.of(2023, 2, 1));
		visit2.setDescription("Vaccination");
		visit2.setPet(pet);

		List<Visit> allVisits = List.of(visit1, visit2);
		given(visits.findByPetIdAndFilters(eq(TEST_PET_ID), eq(null), eq(null), eq(null)))
			.willReturn(allVisits);

		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/history", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/visitHistory"))
			.andExpect(model().attributeExists("visits", "pet", "owner"))
			.andExpect(model().attribute("visits", allVisits));
	}

	@Test
	void showVisitHistoryWithDateFilters() throws Exception {
		LocalDate fromDate = LocalDate.of(2023, 1, 15);
		LocalDate toDate = LocalDate.of(2023, 2, 15);

		Visit visit2 = new Visit();
		visit2.setId(2);
		visit2.setDate(LocalDate.of(2023, 2, 1));
		visit2.setDescription("Vaccination");
		visit2.setPet(pet);

		List<Visit> filteredVisits = List.of(visit2);
		given(visits.findByPetIdAndFilters(eq(TEST_PET_ID), eq(fromDate), eq(toDate), eq(null)))
			.willReturn(filteredVisits);

		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/history", TEST_OWNER_ID, TEST_PET_ID)
				.param("fromDate", fromDate.toString())
				.param("toDate", toDate.toString()))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/visitHistory"))
			.andExpect(model().attributeExists("visits", "pet", "owner", "fromDate", "toDate"))
			.andExpect(model().attribute("visits", filteredVisits));
	}

	@Test
	void showVisitHistoryWithKeywordFilter() throws Exception {
		String keyword = "checkup";
		Visit visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.of(2023, 1, 1));
		visit1.setDescription("Routine checkup");
		visit1.setPet(pet);

		List<Visit> filteredVisits = List.of(visit1);
		given(visits.findByPetIdAndFilters(eq(TEST_PET_ID), eq(null), eq(null), eq(keyword)))
			.willReturn(filteredVisits);

		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/history", TEST_OWNER_ID, TEST_PET_ID)
				.param("keyword", keyword))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/visitHistory"))
			.andExpect(model().attributeExists("visits", "pet", "owner", "keyword"))
			.andExpect(model().attribute("visits", filteredVisits));
	}

	@Test
	void showVisitHistoryWithAllFilters() throws Exception {
		LocalDate fromDate = LocalDate.of(2023, 1, 1);
		LocalDate toDate = LocalDate.of(2023, 3, 1);
		String keyword = "vaccination";

		Visit visit2 = new Visit();
		visit2.setId(2);
		visit2.setDate(LocalDate.of(2023, 2, 1));
		visit2.setDescription("Annual vaccination");
		visit2.setPet(pet);

		List<Visit> filteredVisits = List.of(visit2);
		given(visits.findByPetIdAndFilters(eq(TEST_PET_ID), eq(fromDate), eq(toDate), eq(keyword)))
			.willReturn(filteredVisits);

		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/history", TEST_OWNER_ID, TEST_PET_ID)
				.param("fromDate", fromDate.toString())
				.param("toDate", toDate.toString())
				.param("keyword", keyword))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/visitHistory"))
			.andExpect(model().attributeExists("visits", "pet", "owner", "fromDate", "toDate", "keyword"))
			.andExpect(model().attribute("visits", filteredVisits));
	}

	@Test
	void showVisitHistoryWithDateRangeError() throws Exception {
		LocalDate fromDate = LocalDate.of(2023, 2, 1);
		LocalDate toDate = LocalDate.of(2023, 1, 1);

		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/history", TEST_OWNER_ID, TEST_PET_ID)
				.param("fromDate", fromDate.toString())
				.param("toDate", toDate.toString()))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/visitHistory"))
			.andExpect(model().attributeExists("dateError"))
			.andExpect(model().attribute("dateError", "From Date cannot be after To Date."))
			.andExpect(model().attribute("visits", Collections.emptyList()));
	}

	@Test
	void showVisitHistoryNoVisitsFound() throws Exception {
		given(visits.findByPetIdAndFilters(eq(TEST_PET_ID), any(), any(), any()))
			.willReturn(Collections.emptyList());

		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/history", TEST_OWNER_ID, TEST_PET_ID)
				.param("keyword", "nonexistent"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/visitHistory"))
			.andExpect(model().attribute("visits", Collections.emptyList()));
	}
}
