package org.springframework.samples.petclinic.appointment;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @ModelAttribute("statuses")
    public AppointmentStatus[] populateAppointmentStatuses() {
        return AppointmentStatus.values();
    }

    @GetMapping({"", "/list"})
    public String listAppointments(@ModelAttribute("filterCriteria") AppointmentFilterCriteria filterCriteria, Model model) {
        List<Appointment> appointments = appointmentService.findAppointmentsByCriteria(filterCriteria);
        model.addAttribute("appointments", appointments);
        if (filterCriteria.getStartDateTime() == null && filterCriteria.getEndDateTime() == null &&
            filterCriteria.getStatus() == null && filterCriteria.getOwnerLastName() == null &&
            filterCriteria.getPetName() == null) {
            model.addAttribute("filterCriteria", new AppointmentFilterCriteria());
        } else {
            model.addAttribute("filterCriteria", filterCriteria);
        }
        return "appointments/list";
    }
}
