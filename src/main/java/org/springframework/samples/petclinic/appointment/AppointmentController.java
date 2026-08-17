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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Controller for {@link Appointment}s.
 *
 * @author Wick Dynex
 */
@Controller
class AppointmentController {

	private final AppointmentService appointmentService;
	private final OwnerRepository ownerRepository;
	private final PetRepository petRepository;
	private final VetRepository vetRepository;

	public AppointmentController(AppointmentService appointmentService, OwnerRepository ownerRepository, PetRepository petRepository, VetRepository vetRepository) {
		this.appointmentService = appointmentService;
		this.ownerRepository = ownerRepository;
		this.petRepository = petRepository;
		this.vetRepository = vetRepository;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("statuses")
	public List<String> populateStatuses() {
		return Arrays.asList("PENDING", "CONFIRMED", "CANCELLED");
	}

	@GetMapping("/appointments")
	public String showAppointmentList(
		@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
		@RequestParam(value = "petName", required = false) String petName,
		@RequestParam(value = "ownerLastName", required = false) String ownerLastName,
		@RequestParam(value = "vetLastName", required = false) String vetLastName,
		@RequestParam(value = "status", required = false) String status,
		Model model) {

		Collection<Appointment> appointments = appointmentService.findAppointments(date, petName, ownerLastName, vetLastName, status);
		model.addAttribute("appointments", appointments);
		model.addAttribute("date", date);
		model.addAttribute("petName", petName);
		model.addAttribute("ownerLastName", ownerLastName);
		model.addAttribute("vetLastName", vetLastName);
		model.addAttribute("status", status);

		return "appointments/appointmentList";
	}

}