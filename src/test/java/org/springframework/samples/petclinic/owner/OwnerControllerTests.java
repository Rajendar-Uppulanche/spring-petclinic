package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
class OwnerControllerTests {

	private static final int TEST_OWNER_ID = 1;
	private static final String TEST_USER_ID = "testuser123";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerRepository owners;

	private Owner george;

	@BeforeEach
	void setup() {
		george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");
		george.setUserId(TEST_USER_ID);

		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(george));
		given(this.owners.findByUserId(TEST_USER_ID)).willReturn(Optional.of(george));
	}

	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new")).andExpect(status().isOk()).andExpect(model().attributeExists("owner"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("address", "123 Main St").param("city", "Springfield").param("telephone", "1234567890")
				.param("userId", "joebloggs456"))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/owners/null"));
	}

	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/new").param("firstName", "Joe").param("lastName", "Bloggs")
				.param("city", "Springfield").param("telephone", "1234567890"))
				.andExpect(status().isOk()).andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "address"))
				.andExpect(model().attributeHasFieldErrors("owner", "userId"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testInitUpdateOwnerForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(model().attributeExists("owner"))
				.andExpect(model().attribute("owner", george))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testProcessUpdateOwnerFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
				.param("lastName", "Franklin").param("address", "110 W. Liberty St.")
				.param("city", "Madison").param("telephone", "6085551023")
				.param("userId", TEST_USER_ID))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "George")
				.param("lastName", "Franklin").param("address", "110 W. Liberty St.")
				.param("city", "Madison").param("telephone", "invalid-phone")
				.param("userId", ""))
				.andExpect(status().isOk()).andExpect(model().attributeHasErrors("owner"))
				.andExpect(model().attributeHasFieldErrors("owner", "telephone"))
				.andExpect(model().attributeHasFieldErrors("owner", "userId"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(model().attribute("owner", george))
				.andExpect(model().attributeExists("owner"))
				.andExpect(view().name("owners/ownerDetails"));
	}

	@Test
	void testShowOwnerByUserId() throws Exception {
		mockMvc.perform(get("/owners/user/{userId}", TEST_USER_ID)).andExpect(status().isOk())
				.andExpect(model().attribute("owner", george))
				.andExpect(model().attributeExists("owner"))
				.andExpect(view().name("owners/ownerDetails"));
	}

	@Test
	void testShowOwnerByUserIdNotFound() throws Exception {
		given(this.owners.findByUserId("nonexistent")).willReturn(Optional.empty());
		mockMvc.perform(get("/owners/user/{userId}", "nonexistent"))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertThat(result.getResolvedException()).isInstanceOf(IllegalArgumentException.class));
	}
}
