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
import java.util.Set;
import java.util.HashSet;

import org.springframework.dao.DataIntegrityViolationException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
	private static final int TEST_PET_ID_WITH_VISITS = 2;
	private static final int TEST_PET_ID_WITHOUT_VISITS = 3;


	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PetService petService; // Changed from OwnerRepository and PetTypeRepository

	private Owner testOwner;
	private Pet testPetWithVisits;
	private Pet testPetWithoutVisits;
	private PetType testPetType;

	@BeforeEach
	void setup() {
		testPetType = new PetType();
		testPetType.setId(3);
		testPetType.setName("hamster");
		given(this.petService.findPetTypes()).willReturn(List.of(testPetType)); // Using petService

		testOwner = new Owner();
		testOwner.setId(TEST_OWNER_ID);
		testOwner.setFirstName("George");
		testOwner.setLastName("Franklin");

		testPetWithVisits = new Pet();
		testPetWithVisits.setId(TEST_PET_ID_WITH_VISITS);
		testPetWithVisits.setName("Leo");
		testPetWithVisits.setBirthDate(LocalDate.of(2000, 1, 1));
		testPetWithVisits.setType(testPetType);
		testPetWithVisits.setOwner(testOwner);
		Visit visit = new Visit();
		visit.setId(1);
		visit.setDate(LocalDate.now());
		visit.setDescription("Routine checkup");
		testPetWithVisits.addVisit(visit);

		testPetWithoutVisits = new Pet();
		testPetWithoutVisits.setId(TEST_PET_ID_WITHOUT_VISITS);
		testPetWithoutVisits.setName("Max");
		testPetWithoutVisits.setBirthDate(LocalDate.of(2001, 2, 2));
		testPetWithoutVisits.setType(testPetType);
		testPetWithoutVisits.setOwner(testOwner);

		testOwner.addPet(testPetWithVisits);
		testOwner.addPet(testPetWithoutVisits);

		// Mock PetService methods
		given(this.petService.findOwnerById(TEST_OWNER_ID)).willReturn(testOwner);
		given(this.petService.findPetById(TEST_PET_ID_WITH_VISITS, TEST_OWNER_ID)).willReturn(testPetWithVisits);
		given(this.petService.findPetById(TEST_PET_ID_WITHOUT_VISITS, TEST_OWNER_ID)).willReturn(testPetWithoutVisits);
		given(this.petService.hasVisits(TEST_PET_ID_WITH_VISITS, TEST_OWNER_ID)).willReturn(true);
		given(this.petService.hasVisits(TEST_PET_ID_WITHOUT_VISITS, TEST_OWNER_ID)).willReturn(false);
		given(this.petService.hasVisits(null, TEST_OWNER_ID)).willReturn(false); // For new pets
	}

	@Test
	void initCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdatePetForm"))
			.andExpect(model().attributeExists("pet"))
			.andExpect(model().attribute("hasVisits", false)); // Verify hasVisits for new pet
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
			// Mock getPet to return an existing pet for duplicate name check
			Owner ownerWithDuplicate = new Owner();
			ownerWithDuplicate.setId(TEST_OWNER_ID);
			Pet existingPet = new Pet();
			existingPet.setName("petty");
			ownerWithDuplicate.addPet(existingPet);
			given(petService.findOwnerById(TEST_OWNER_ID)).willReturn(ownerWithDuplicate);

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
			given(petService.saveOwner(any(Owner.class))) // Using petService
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
			mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID_WITHOUT_VISITS))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("pet"))
				.andExpect(model().attribute("hasVisits", false)) // Verify hasVisits for pet without visits
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

		@Test
		void initUpdateFormWithVisits() throws Exception {
			mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID_WITH_VISITS))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("pet"))
				.andExpect(model().attribute("hasVisits", true)) // Verify hasVisits for pet with visits
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

	}

	@Test
	void processUpdateFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID_WITHOUT_VISITS).param("name", "Betty")
				.param("type", "hamster")
				.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processUpdateFormWithSameName() throws Exception {
		// Mock getPet to return an existing pet for duplicate name check
		Owner ownerWithPet = new Owner();
		ownerWithPet.setId(TEST_OWNER_ID);
		Pet existingPet = new Pet();
		existingPet.setId(TEST_PET_ID_WITHOUT_VISITS);
		existingPet.setName("Max"); // same name as existing pet
		ownerWithPet.addPet(existingPet);
		given(petService.findOwnerById(TEST_OWNER_ID)).willReturn(ownerWithPet);
		given(petService.findPetById(TEST_PET_ID_WITHOUT_VISITS, TEST_OWNER_ID)).willReturn(existingPet);


		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID_WITHOUT_VISITS).param("name", "Max")
			.param("type", "hamster")
			.param("birthDate", "2015-02-12"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Nested
	class ProcessUpdateFormHasErrors {

		@Test
		void processUpdateFormWithDuplicateName() throws Exception {
			// Mock getPet to return an existing pet for duplicate name check
			Owner ownerWithPets = new Owner();
			ownerWithPets.setId(TEST_OWNER_ID);
			Pet pet1 = new Pet();
			pet1.setId(TEST_PET_ID_WITHOUT_VISITS);
			pet1.setName("Max");
			ownerWithPets.addPet(pet1);
			Pet pet2 = new Pet();
			pet2.setId(TEST_PET_ID_WITH_VISITS);
			pet2.setName("Leo");
			ownerWithPets.addPet(pet2);
			given(petService.findOwnerById(TEST_OWNER_ID)).willReturn(ownerWithPets);
			given(petService.findPetById(TEST_PET_ID_WITH_VISITS, TEST_OWNER_ID)).willReturn(pet2);


			mockMvc
				.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID_WITH_VISITS)
					.param("name", "Max") // Trying to change Leo to Max, which already exists
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
				.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID_WITHOUT_VISITS).param("name", " ")
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
				.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID_WITHOUT_VISITS).param("name", "  ")
					.param("birthDate", "2015-02-12"))
				.andExpect(model().attributeHasNoErrors("owner"))
				.andExpect(model().attributeHasErrors("pet"))
				.andExpect(model().attributeHasFieldErrors("pet", "name"))
				.andExpect(model().attributeHasFieldErrorCode("pet", "name", "required"))
				.andExpect(view().name("pets/createOrUpdatePetForm"));
		}

		@Test
		void processUpdateFormWithDataIntegrityViolation() throws Exception {
			given(petService.saveOwner(any(Owner.class))) // Using petService
				.willThrow(new DataIntegrityViolationException("Duplicate key: unique_owner_pet_name"));
			mockMvc
				.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID_WITHOUT_VISITS).param("name", "Betty")
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

}
