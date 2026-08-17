package org.springframework.samples.petclinic.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.vet.Vet;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTests {

	@Mock
	private AppointmentRepository appointmentRepository;

	@InjectMocks
	private AppointmentService appointmentService;

	private Pet pet1;
	private Pet pet2;
	private Vet vet1;
	private Vet vet2;
	private Appointment appt1;
	private Appointment appt2;
	private Appointment appt3;

	@BeforeEach
	void setup() {
		// Setup Owners
		Owner owner1 = new Owner();
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");

		Owner owner2 = new Owner();
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");

		// Setup Pets
		pet1 = new Pet();
		pet1.setName("Leo");
		owner1.addPet(pet1);

		pet2 = new Pet();
		pet2.setName("Max");
		owner2.addPet(pet2);

		// Setup Vets
		vet1 = new Vet();
		vet1.setFirstName("James");
		vet1.setLastName("Carter");

		vet2 = new Vet();
		vet2.setFirstName("Helen");
		vet2.setLastName("Leary");

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
		appt2.setPet(pet2);
		appt2.setVet(vet2);
		appt2.setStatus(AppointmentStatus.COMPLETED);

		appt3 = new Appointment();
		appt3.setDate(LocalDate.of(2023, 10, 26));
		appt3.setDescription("Dental cleaning");
		appt3.setPet(pet2);
		appt3.setVet(vet1);
		appt3.setStatus(AppointmentStatus.SCHEDULED);
	}

	@Test
	void searchAppointmentsByDate() {
		when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2, appt3));
		when(appointmentRepository.findByDate(LocalDate.of(2023, 10, 26))).thenReturn(Arrays.asList(appt1, appt3));

		Collection<Appointment> results = appointmentService.searchAppointments(LocalDate.of(2023, 10, 26), null, null, null, null);
		assertThat(results).containsExactlyInAnyOrder(appt1, appt3);
	}

	@Test
	void searchAppointmentsByPetName() {
		when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2, appt3));
		when(appointmentRepository.findByPetNameContainingIgnoreCase("leo")).thenReturn(Collections.singletonList(appt1));

		Collection<Appointment> results = appointmentService.searchAppointments(null, "leo", null, null, null);
		assertThat(results).containsExactly(appt1);
	}

	@Test
	void searchAppointmentsByOwnerLastName() {
		when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2, appt3));
		when(appointmentRepository.findByOwnerLastNameContainingIgnoreCase("franklin")).thenReturn(Collections.singletonList(appt1));

		Collection<Appointment> results = appointmentService.searchAppointments(null, null, "franklin", null, null);
		assertThat(results).containsExactly(appt1);
	}

	@Test
	void searchAppointmentsByVetLastName() {
		when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2, appt3));
		when(appointmentRepository.findByVetLastNameContainingIgnoreCase("carter")).thenReturn(Arrays.asList(appt1, appt3));

		Collection<Appointment> results = appointmentService.searchAppointments(null, null, null, "carter", null);
		assertThat(results).containsExactlyInAnyOrder(appt1, appt3);
	}

	@Test
	void searchAppointmentsByStatus() {
		when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2, appt3));
		when(appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED)).thenReturn(Arrays.asList(appt1, appt3));

		Collection<Appointment> results = appointmentService.searchAppointments(null, null, null, null, AppointmentStatus.SCHEDULED);
		assertThat(results).containsExactlyInAnyOrder(appt1, appt3);
	}

	@Test
	void searchAppointmentsCombinedCriteria() {
		when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2, appt3));
		when(appointmentRepository.findByDate(LocalDate.of(2023, 10, 26))).thenReturn(Arrays.asList(appt1, appt3));
		when(appointmentRepository.findByPetNameContainingIgnoreCase("max")).thenReturn(Arrays.asList(appt2, appt3));
		when(appointmentRepository.findByVetLastNameContainingIgnoreCase("carter")).thenReturn(Arrays.asList(appt1, appt3));

		Collection<Appointment> results = appointmentService.searchAppointments(LocalDate.of(2023, 10, 26), "max", null, "carter", null);
		assertThat(results).containsExactly(appt3);
	}

	@Test
	void searchAppointmentsNoCriteria() {
		when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2, appt3));

		Collection<Appointment> results = appointmentService.searchAppointments(null, null, null, null, null);
		assertThat(results).containsExactly(appt1, appt3, appt2); // Sorted by date
	}

	@Test
	void searchAppointmentsNoMatch() {
		when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt1, appt2, appt3));
		when(appointmentRepository.findByDate(any(LocalDate.class))).thenReturn(Collections.emptyList());

		Collection<Appointment> results = appointmentService.searchAppointments(LocalDate.of(2025, 1, 1), null, null, null, null);
		assertThat(results).isEmpty();
	}

}
