package org.springframework.samples.petclinic.owner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.springframework.samples.petclinic.vet.Veterinarian;
import org.springframework.samples.petclinic.vet.VeterinarianRepository;

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

	@MockitoBean
	private VeterinarianRepository veterinarianRepository;

	private Owner owner;
	private Pet pet;
	private Veterinarian veterinarian1;
	private Veterinarian veterinarian2;

	@BeforeEach
	void init() {
		owner = new Owner();
		owner.setId(TEST_OWNER_ID);
		pet = new Pet();
		pet.setId(TEST_PET_ID);
		owner.addPet(pet);
		given(this.owners.findById(TEST_OWNER_ID)).willReturn(Optional.of(owner));

		veterinarian1 = new Veterinarian();
		veterinarian1.setId(1);
		veterinarian1.setFirstName("John");
		veterinarian1.setLastName("Doe");
		veterinarian1.setTelephone("111-222-3333");

		veterinarian2 = new Veterinarian();
		veterinarian2.setId(2);
		veterinarian2.setFirstName("Jane");
		veterinarian2.setLastName("Smith");
		veterinarian2.setTelephone("444-555-6666");

		given(veterinarianRepository.findAll()).willReturn(Arrays.asList(veterinarian1, veterinarian2));
	}

	@Test
	void initNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("veterinarians"))
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormSuccess() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "George")
				.param("date", LocalDate.now().plusDays(1).toString())
				.param("description", "Visit Description")
				.param("veterinarian.id", "1"))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	void processNewVisitFormHasErrors() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID).param("name",
					"George"))
			.andExpect(model().attributeHasErrors("visit"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void processNewVisitFormHasErrorsWhenVisitDateIsNotInFuture() throws Exception {
		mockMvc
			.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("name", "George")
				.param("date", LocalDate.now().toString())
				.param("description", "Visit Description"))
			.andExpect(model().attributeHasFieldErrors("visit", "date"))
			.andExpect(model().attributeHasFieldErrorCode("visit", "date", "typeMismatch.visitDate"))
			.andExpect(status().isOk())
			.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	void getFilteredVisitsNoFilters() throws Exception {
		Visit visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.now().plusDays(1));
		visit1.setDescription("Routine checkup");
		visit1.setPet(pet);
		visit1.setVeterinarian(veterinarian1);

		Visit visit2 = new Visit();
		visit2.setId(2);
		visit2.setDate(LocalDate.now().plusDays(2));
		visit2.setDescription("Vaccination");
		visit2.setPet(pet);
		visit2.setVeterinarian(veterinarian2);

		given(visitService.findFilteredVisits(any(), any(), any(), any(), any(), any()))
			.willReturn(Arrays.asList(visit1, visit2));

		mockMvc.perform(get("/api/visits").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].description").value("Routine checkup"))
			.andExpect(jsonPath("$[1].description").value("Vaccination"));
	}

	@Test
	void getFilteredVisitsWithVeterinarianFilter() throws Exception {
		Visit visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.now().plusDays(1));
		visit1.setDescription("Routine checkup");
		visit1.setPet(pet);
		visit1.setVeterinarian(veterinarian1);

		given(visitService.findFilteredVisits(eq(null), eq(null), eq(1), any(), any(), eq(null)))
			.willReturn(Collections.singletonList(visit1));

		mockMvc.perform(get("/api/visits")
				.param("veterinarianId", "1")
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].veterinarian.firstName").value("John"));
	}

	@Test
	void getAllVeterinariansApi() throws Exception {
		mockMvc.perform(get("/api/veterinarians").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].firstName").value("John"))
			.andExpect(jsonPath("$[1].firstName").value("Jane"));
	}
}