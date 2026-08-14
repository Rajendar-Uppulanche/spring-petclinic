package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * UI integration tests for the Owner search and related pages.
 *
 * @author Synapse Builder
 */
@WebMvcTest(OwnerController.class)
class OwnerUITests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository owners;

	@BeforeEach
	void setup() {
		// Mock the repository to return an empty page for findPaginatedForOwnersLastName
		// This prevents actual database calls during UI tests and allows testing the "no owners found" path
		when(owners.findByLastNameStartingWith(anyString(), any(Pageable.class)))
			.thenReturn(new PageImpl<>(Collections.emptyList()));
	}

	@Test
	void testNavigationMenuLabel() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Search Owner")))
			.andExpect(content().string(not(containsString("Find Owners"))));
	}

	@Test
	void testOwnerSearchPageHeading() throws Exception {
		mockMvc.perform(get("/owners/find"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/findOwners"))
			.andExpect(content().string(containsString("<h2>Search Owner</h2>")))
			.andExpect(content().string(not(containsString("<h2>Find Owners</h2>"))));
	}

	@Test
	void testFormSubmissionButtonLabel() throws Exception {
		mockMvc.perform(get("/owners/find"))
			.andExpect(status().isOk())
			.andExpect(view().name("owners/findOwners"))
			.andExpect(content().string(containsString("<button type=\"submit\" class=\"btn btn-primary\" th:text=\"#{searchOwnerButton}\">Search Owner</button>")))
			.andExpect(content().string(not(containsString("<button type=\"submit\" class=\"btn btn-primary\" th:text=\"#{findOwner}\">Find Owner</button>"))));
	}

	@Test
	void testAbsenceOfOldLabelsInOwnerSearchFlow() throws Exception {
		// Check the find owners page itself
		mockMvc.perform(get("/owners/find"))
			.andExpect(status().isOk())
			.andExpect(content().string(not(containsString("Find Owners"))))
			.andExpect(content().string(not(containsString("Find Owner")))); // Check for button text too
	}
}
