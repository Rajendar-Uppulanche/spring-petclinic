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

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;

/**
 * UI tests for {@link VisitController} and the visit form.
 */
@WebMvcTest(VisitController.class)
@DisabledInNativeImage
@DisabledInAotMode
class VisitFormUITests {

	private static final int TEST_OWNER_ID = 1;

	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	private Owner owner;

	private Pet pet;

	@BeforeEach
	void setup() {
		owner = new Owner();
		owner.setId(TEST_OWNER_ID);
		pet = new Pet();
		pet.setId(TEST_PET_ID);
		owner.addPet(pet);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));
	}

	@Test
	void testVisitFormLabelsAndStyling() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"))
			// UI-004: Check for 'Appointment Date' label
			.andExpect(content().string(containsString("<label for=\"date\" class=\"col-sm-2 control-label\" style=\"font-weight: 600;\">Appointment Date</label>")))
			// UI-005: Check for 'Reason for Visit' label
			.andExpect(content().string(containsString("<label for=\"description\" class=\"col-sm-2 control-label\" style=\"font-weight: 600;\">Reason for Visit</label>")))
			// UI-001: Check for 'Add Visit' button styling
			.andExpect(content().string(containsString("<button class=\"btn btn-primary\" type=\"submit\" style=\"background-color: #28A745; color: white;\">Add Visit</button>")))
			// UI-002: Check for 'Cancel' link styling
			.andExpect(content().string(containsString("<a href=\"/owners/1\" style=\"color: #6C757D; margin-left: 10px;\">Cancel</a>")))
			// UI-003: Check for form panel background styling
			.andExpect(content().string(containsString("<div style=\"background-color: #EBF5FB; padding: 20px; border-radius: 5px; margin-bottom: 20px;\">")));
	}

}
