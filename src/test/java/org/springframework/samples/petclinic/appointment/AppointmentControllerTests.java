package org.springframework.samples.petclinic.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AppointmentService appointmentService;

	private Appointment appt1;
	private Appointment appt2;

	@BeforeEach
	void setup() {
		// Setup Owners
		Owner owner1 = new Owner();
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");

		// Setup Pets
		Pet pet1 = new Pet();
		pet1.setName("Leo");
		owner1.addPet(pet1);

		// Setup Vets
		Vet vet1 = new Vet();
		vet1.setFirstName("James");
		vet1.setLastName("Carter");

		// Setup Appointments
		appt1 = new Appointment();
		appt1.setDate(LocalDate.of(2023, 10, 26));
		appt1.setDescription("Annual check-up");
		appt1.setPet(pet1);
		appt1.setVet(vet1);
		appt1.setStatus(AppointmentStatus.SCHEDULED);

		appt2 = new Appointment();
		appt2.setDate(LocalDate.of(2023, 10, 27));
		appt2.setDescription("Vaccination");
		appt2.setPet(pet1);
		appt2.setVet(vet1);
		appt2.setStatus(AppointmentStatus.COMPLETED);

		when(appointmentService.findAllAppointments()).thenReturn(Arrays.asList(appt1, appt2));
	}

	@Test
	void testShowAppointmentList() throws Exception {
		mockMvc.perform(get("/appointments"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("appointments"))
				.andExpect(view().name("appointments/appointment_management"));
	}

	@Test
	void testSearchAppointmentsByDate() throws Exception {
		LocalDate searchDate = LocalDate.of(2023, 10, 26);
		when(appointmentService.searchAppointments(searchDate, null, null, null, null))
				.thenReturn(Collections.singletonList(appt1));

		mockMvc.perform(get("/appointments/search").param("date", searchDate.toString()))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("appointments"))
				.andExpect(model().attribute("appointments", Collections.singletonList(appt1)))
				.andExpect(model().attribute("selectedDate", searchDate))
				.andExpect(view().name("appointments/appointment_management"));
	}

	@Test
	void testSearchAppointmentsByPetName() throws Exception {
		String petName = "Leo";
		when(appointmentService.searchAppointments(null, petName, null, null, null))
				.thenReturn(Arrays.asList(appt1, appt2));

		mockMvc.perform(get("/appointments/search").param("petName", petName))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("appointments"))
				.andExpect(model().attribute("appointments", Arrays.asList(appt1, appt2)))
				.andExpect(model().attribute("selectedPetName", petName))
				.andExpect(view().name("appointments/appointment_management"));
	}

	@Test
	void testSearchAppointmentsNoResults() throws Exception {
		when(appointmentService.searchAppointments(any(), any(), any(), any(), any()))
				.thenReturn(Collections.emptyList());

		mockMvc.perform(get("/appointments/search").param("date", "2025-01-01"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("appointments"))
				.andExpect(model().attribute("appointments", Collections.emptyList()))
				.andExpect(view().name("appointments/appointment_management"));
	}

}
