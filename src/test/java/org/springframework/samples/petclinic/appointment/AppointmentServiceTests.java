package org.springframework.samples.petclinic.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTests {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment appointment1;
    private Appointment appointment2;

    @BeforeEach
    void setUp() {
        org.springframework.samples.petclinic.owner.Owner owner1 = new org.springframework.samples.petclinic.owner.Owner();
        owner1.setId(1);
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");

        org.springframework.samples.petclinic.owner.Pet pet1 = new org.springframework.samples.petclinic.owner.Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setOwner(owner1);

        org.springframework.samples.petclinic.owner.Owner owner2 = new org.springframework.samples.petclinic.owner.Owner();
        owner2.setId(2);
        owner2.setFirstName("Betty");
        owner2.setLastName("Davis");

        org.springframework.samples.petclinic.owner.Pet pet2 = new org.springframework.samples.petclinic.owner.Pet();
        pet2.setId(2);
        pet2.setName("Max");
        pet2.setOwner(owner2);

        appointment1 = new Appointment();
        appointment1.setId(1);
        appointment1.setAppointmentDateTime(LocalDateTime.of(2023, 10, 26, 10, 0));
        appointment1.setPet(pet1);
        appointment1.setDescription("Routine check-up");
        appointment1.setStatus(AppointmentStatus.SCHEDULED);

        appointment2 = new Appointment();
        appointment2.setId(2);
        appointment2.setAppointmentDateTime(LocalDateTime.of(2023, 11, 15, 14, 30));
        appointment2.setPet(pet2);
        appointment2.setDescription("Vaccination");
        appointment2.setStatus(AppointmentStatus.COMPLETED);
    }

    @Test
    void testFindAppointmentsByCriteria_allNull() {
        AppointmentFilterCriteria criteria = new AppointmentFilterCriteria();
        when(appointmentRepository.findAppointmentsByCriteria(null, null, null, null, null))
            .thenReturn(Arrays.asList(appointment1, appointment2));

        List<Appointment> result = appointmentService.findAppointmentsByCriteria(criteria);

        assertThat(result).containsExactlyInAnyOrder(appointment1, appointment2);
        verify(appointmentRepository).findAppointmentsByCriteria(null, null, null, null, null);
    }

    @Test
    void testFindAppointmentsByCriteria_byStatus() {
        AppointmentFilterCriteria criteria = new AppointmentFilterCriteria();
        criteria.setStatus(AppointmentStatus.SCHEDULED);
        when(appointmentRepository.findAppointmentsByCriteria(null, null, AppointmentStatus.SCHEDULED, null, null))
            .thenReturn(Collections.singletonList(appointment1));

        List<Appointment> result = appointmentService.findAppointmentsByCriteria(criteria);

        assertThat(result).containsExactly(appointment1);
        verify(appointmentRepository).findAppointmentsByCriteria(null, null, AppointmentStatus.SCHEDULED, null, null);
    }

    @Test
    void testFindAppointmentsByCriteria_byOwnerLastName() {
        AppointmentFilterCriteria criteria = new AppointmentFilterCriteria();
        criteria.setOwnerLastName("Franklin");
        when(appointmentRepository.findAppointmentsByCriteria(null, null, null, "Franklin", null))
            .thenReturn(Collections.singletonList(appointment1));

        List<Appointment> result = appointmentService.findAppointmentsByCriteria(criteria);

        assertThat(result).containsExactly(appointment1);
        verify(appointmentRepository).findAppointmentsByCriteria(null, null, null, "Franklin", null);
    }

    @Test
    void testFindAppointmentsByCriteria_byDateTimeRange() {
        LocalDateTime start = LocalDateTime.of(2023, 11, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 11, 30, 23, 59);
        AppointmentFilterCriteria criteria = new AppointmentFilterCriteria();
        criteria.setStartDateTime(start);
        criteria.setEndDateTime(end);
        when(appointmentRepository.findAppointmentsByCriteria(start, end, null, null, null))
            .thenReturn(Collections.singletonList(appointment2));

        List<Appointment> result = appointmentService.findAppointmentsByCriteria(criteria);

        assertThat(result).containsExactly(appointment2);
        verify(appointmentRepository).findAppointmentsByCriteria(start, end, null, null, null);
    }

    @Test
    void testFindAppointmentsByCriteria_combinedCriteria() {
        LocalDateTime start = LocalDateTime.of(2023, 10, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 31, 23, 59);
        AppointmentFilterCriteria criteria = new AppointmentFilterCriteria();
        criteria.setStartDateTime(start);
        criteria.setEndDateTime(end);
        criteria.setStatus(AppointmentStatus.SCHEDULED);
        criteria.setOwnerLastName("Franklin");
        criteria.setPetName("Leo");

        when(appointmentRepository.findAppointmentsByCriteria(
            eq(start), eq(end), eq(AppointmentStatus.SCHEDULED), eq("Franklin"), eq("Leo")))
            .thenReturn(Collections.singletonList(appointment1));

        List<Appointment> result = appointmentService.findAppointmentsByCriteria(criteria);

        assertThat(result).containsExactly(appointment1);
        verify(appointmentRepository).findAppointmentsByCriteria(
            eq(start), eq(end), eq(AppointmentStatus.SCHEDULED), eq("Franklin"), eq("Leo"));
    }

    @Test
    void testFindAppointmentsByCriteria_noResults() {
        AppointmentFilterCriteria criteria = new AppointmentFilterCriteria();
        criteria.setOwnerLastName("NonExistent");
        when(appointmentRepository.findAppointmentsByCriteria(null, null, null, "NonExistent", null))
            .thenReturn(Collections.emptyList());

        List<Appointment> result = appointmentService.findAppointmentsByCriteria(criteria);

        assertThat(result).isEmpty();
        verify(appointmentRepository).findAppointmentsByCriteria(null, null, null, "NonExistent", null);
    }
}
