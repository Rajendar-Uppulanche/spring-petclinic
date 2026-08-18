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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 * @author Wick Dynex
 * @author Synapse Builder
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
	private VisitService visitService;

	private Owner testOwner;
	private Pet testPet;

	@BeforeEach
	void init() {
		testOwner = new Owner();
		testOwner.setId(TEST_OWNER_ID);
		testOwner.setFirstName("George");
		testOwner.setLastName("Franklin");

		testPet = new Pet();
		testPet.setId(TEST_PET_ID);
		testPet.setName("Leo");
		testPet.setOwner(testOwner);
		testOwner.addPet(testPet);

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(testOwner));
	}

	@Test
	void initNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormSuccess() throws Exception {
		when(visitService.saveVisit(any(Visit.class))).thenReturn(null);

		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Visit Description")
                .param("status", VisitStatus.SCHEDULED.name()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));

		verify(visitService).saveVisit(any(Visit.class));
	}

	@Test
	void processNewVisitFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
                .param("date", LocalDate.now().plusDays(1).toString())
                .param("description", ""))
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormHasErrorsWhenVisitDateIsNotInFuture() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", LocalDate.now().toString())
				.param("description", "Visit Description"))
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "typeMismatch.visitDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

    @Test
    void searchVisitsReturnsFilteredResults() throws Exception {
        Visit visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.now().plusDays(5));
        visit1.setDescription("Routine checkup");
        visit1.setPet(testPet);
        visit1.setStatus(VisitStatus.SCHEDULED);

        Visit visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.now().plusDays(10));
        visit2.setDescription("Vaccination");
        visit2.setPet(testPet);
        visit2.setStatus(VisitStatus.SCHEDULED);

        List<Visit> mockVisits = Arrays.asList(visit1, visit2);

        when(visitService.findVisits(any(VisitSearchCriteriaDTO.class))).thenReturn(mockVisits);

        mockMvc.perform(get("/visits/search")
                .param("startDate", LocalDate.now().toString())
                .param("endDate", LocalDate.now().plusDays(15).toString())
                .param("petName", "Leo")
                .param("status", VisitStatus.SCHEDULED.name()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].description").value("Routine checkup"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].description").value("Vaccination"));

        verify(visitService).findVisits(any(VisitSearchCriteriaDTO.class));
    }

}
