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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetRepository;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link AppointmentController}
 *
 * @author Wick Dynex
 */
@WebMvcTest(AppointmentController.class)
class AppointmentControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AppointmentService appointmentService;

	@MockBean
	private OwnerRepository ownerRepository;

	@MockBean
	private PetRepository petRepository;

	@MockBean
	private VetRepository vetRepository;

	private Appointment appointment1;
	private Appointment appointment2;

	@BeforeEach
	void setup() {
		Owner owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");

		Pet pet1 = new Pet();
		pet1.setId(1);
		pet1.setName("Leo");

		Vet vet1 = new Vet();
		vet1.setId(1);
		vet1.setFirstName("James");
		vet1.setLastName("Carter");

		appointment1 = new Appointment();
		appointment1.setId(1);
		appointment1.setDate(LocalDate.of(2024, 1, 1));
		appointment1.setDescription("Routine checkup");
		appointment1.setPet(pet1);
		appointment1.setOwner(owner1);
		appointment1.setVet(vet1);
		appointment1.setStatus("CONFIRMED");

		Owner owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");

		Pet pet2 = new Pet();
		pet2.setId(2);
		pet2.setName("Basil");

		Vet vet2 = new Vet();
		vet2.setId(2);
		vet2.setFirstName("Helen");
		vet2.setLastName("Leary");

		appointment2 = new Appointment();
		appointment2.setId(2);
		appointment2.setDate(LocalDate.of(2024, 1, 2));
		appointment2.setDescription("Vaccination");
		appointment2.setPet(pet2);
		appointment2.setOwner(owner2);
		appointment2.setVet(vet2);
		appointment2.setStatus("PENDING");

		given(this.appointmentService.findAllAppointments()).willReturn(Arrays.asList(appointment1, appointment2));
		given(this.appointmentService.findAppointments(any(), any(), any(), any(), any()))
			.willReturn(Arrays.asList(appointment1, appointment2));
	}

	@Test
	void testShowAppointmentListWithoutFilters() throws Exception {
		mockMvc.perform(get("/appointments"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("appointments"))
			.andExpect(view().name("appointments/appointmentList"));
	}

	@Test
	void testShowAppointmentListWithDateFilter() throws Exception {
		given(this.appointmentService.findAppointments(eq(LocalDate.of(2024, 1, 1)), any(), any(), any(), any()))
			.willReturn(Collections.singletonList(appointment1));

		mockMvc.perform(get("/appointments").param("date", "2024-01-01"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("appointments"))
			.andExpect(model().attribute("appointments", Collections.singletonList(appointment1)))
			.andExpect(view().name("appointments/appointmentList"));
	}

	@Test
	void testShowAppointmentListWithPetNameFilter() throws Exception {
		given(this.appointmentService.findAppointments(any(), eq("Leo"), any(), any(), any()))
			.willReturn(Collections.singletonList(appointment1));

		mockMvc.perform(get("/appointments").param("petName", "Leo"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("appointments"))
			.andExpect(model().attribute("appointments", Collections.singletonList(appointment1)))
			.andExpect(view().name("appointments/appointmentList"));
	}

	@Test
	void testShowAppointmentListWithOwnerLastNameFilter() throws Exception {
		given(this.appointmentService.findAppointments(any(), any(), eq("Franklin"), any(), any()))
			.willReturn(Collections.singletonList(appointment1));

		mockMvc.perform(get("/appointments").param("ownerLastName", "Franklin"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("appointments"))
			.andExpect(model().attribute("appointments", Collections.singletonList(appointment1)))
			.andExpect(view().name("appointments/appointmentList"));
	}

	@Test
	void testShowAppointmentListWithVetLastNameFilter() throws Exception {
		given(this.appointmentService.findAppointments(any(), any(), any(), eq("Carter"), any()))
			.willReturn(Collections.singletonList(appointment1));

		mockMvc.perform(get("/appointments").param("vetLastName", "Carter"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("appointments"))
			.andExpect(model().attribute("appointments", Collections.singletonList(appointment1)))
			.andExpect(view().name("appointments/appointmentList"));
	}

	@Test
	void testShowAppointmentListWithStatusFilter() throws Exception {
		given(this.appointmentService.findAppointments(any(), any(), any(), any(), eq("CONFIRMED")))
			.willReturn(Collections.singletonList(appointment1));

		mockMvc.perform(get("/appointments").param("status", "CONFIRMED"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("appointments"))
			.andExpect(model().attribute("appointments", Collections.singletonList(appointment1)))
			.andExpect(view().name("appointments/appointmentList"));
	}

	@Test
	void testShowAppointmentListWithMultipleFilters() throws Exception {
		given(this.appointmentService.findAppointments(eq(LocalDate.of(2024, 1, 1)), eq("Leo"), eq("Franklin"), eq("Carter"), eq("CONFIRMED")))
			.willReturn(Collections.singletonList(appointment1));

		mockMvc.perform(get("/appointments")
				.param("date", "2024-01-01")
				.param("petName", "Leo")
				.param("ownerLastName", "Franklin")
				.param("vetLastName", "Carter")
				.param("status", "CONFIRMED"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("appointments"))
			.andExpect(model().attribute("appointments", Collections.singletonList(appointment1)))
			.andExpect(view().name("appointments/appointmentList"));
	}

	@Test
	void testShowAppointmentListNoResults() throws Exception {
		given(this.appointmentService.findAppointments(any(), any(), any(), any(), any()))
			.willReturn(Collections.emptyList());

		mockMvc.perform(get("/appointments").param("petName", "NonExistentPet"))
			.andExpect(status().isOk())
			.andExpect(model().attributeExists("appointments"))
			.andExpect(model().attribute("appointments", Collections.emptyList()))
			.andExpect(view().name("appointments/appointmentList"));
	}

}