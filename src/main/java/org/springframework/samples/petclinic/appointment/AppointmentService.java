package org.springframework.samples.petclinic.appointment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Appointment> findAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Appointment findAppointmentById(int id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Transactional
    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteAppointment(int id) {
        appointmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Appointment> findAppointmentsByCriteria(AppointmentFilterCriteria criteria) {
        return appointmentRepository.findAppointmentsByCriteria(
                criteria.getStartDateTime(),
                criteria.getEndDateTime(),
                criteria.getStatus(),
                criteria.getOwnerLastName(),
                criteria.getPetName()
        );
    }
}
