package org.springframework.samples.petclinic.appointment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    private Appointment appointment1;
    private Appointment appointment2;

    @BeforeEach
    void setup() {
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
    void testListAppointmentsNoFilter() throws Exception {
        given(appointmentService.findAppointmentsByCriteria(any(AppointmentFilterCriteria.class)))
            .willReturn(Arrays.asList(appointment1, appointment2));

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments"))
            .andExpect(status().isOk())
            .andExpect(view().name("appointments/list"))
            .andExpect(model().attributeExists("appointments"))
            .andExpect(model().attribute("appointments", Arrays.asList(appointment1, appointment2)))
            .andExpect(model().attributeExists("filterCriteria"));
    }

    @Test
    void testFilterAppointmentsByStatus() throws Exception {
        given(appointmentService.findAppointmentsByCriteria(any(AppointmentFilterCriteria.class)))
            .willReturn(Collections.singletonList(appointment1));

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments")
            .param("status", AppointmentStatus.SCHEDULED.name()))
            .andExpect(status().isOk())
            .andExpect(view().name("appointments/list"))
            .andExpect(model().attributeExists("appointments"))
            .andExpect(model().attribute("appointments", Collections.singletonList(appointment1)))
            .andExpect(model().attribute("filterCriteria", hasProperty("status", is(AppointmentStatus.SCHEDULED))));
    }

    @Test
    void testFilterAppointmentsByOwnerLastName() throws Exception {
        given(appointmentService.findAppointmentsByCriteria(any(AppointmentFilterCriteria.class)))
            .willReturn(Collections.singletonList(appointment1));

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments")
            .param("ownerLastName", "Franklin"))
            .andExpect(status().isOk())
            .andExpect(view().name("appointments/list"))
            .andExpect(model().attributeExists("appointments"))
            .andExpect(model().attribute("appointments", Collections.singletonList(appointment1)))
            .andExpect(model().attribute("filterCriteria", hasProperty("ownerLastName", is("Franklin"))));
    }

    @Test
    void testFilterAppointmentsByDateTimeRange() throws Exception {
        given(appointmentService.findAppointmentsByCriteria(any(AppointmentFilterCriteria.class)))
            .willReturn(Collections.singletonList(appointment2));

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments")
            .param("startDateTime", "2023-11-01T00:00")
            .param("endDateTime", "2023-11-30T23:59"))
            .andExpect(status().isOk())
            .andExpect(view().name("appointments/list"))
            .andExpect(model().attributeExists("appointments"))
            .andExpect(model().attribute("appointments", Collections.singletonList(appointment2)))
            .andExpect(model().attribute("filterCriteria", hasProperty("startDateTime", is(LocalDateTime.of(2023, 11, 1, 0, 0)))))
            .andExpect(model().attribute("filterCriteria", hasProperty("endDateTime", is(LocalDateTime.of(2023, 11, 30, 23, 59)))));
    }

    @Test
    void testFilterAppointmentsNoResults() throws Exception {
        given(appointmentService.findAppointmentsByCriteria(any(AppointmentFilterCriteria.class)))
            .willReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/appointments")
            .param("ownerLastName", "NonExistent"))
            .andExpect(status().isOk())
            .andExpect(view().name("appointments/list"))
            .andExpect(model().attributeExists("appointments"))
            .andExpect(model().attribute("appointments", Collections.emptyList()));
    }
}
