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
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList; // Added for ArrayList
import java.util.Collections; // Added for Collections.sort
import java.util.Comparator; // Added for Comparator
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link OwnerController}
 *
 * @author Colin But
 * @author Wick Dynex
 */
@WebMvcTest(OwnerController.class)
@DisabledInNativeImage
@DisabledInAotMode
class OwnerControllerTests {

	private static final int TEST_OWNER_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerRepository owners;

	private Owner george() {
		Owner george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");
		Pet max = new Pet();
		PetType dog = new PetType();
		dog.setName("dog");
		max.setType(dog);
		max.setName("Max");
		max.setBirthDate(LocalDate.now());
		george.addPet(max);
		max.setId(1);
		return george;
	}

	private Owner betty() {
		Owner betty = new Owner();
		betty.setId(2);
		betty.setFirstName("Betty");
		betty.setLastName("Davis");
		betty.setAddress("638 Cardinal Ave.");
		betty.setCity("Sun Prairie");
		betty.setTelephone("6085551749");
		return betty;
	}

	private Owner harold() {
		Owner harold = new Owner();
		harold.setId(4);
		harold.setFirstName("Harold");
		harold.setLastName("Davis");
		harold.setAddress("563 Friendly St.");
		harold.setCity("Windsor");
		harold.setTelephone("6085553198");
		return harold;
	}

	private Owner jeanette() {
		Owner jeanette = new Owner();
		jeanette.setId(6);
		jeanette.setFirstName("Jeanette");
		jeanette.setLastName("Williams");
		jeanette.setAddress("1013f W. Long St.");
		jeanette.setCity("Madison");
		jeanette.setTelephone("6085552765");
		return jeanette;
	}

	@BeforeEach
	void setup() {

		Owner george = george();
		Owner betty = betty();
		Owner harold = harold();
		Owner jeanette = jeanette();

		// Mock for findByLastNameContainingIgnoreCaseOrderByLastNameAsc
		// For "Franklin" or "franklin"
		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("Franklin"), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(george)));
		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("franklin"), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of(george)));

		// For "Davis" or "davis" or "dav"
		List<Owner> davisOwners = new ArrayList<>(List.of(betty, harold));
		Collections.sort(davisOwners, Comparator.comparing(Owner::getLastName)); // Ensure sorted
		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("Davis"), any(Pageable.class)))
			.willReturn(new PageImpl<>(davisOwners));
		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("davis"), any(Pageable.class)))
			.willReturn(new PageImpl<>(davisOwners));
		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("dav"), any(Pageable.class)))
			.willReturn(new PageImpl<>(davisOwners));

		// For "i" (all owners containing 'i', sorted)
		List<Owner> allOwnersWithI = new ArrayList<>(List.of(george, betty, harold, jeanette)); // Assuming these are the ones with 'i'
		// Add more owners if needed to match the 6 from ClinicServiceTests
		Owner eduardo = new Owner(); eduardo.setId(3); eduardo.setLastName("Rodriquez"); allOwnersWithI.add(eduardo);
		Owner peter = new Owner(); peter.setId(5); peter.setLastName("McTavish"); allOwnersWithI.add(peter);
		Collections.sort(allOwnersWithI, Comparator.comparing(Owner::getLastName));
		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("i"), any(Pageable.class)))
			.willReturn(new PageImpl<>(allOwnersWithI));


		// For empty string (all owners, sorted)
		List<Owner> allOwners = new ArrayList<>(List.of(george, betty, harold, jeanette, eduardo, peter));
		Collections.sort(allOwners, Comparator.comparing(Owner::getLastName));
		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq(""), any(Pageable.class)))
			.willReturn(new PageImpl<>(allOwners));

		// For no match
		given(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("NonExistent"), any(Pageable.class)))
			.willReturn(new PageImpl<>(List.of()));


		// Original mock for findById
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(george));
		Visit visit = new Visit();
		visit.setDate(LocalDate.now());
		george.getPet("Max").getVisits().add(visit);

	}

	@Test
	void initCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void processCreationFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "1316761638"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void processCreationFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs").param("city", "London"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "address"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void initFindForm() throws Exception {
		mockMvc.perform(get("/owners/find"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(view().name("owners/findOwners"));
	}

	@Test
	void processFindFormSuccessWithEmptyLastNameReturnsAllOwners() throws Exception { // Renamed
		List<Owner> allOwners = new ArrayList<>(List.of(george(), betty(), harold(), jeanette()));
		Owner eduardo = new Owner(); eduardo.setId(3); eduardo.setLastName("Rodriquez"); allOwners.add(eduardo);
		Owner peter = new Owner(); peter.setId(5); peter.setLastName("McTavish"); allOwners.add(peter);
		Collections.sort(allOwners, Comparator.comparing(Owner::getLastName));

		Page<Owner> tasks = new PageImpl<>(allOwners);
		when(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq(""), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1")) // No lastName param, so it defaults to empty string
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(model().attribute("listOwners", hasSize(6)))
			.andExpect(model().attribute("listOwners", hasItem(hasProperty("lastName", is("Davis")))))
			.andExpect(model().attribute("listOwners", hasItem(hasProperty("lastName", is("Franklin")))));

		verify(this.owners).findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq(""), any(Pageable.class));
	}

	@Test
	void processFindFormByLastNameExactMatchCaseInsensitive() throws Exception {
		mockMvc.perform(get("/owners?page=1").param("lastName", "franklin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
		verify(this.owners).findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("franklin"), any(Pageable.class));
	}

	@Test
	void processFindFormByLastNamePartialMatchCaseInsensitiveMultipleResults() throws Exception {
		mockMvc.perform(get("/owners?page=1").param("lastName", "dav"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(model().attribute("listOwners", hasSize(2)))
			.andExpect(model().attribute("listOwners", hasItem(hasProperty("lastName", is("Davis")))));
		verify(this.owners).findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("dav"), any(Pageable.class));
	}

	@Test
	void processFindFormIgnoresSurroundingWhitespace() throws Exception {
		Owner george = george();
		Page<Owner> tasks = new PageImpl<>(List.of(george));
		when(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("Franklin"), any(Pageable.class))).thenReturn(tasks);

		for (String lastName : List.of(" Franklin", "Franklin ", " Franklin ")) {
			mockMvc.perform(get("/owners?page=1").param("lastName", lastName))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
		}

		verify(this.owners, times(3)).findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("Franklin"), any(Pageable.class));
	}

	@Test
	void processFindFormWithWhitespaceOnlyLastNameReturnsAllOwners() throws Exception {
		List<Owner> allOwners = new ArrayList<>(List.of(george(), betty(), harold(), jeanette()));
		Owner eduardo = new Owner(); eduardo.setId(3); eduardo.setLastName("Rodriquez"); allOwners.add(eduardo);
		Owner peter = new Owner(); peter.setId(5); peter.setLastName("McTavish"); allOwners.add(peter);
		Collections.sort(allOwners, Comparator.comparing(Owner::getLastName));

		Page<Owner> tasks = new PageImpl<>(allOwners);
		when(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq(""), any(Pageable.class))).thenReturn(tasks);

		mockMvc.perform(get("/owners?page=1").param("lastName", "   "))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/ownersList"))
			.andExpect(model().attribute("listOwners", hasSize(6)));

		verify(this.owners).findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq(""), any(Pageable.class));
	}

	@Test
	void processFindFormNoOwnersFound() throws Exception {
		Page<Owner> tasks = new PageImpl<>(List.of());
		when(this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("Unknown Surname"), any(Pageable.class))).thenReturn(tasks);
		mockMvc.perform(get("/owners?page=1").param("lastName", "Unknown Surname"))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasFieldErrors("owner", "lastName"))
			.andExpect(model().attributeHasFieldErrorCode("owner", "lastName", "notFound"))
			.andExpect(view().name("owners/findOwners"));
		verify(this.owners).findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("Unknown Surname"), any(Pageable.class));
	}

	@Test
	void initUpdateOwnerForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("owner"))
			.andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
			.andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
			.andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
			.andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
			.andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void processUpdateOwnerFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "1616291589"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processUpdateOwnerFormUnchangedSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processUpdateOwnerFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "")
				.param("telephone", ""))
			.andExpect(status().isOk())
			.andExpect(model().attributeHasErrors("owner"))
			.andExpect(model().attributeHasFieldErrors("owner", "address"))
			.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
			.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void showOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
			.andExpect(status().isOk())
			.andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
			.andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
			.andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
			.andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
			.andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
			.andExpect(model().attribute("owner", hasProperty("pets", not(empty()))))
			.andExpect(model().attribute("owner",
					hasProperty("pets", hasItem(hasProperty("visits", hasSize(greaterThan(0)))))))
			.andExpect(view().name("owners/ownerDetails"));
	}

	@Test
	void processUpdateOwnerFormWithIdMismatch() throws Exception {
		int pathOwnerId = 1;

		Owner owner = new Owner();
		owner.setId(2);
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("Center Street");
		owner.setCity("New York");
		owner.setTelephone("0123456789");

		when(owners.findById(pathOwnerId)).thenReturn(Optional.of(owner));

		mockMvc.perform(MockMvcRequestBuilders.post("/owners/{ownerId}/edit", pathOwnerId).flashAttr("owner", owner))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/owners/" + pathOwnerId + "/edit"))
			.andExpect(flash().attributeExists("error"));
	}

}
