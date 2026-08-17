package org.springframework.samples.petclinic.appointment;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collection;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	@GetMapping({"", "/list"})
	public String showAppointmentList(Model model) {
		model.addAttribute("appointments", appointmentService.findAllAppointments());
		model.addAttribute("appointmentStatuses", AppointmentStatus.values());
		return "appointments/appointment_management";
	}

	@GetMapping("/search")
	public String searchAppointments(
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
			@RequestParam(required = false) String petName,
			@RequestParam(required = false) String ownerLastName,
			@RequestParam(required = false) String vetLastName,
			@RequestParam(required = false) AppointmentStatus status,
			Model model) {

		Collection<Appointment> results = appointmentService.searchAppointments(date, petName, ownerLastName, vetLastName, status);
		model.addAttribute("appointments", results);
		model.addAttribute("appointmentStatuses", AppointmentStatus.values());
		model.addAttribute("selectedDate", date);
		model.addAttribute("selectedPetName", petName);
		model.addAttribute("selectedOwnerLastName", ownerLastName);
		model.addAttribute("selectedVetLastName", vetLastName);
		model.addAttribute("selectedStatus", status);

		return "appointments/appointment_management";
	}

}
