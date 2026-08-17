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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // Added for API tests
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; // Changed from boot.webmvc.test
import org.springframework.boot.test.mock.mockito.MockBean; // Changed from MockitoBean
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;

import java.time.LocalDate;
import java.util.Collections;
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

	@MockBean // Changed to MockBean
	private OwnerRepository owners;

	@MockBean // Added for VisitService
	private VisitService visitService;

	@MockBean // Added for VetRepository
	private VetRepository vets;

	private Owner george;
	private Pet pet;
	private Vet vet1;
	private Visit visit1;

	@BeforeEach
	void init() {
		george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");

		pet = new Pet();
		pet.setId(TEST_PET_ID);
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.of(2000, 1, 1));
		pet.setType(new PetType()); // Assuming PetType exists
		george.addPet(pet);

		vet1 = new Vet();
		vet1.setId(1);
		vet1.setFirstName("James");
		vet1.setLastName("Carter");

		visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.now().plusDays(1));
		visit1.setDescription("Routine checkup");
		visit1.setPet(pet);
		visit1.setVet(vet1);

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(george));
		given(this.visitService.findVisits(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
				.willReturn(new PageImpl<>(Collections.singletonList(visit1)));
		given(this.vets.findAll()).willReturn(Collections.singletonList(vet1));
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
				.param("name", "George") // This param is not on Visit, but was in original test. Keeping for consistency.
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Visit Description"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processNewVisitFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID).param("name",
					"George")) // This param is not on Visit, but was in original test. Keeping for consistency.
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormHasErrorsWhenVisitDateIsNotInFuture() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "George") // This param is not on Visit, but was in original test. Keeping for consistency.
				.param("date", LocalDate.now().toString())
				.param("description", "Visit Description"))
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "typeMismatch.visitDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	// New tests for filtered visits API
	@Test
	void getFilteredVisitsWithoutParams() throws Exception {
		mockMvc.perform(get("/api/visits"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(visit1.getId()))
				.andExpect(jsonPath("$.content[0].description").value(visit1.getDescription()));
	}

	@Test
	void getFilteredVisitsByOwnerId() throws Exception {
		mockMvc.perform(get("/api/visits").param("ownerId", String.valueOf(TEST_OWNER_ID)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(visit1.getId()));
	}

	@Test
	void getFilteredVisitsByPetId() throws Exception {
		mockMvc.perform(get("/api/visits").param("petId", String.valueOf(TEST_PET_ID)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(visit1.getId()));
	}

	@Test
	void getFilteredVisitsByVetId() throws Exception {
		mockMvc.perform(get("/api/visits").param("vetId", String.valueOf(vet1.getId())))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(visit1.getId()));
	}

	@Test
	void getFilteredVisitsByDateRange() throws Exception {
		LocalDate futureDate = LocalDate.now().plusDays(2);
		mockMvc.perform(get("/api/visits")
						.param("startDate", LocalDate.now().toString())
						.param("endDate", futureDate.toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(visit1.getId()));
	}

	@Test
	void getFilteredVisitsByDescription() throws Exception {
		mockMvc.perform(get("/api/visits").param("description", "checkup"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(visit1.getId()));
	}

	@Test
	void getAllVetsApi() throws Exception {
		mockMvc.perform(get("/api/vets"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(vet1.getId()))
				.andExpect(jsonPath("$[0].firstName").value(vet1.getFirstName()));
	}
}