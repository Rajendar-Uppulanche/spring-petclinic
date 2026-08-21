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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.samples.petclinic.visit.Visit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Test class for the {@link PetController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(value = PetController.class,
		includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE))
@DisabledInNativeImage
@DisabledInAotMode
class PetControllerTests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	@MockitoBean
	private PetTypeRepository types;

	@BeforeEach
	void setup() {
		PetType catType = new PetType();
		catType.setId(3);
		catType.setName("cat");
		PetType dogType = new PetType();
		dogType.setId(4);
		dogType.setName("dog");
		PetType hamsterType = new PetType();
		hamsterType.setId(5);
		hamsterType.setName("hamster");
		given(this.types.findPetTypes()).willReturn(List.of(catType, dogType, hamsterType));

		Owner owner = new Owner();
		owner.setId(TEST_OWNER_ID);

		Pet petWithVisits = new Pet();
		petWithVisits.setId(TEST_PET_ID);
		petWithVisits.setName("petty");
		petWithVisits.setBirthDate(LocalDate.of(2015, 2, 12));
		petWithVisits.setType(catType);
		Visit visit = new Visit();
		visit.setId(1);
		visit.setDate(LocalDate.now());
		visit.setDescription("routine checkup");
		petWithVisits.addVisit(visit);

		Pet petWithoutVisits = new Pet();
		petWithoutVisits.setId(TEST_PET_ID + 1);
		petWithoutVisits.setName("doggy");
		petWithoutVisits.setBirthDate(LocalDate.of(2018, 5, 1));
		petWithoutVisits.setType(dogType);

		owner.addPet(petWithVisits);
		owner.addPet(petWithoutVisits);

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));
		given(this.owners.saveAndFlush(any(Owner.class))).willReturn(owner);
	}

	@Test
	void initCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"))
			.andExpect(model().attributeExists("pet"));
	}

	@Test
	void processCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Nested
	class ProcessCreationFormHasErrors {

		@Test
		void processCreationFormWithBlankName() throws Exception {
			mockMvc
				.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "\t \n")
					.param("birthDate", "2015-02-12"))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "name"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
				.andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

		@Test
		void processCreationFormWithDuplicateName() throws Exception {
			mockMvc
				.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "petty")
					.param("birthDate", "2015-02-12"))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "name"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
				.andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

		@Test
		void processCreationFormWithMissingPetType() throws Exception {
			mockMvc
				.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
					.param("birthDate", "2015-02-12"))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "type"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "type", "required"))
				.andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

		@Test
		void processCreationFormWithInvalidBirthDate() throws Exception {
			LocalDate currentDate = LocalDate.now();
			String futureBirthDate = currentDate.plusMonths(1).toString();

			mockMvc
				.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
					.param("birthDate", futureBirthDate))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "typeMismatch.birthDate"))
				.andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

		@Test
		void processCreationFormWithDataIntegrityViolation() throws Exception {
			given(owners.saveAndFlush(any(Owner.class)))
				.willThrow(new DataIntegrityViolationException("Duplicate key: unique_owner_pet_name"));
			mockMvc
				.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID).param("name", "Betty")
					.param("type", "hamster")
					.param("birthDate", "2015-02-12"))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "name"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
				.andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

		@Test
		void initUpdateForm() throws Exception {
			mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("pet"))
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

	}

	@Test
	void initUpdateForm_petWithVisits_petTypeIsReadOnly() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)) // TEST_PET_ID has visits
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attribute("isPetTypeReadOnly", true))
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void initUpdateForm_petWithoutVisits_petTypeIsEditable() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID + 1)) // TEST_PET_ID + 1 has no visits
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attribute("isPetTypeReadOnly", false))
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void processUpdateFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processUpdateFormWithSameName() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "petty") // same
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																														.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Nested
	class ProcessUpdateFormHasErrors {

		@Test
		void processUpdateFormWithDuplicateName() throws Exception {
			mockMvc
				.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID + 1)
					.param("name", "petty")
					.param("type", "hamster")
					.param("birthDate", "2015-02-12"))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "name"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
				.andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

		@Test
		void processUpdateFormWithInvalidBirthDate() throws Exception {
			mockMvc
				.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", " ")
					.param("birthDate", "2015/02/12"))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "birthDate"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "birthDate", "typeMismatch"))
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

		@Test
		void processUpdateFormWithBlankName() throws Exception {
			mockMvc
				.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "  ")
					.param("birthDate", "2015-02-12"))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "name"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

		@Test
		void processUpdateFormWithDataIntegrityViolation() throws Exception {
			given(owners.saveAndFlush(any(Owner.class)))
				.willThrow(new DataIntegrityViolationException("Duplicate key: unique_owner_pet_name"));
			mockMvc
				.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID).param("name", "Betty")
					.param("type", "hamster")
					.param("birthDate", "2015-02-12"))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "name"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "name", "duplicate"))
				.andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

	}

	@Test
	void processUpdateForm_changePetTypeWithVisits_failsValidation() throws Exception {
		// Attempt to change type of petWithVisits (TEST_PET_ID) from 'cat' to 'dog'
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "petty")
				.param("type", "dog") // Attempt to change type
				.param("birthDate", "2015-02-12"))
			.andExpect(model().attributeHasErrors("pet"))
			.andExpect(model().attributeHasFieldErrors("pet", "type"))
			.andExpect(model().attributeHasFieldErrorCode("pet", "type", "petTypeReadOnly"))
			.andExpect(status().isOk()) // Should stay on the form
			.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	void processUpdateForm_changePetTypeWithoutVisits_succeeds() throws Exception {
		// Attempt to change type of petWithoutVisits (TEST_PET_ID + 1) from 'dog' to 'cat'
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID + 1)
				.param("name", "doggy")
				.param("type", "cat") // Change type
				.param("birthDate", "2018-05-01"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processUpdateForm_noChangePetTypeWithVisits_succeeds() throws Exception {
		// Keep the same type for petWithVisits (TEST_PET_ID)
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "petty")
				.param("type", "cat") // Same type
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

}
