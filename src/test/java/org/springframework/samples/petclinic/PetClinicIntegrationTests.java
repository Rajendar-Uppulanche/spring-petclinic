package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PetClinicIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void testOwnerCreationAndRetrievalByUserId() throws Exception {
		mockMvc.perform(post("/owners/new")
				.param("firstName", "Integration")
				.param("lastName", "Test")
				.param("address", "123 Test St")
				.param("city", "Testville")
				.param("telephone", "1112223333")
				.param("userId", "integration.test.user"))
				.andExpect(status().is3xxRedirection());

		mockMvc.perform(get("/owners/user/{userId}", "integration.test.user"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("owner"))
				.andExpect(model().attribute("owner", org.hamcrest.Matchers.hasProperty("firstName", org.hamcrest.Matchers.is("Integration"))))
				.andExpect(model().attribute("owner", org.hamcrest.Matchers.hasProperty("lastName", org.hamcrest.Matchers.is("Test"))))
				.andExpect(model().attribute("owner", org.hamcrest.Matchers.hasProperty("userId", org.hamcrest.Matchers.is("integration.test.user"))))
				.andExpect(view().name("owners/ownerDetails"));
	}

	@Test
	void testOwnerUpdateWithUserId() throws Exception {
		mockMvc.perform(get("/owners/1"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("owner"));

		mockMvc.perform(post("/owners/1/edit")
				.param("firstName", "George")
				.param("lastName", "Franklin")
				.param("address", "110 W. Liberty St.")
				.param("city", "Madison")
				.param("telephone", "6085551023")
				.param("userId", "george.franklin.updated"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/1"));

		mockMvc.perform(get("/owners/user/{userId}", "george.franklin.updated"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("owner"))
				.andExpect(model().attribute("owner", org.hamcrest.Matchers.hasProperty("userId", org.hamcrest.Matchers.is("george.franklin.updated"))));
	}

	@Test
	void testOwnerListIncludesUserId() throws Exception {
		mockMvc.perform(get("/owners"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("listOwners"))
				.andExpect(model().attribute("listOwners", org.hamcrest.Matchers.hasItem(
						org.hamcrest.Matchers.hasProperty("userId", org.hamcrest.Matchers.notNullValue()))));
	}
}
