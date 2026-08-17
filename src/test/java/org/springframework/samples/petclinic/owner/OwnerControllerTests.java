package org.springframework.samples.petclinic.owner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for {@link OwnerController}
 *
 * @author Michael Isvy
 * @author Wick Dynex
 */
@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository ownerRepository;

	private Owner george;
	private Owner betty;

	@BeforeEach
	void setup() {
		george = new Owner();
		george.setId(1);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");

		betty = new Owner();
		betty.setId(2);
		betty.setFirstName("Betty");
		betty.setLastName("Davis");
		betty.setAddress("638 Cardinal Ave.");
		betty.setCity("Sun Prairie");
		betty.setTelephone("6085551749");
	}

	@Test
	void testProcessFindFormWithLastName() throws Exception {
		// Scenario 1: Multiple owners found
		List<Owner> owners = Arrays.asList(betty, george);
		Page<Owner> ownersPage = new PageImpl<>(owners);
		when(ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("a"), any(Pageable.class)))
				.thenReturn(ownersPage);

		mockMvc.perform(get("/owners").param("lastName", "a"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("listOwners"))
				.andExpect(model().attribute("listOwners", owners));

		// Scenario 2: Single owner found
		ownersPage = new PageImpl<>(Collections.singletonList(george));
		when(ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("Franklin"), any(Pageable.class)))
				.thenReturn(ownersPage);

		mockMvc.perform(get("/owners").param("lastName", "Franklin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/1"));

		// Scenario 3: No owners found
		ownersPage = new PageImpl<>(Collections.emptyList());
		when(ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("nonexistent"), any(Pageable.class)))
				.thenReturn(ownersPage);

		mockMvc.perform(get("/owners").param("lastName", "nonexistent"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/findOwners"))
				.andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "lastName"));

		// Scenario 4: Empty last name (should return all owners, or a paginated subset)
		ownersPage = new PageImpl<>(owners); // Assuming all owners for simplicity
		when(ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq(""), any(Pageable.class)))
				.thenReturn(ownersPage);

		mockMvc.perform(get("/owners").param("lastName", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("listOwners"));

		// Scenario 5: No last name parameter (should also return all owners)
		mockMvc.perform(get("/owners"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("listOwners"));
	}

	@Test
	void testProcessFindFormWithLastNameCaseInsensitive() throws Exception {
		List<Owner> owners = Collections.singletonList(george);
		Page<Owner> ownersPage = new PageImpl<>(owners);
		when(ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("franklin"), any(Pageable.class)))
				.thenReturn(ownersPage);

		mockMvc.perform(get("/owners").param("lastName", "franklin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/1"));
	}

	@Test
	void testProcessFindFormWithLastNamePartialMatch() throws Exception {
		List<Owner> owners = Collections.singletonList(george);
		Page<Owner> ownersPage = new PageImpl<>(owners);
		when(ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("rank"), any(Pageable.class)))
				.thenReturn(ownersPage);

		mockMvc.perform(get("/owners").param("lastName", "rank"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/1"));
	}

	@Test
	void testProcessFindFormWithLastNameOrdering() throws Exception {
		Owner owner1 = new Owner();
		owner1.setId(3);
		owner1.setFirstName("James");
		owner1.setLastName("Smith");

		Owner owner2 = new Owner();
		owner2.setId(4);
		owner2.setFirstName("John");
		owner2.setLastName("Adams");

		List<Owner> orderedOwners = Arrays.asList(owner2, owner1); // Adams, Smith
		Page<Owner> ownersPage = new PageImpl<>(orderedOwners);

		when(ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(eq("a"), any(Pageable.class)))
				.thenReturn(ownersPage);

		mockMvc.perform(get("/owners").param("lastName", "a"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("listOwners"))
				.andExpect(model().attribute("listOwners", orderedOwners));
	}
}
