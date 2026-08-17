package org.springframework.samples.petclinic.visit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VisitController.class)
class VisitControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VisitService visitService;

	@MockBean
	private OwnerRepository ownerRepository;

	@MockBean
	private VetRepository vetRepository;

	private ObjectMapper objectMapper;

	private Pet pet1;
	private Owner owner1;
	private Vet vet1;
	private Visit visit1;
	private Visit visit2;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");

		pet1 = new Pet();
		pet1.setId(10);
		pet1.setName("Leo");
		pet1.setOwner(owner1);
		owner1.addPet(pet1);

		vet1 = new Vet();
		vet1.setId(100);
		vet1.setFirstName("James");
		vet1.setLastName("Carter");

		visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.of(2023, 1, 15));
		visit1.setDescription("Routine checkup");
		visit1.setPet(pet1);
		visit1.setVet(vet1);

		visit2 = new Visit();
		visit2.setId(2);
		visit2.setDate(LocalDate.of(2023, 1, 20));
		visit2.setDescription("Vaccination");
		visit2.setPet(pet1);
		visit2.setVet(vet1);
	}

	@Test
	void testGetFilteredVisits_NoFilters() throws Exception {
		given(visitService.findVisitsByCriteria(null, null, null, null, null))
			.willReturn(Arrays.asList(visit1, visit2));

		mockMvc.perform(get("/api/visits").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].description").value("Routine checkup"))
			.andExpect(jsonPath("$[1].description").value("Vaccination"));
	}

	@Test
	void testGetFilteredVisits_ByPetId() throws Exception {
		given(visitService.findVisitsByCriteria(null, null, pet1.getId(), null, null))
			.willReturn(Arrays.asList(visit1, visit2));

		mockMvc.perform(get("/api/visits").param("petId", pet1.getId().toString()).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].pet.id").value(pet1.getId()));
	}

	@Test
	void testGetFilteredVisits_ByVetId() throws Exception {
		given(visitService.findVisitsByCriteria(null, null, null, null, vet1.getId()))
			.willReturn(Arrays.asList(visit1, visit2));

		mockMvc.perform(get("/api/visits").param("vetId", vet1.getId().toString()).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2))
			.andExpect(jsonPath("$[0].vet.id").value(vet1.getId()));
	}

	@Test
	void testGetFilteredVisits_ByDateRange() throws Exception {
		LocalDate startDate = LocalDate.of(2023, 1, 10);
		LocalDate endDate = LocalDate.of(2023, 1, 16);
		given(visitService.findVisitsByCriteria(startDate, endDate, null, null, null))
			.willReturn(Collections.singletonList(visit1));

		mockMvc
			.perform(get("/api/visits").param("startDate", startDate.toString())
				.param("endDate", endDate.toString())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].date").value(visit1.getDate().toString()));
	}

	@Test
	void testCreateVisit() throws Exception {
		Visit newVisit = new Visit();
		newVisit.setDate(LocalDate.now().plusDays(1));
		newVisit.setDescription("New visit description");
		newVisit.setVet(vet1);

		given(ownerRepository.findById(owner1.getId())).willReturn(Optional.of(owner1));
		given(vetRepository.findById(vet1.getId())).willReturn(Optional.of(vet1));
		given(visitService.saveVisit(any(Visit.class))).willAnswer(invocation -> {
			Visit savedVisit = invocation.getArgument(0);
			savedVisit.setId(3);
			return savedVisit;
		});

		mockMvc
			.perform(post("/api/visits/owners/{ownerId}/pets/{petId}", owner1.getId(), pet1.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newVisit)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(3))
			.andExpect(jsonPath("$.description").value("New visit description"))
			.andExpect(jsonPath("$.pet.id").value(pet1.getId()))
			.andExpect(jsonPath("$.vet.id").value(vet1.getId()));
	}

	@Test
	void testGetVisitById() throws Exception {
		given(visitService.findVisitById(visit1.getId())).willReturn(visit1);

		mockMvc.perform(get("/api/visits/{visitId}", visit1.getId()).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(visit1.getId()))
			.andExpect(jsonPath("$.description").value(visit1.getDescription()));
	}

	@Test
	void testGetVisitById_NotFound() throws Exception {
		given(visitService.findVisitById(999)).willReturn(null);

		mockMvc.perform(get("/api/visits/{visitId}", 999).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound());
	}
}
