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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link VisitController}
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

	private Owner george;
	private Pet max;

	@BeforeEach
	void setup() {
		george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");

		max = new Pet();
		PetType dog = new PetType();
		dog.setName("dog");
		max.setType(dog);
		max.setName("Max");
		max.setBirthDate(LocalDate.now().minusYears(2));
		max.setId(TEST_PET_ID);
		george.addPet(max);

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(george));
		given(this.owners.save(any(Owner.class))).willReturn(george); // Mock save operation
	}

	@Test
	void initNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("visit"))
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormSuccessWithWeight() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(2).toString())
				.param("description", "Routine checkup with weight")
				.param("weight", "15.5")) // Valid weight
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/" + TEST_OWNER_ID));
	}

	@Test
	void processNewVisitFormSuccessWithoutWeight() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(2).toString())
				.param("description", "Routine checkup without weight"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/" + TEST_OWNER_ID));
	}

	@Test
	void processNewVisitFormHasErrorsInvalidWeightZero() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(2).toString())
				.param("description", "Invalid weight test")
				.param("weight", "0.0")) // Invalid weight (zero)
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("visit", "weight"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "weight", "DecimalMin"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormHasErrorsInvalidWeightNegative() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(2).toString())
				.param("description", "Invalid weight test")
				.param("weight", "-5.0")) // Invalid weight (negative)
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("visit", "weight"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "weight", "DecimalMin"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormHasErrorsInvalidWeightNonNumeric() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(2).toString())
				.param("description", "Invalid weight test")
				.param("weight", "abc")) // Invalid weight (non-numeric)
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("visit", "weight"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "weight", "typeMismatch"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormHasErrorsInvalidDate() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().minusDays(1).toString()) // Invalid date (past)
				.param("description", "Past date test")
				.param("weight", "10.0"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "typeMismatch.visitDate"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

}
