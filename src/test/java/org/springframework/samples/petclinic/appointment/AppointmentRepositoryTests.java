package org.springframework.samples.petclinic.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.Specialty;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AppointmentRepositoryTests {

	@Autowired
	private AppointmentRepository appointmentRepository;

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

		// Setup PetTypes
		PetType cat = new PetType();
		cat.setName("cat");

		PetType dog = new PetType();
		dog.setName("dog");

		// Setup Pets
		pet1 = new Pet();
		pet1.setName("Leo");
		pet1.setBirthDate(LocalDate.of(2020, 1, 1));
		pet1.setType(cat);
		owner1.addPet(pet1);

		pet2 = new Pet();
		pet2.setName("Max");
		pet2.setBirthDate(LocalDate.of(2019, 5, 10));
		pet2.setType(dog);
		owner2.addPet(pet2);

		// Setup Specialties
		Specialty surgery = new Specialty();
		surgery.setName("surgery");

		// Setup Vets
		vet1 = new Vet();
		vet1.setFirstName("James");
		vet1.setLastName("Carter");
		vet1.addSpecialty(surgery);

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
		appointmentRepository.save(appt1);

		appt2 = new Appointment();
		appt2.setDate(LocalDate.of(2023, 10, 27));
		appt2.setDescription("Vaccination");
		appt2.setPet(pet2);
		appt2.setVet(vet2);
		appt2.setStatus(AppointmentStatus.COMPLETED);
		appointmentRepository.save(appt2);

		appt3 = new Appointment();
		appt3.setDate(LocalDate.of(2023, 10, 26));
		appt3.setDescription("Dental cleaning");
		appt3.setPet(pet2);
		appt3.setVet(vet1);
		appt3.setStatus(AppointmentStatus.SCHEDULED);
		appointmentRepository.save(appt3);
	}

	@Test
	void findByDate() {
		Collection<Appointment> appointments = appointmentRepository.findByDate(LocalDate.of(2023, 10, 26));
		assertThat(appointments).hasSize(2);
		assertThat(appointments).contains(appt1, appt3);
	}

	@Test
	void findByPetNameContainingIgnoreCase() {
		Collection<Appointment> appointments = appointmentRepository.findByPetNameContainingIgnoreCase("leo");
		assertThat(appointments).hasSize(1);
		assertThat(appointments).contains(appt1);
	}

	@Test
	void findByOwnerLastNameContainingIgnoreCase() {
		Collection<Appointment> appointments = appointmentRepository.findByOwnerLastNameContainingIgnoreCase("franklin");
		assertThat(appointments).hasSize(1);
		assertThat(appointments).contains(appt1);
	}

	@Test
	void findByVetLastNameContainingIgnoreCase() {
		Collection<Appointment> appointments = appointmentRepository.findByVetLastNameContainingIgnoreCase("carter");
		assertThat(appointments).hasSize(2);
		assertThat(appointments).contains(appt1, appt3);
	}

	@Test
	void findByStatus() {
		Collection<Appointment> appointments = appointmentRepository.findByStatus(AppointmentStatus.SCHEDULED);
		assertThat(appointments).hasSize(2);
		assertThat(appointments).contains(appt1, appt3);
	}

	@Test
	void findByNonExistentCriteria() {
		Collection<Appointment> appointments = appointmentRepository.findByDate(LocalDate.of(2024, 1, 1));
		assertThat(appointments).isEmpty();

		appointments = appointmentRepository.findByPetNameContainingIgnoreCase("nonexistent");
		assertThat(appointments).isEmpty();
	}

}
