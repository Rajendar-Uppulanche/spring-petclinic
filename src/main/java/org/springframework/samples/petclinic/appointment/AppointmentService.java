package org.springframework.samples.petclinic.appointment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;

	public AppointmentService(AppointmentRepository appointmentRepository) {
		this.appointmentRepository = appointmentRepository;
	}

	@Transactional(readOnly = true)
	public Collection<Appointment> findAllAppointments() {
		return appointmentRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Collection<Appointment> searchAppointments(LocalDate date, String petName, String ownerLastName,
			String vetLastName, AppointmentStatus status) {

		Set<Appointment> results = new HashSet<>(appointmentRepository.findAll());

		if (date != null) {
			results.retainAll(appointmentRepository.findByDate(date));
		}
		if (petName != null && !petName.isEmpty()) {
			results.retainAll(appointmentRepository.findByPetNameContainingIgnoreCase(petName));
		}
		if (ownerLastName != null && !ownerLastName.isEmpty()) {
			results.retainAll(appointmentRepository.findByOwnerLastNameContainingIgnoreCase(ownerLastName));
		}
		if (vetLastName != null && !vetLastName.isEmpty()) {
			results.retainAll(appointmentRepository.findByVetLastNameContainingIgnoreCase(vetLastName));
		}
		if (status != null) {
			results.retainAll(appointmentRepository.findByStatus(status));
		}

		return results.stream().sorted((a1, a2) -> a1.getDate().compareTo(a2.getDate()))
				.collect(Collectors.toList());
	}

	@Transactional
	public void saveAppointment(Appointment appointment) {
		appointmentRepository.save(appointment);
	}

}
