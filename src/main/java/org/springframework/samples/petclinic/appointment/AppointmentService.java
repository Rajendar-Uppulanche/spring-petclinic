/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.appointment;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service class for {@link Appointment} related operations.
 *
 * @author Wick Dynex
 */
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
	public Optional<Appointment> findAppointmentById(Integer id) {
		return appointmentRepository.findById(id);
	}

	@Transactional
	public void saveAppointment(Appointment appointment) {
		appointmentRepository.save(appointment);
	}

	@Transactional(readOnly = true)
	public Collection<Appointment> findAppointments(
		LocalDate date,
		String petName,
		String ownerLastName,
		String vetLastName,
		String status) {

		Specification<Appointment> spec = (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (date != null) {
				predicates.add(cb.equal(root.get("date"), date));
			}
			if (petName != null && !petName.isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("pet").get("name")), "%" + petName.toLowerCase() + "%"));
			}
			if (ownerLastName != null && !ownerLastName.isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("owner").get("lastName")), "%" + ownerLastName.toLowerCase() + "%"));
			}
			if (vetLastName != null && !vetLastName.isEmpty()) {
				predicates.add(cb.like(cb.lower(root.get("vet").get("lastName")), "%" + vetLastName.toLowerCase() + "%"));
			}
			if (status != null && !status.isEmpty()) {
				predicates.add(cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};

		return appointmentRepository.findAll(spec);
	}
}