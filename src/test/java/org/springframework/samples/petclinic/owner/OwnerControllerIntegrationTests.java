package org.springframework.samples.petclinic.owner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test for {@link OwnerController}
 *
 * @author Wick Dynex
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OwnerControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testOwnerSearchEndpointNoParam() throws Exception {
		mockMvc.perform(get("/owners"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("listOwners"))
				.andExpect(xpath("//table[@id='owners']/tbody/tr").nodeCount(10)); // Assuming 10 owners in test data
	}

	@Test
	void testOwnerSearchEndpointWithLastNamePartialMatch() throws Exception {
		mockMvc.perform(get("/owners").param("lastName", "da"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("listOwners"))
				.andExpect(xpath("//table[@id='owners']/tbody/tr").nodeCount(2)) // Davis, Davidson
				.andExpect(xpath("//table[@id='owners']/tbody/tr[1]/td[1]").string("Betty Davis"))
				.andExpect(xpath("//table[@id='owners']/tbody/tr[2]/td[1]").string("Eduardo Davidson"));
	}

	@Test
	void testOwnerSearchEndpointWithLastNameCaseInsensitive() throws Exception {
		mockMvc.perform(get("/owners").param("lastName", "franklin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/1")); // George Franklin
	}

	@Test
	void testOwnerSearchEndpointWithLastNameNoMatch() throws Exception {
		mockMvc.perform(get("/owners").param("lastName", "nonexistent"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/findOwners"))
				.andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "lastName"));
	}

	@Test
	void testOwnerSearchEndpointWithLastNameEmptyString() throws Exception {
		mockMvc.perform(get("/owners").param("lastName", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("listOwners"))
				.andExpect(xpath("//table[@id='owners']/tbody/tr").nodeCount(10)); // All owners
	}

	@Test
	void testOwnerSearchEndpointWithPagination() throws Exception {
		mockMvc.perform(get("/owners").param("page", "2").param("lastName", ""))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"))
				.andExpect(model().attributeExists("listOwners"))
				.andExpect(model().attribute("currentPage", 2));
				// Further assertions could check the content of the second page
	}
}
