package org.springframework.samples.petclinic.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.VaccinationReminderRepository;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class VaccinationReminderServiceTest {

	@Mock
	private VaccinationReminderRepository vaccinationReminderRepository;

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private EmailService emailService;

	@InjectMocks
	private VaccinationReminderService vaccinationReminderService;

	private Owner owner1;

	private Owner owner2;

	private Pet pet1;

	private Pet pet2;

	@BeforeEach
	void setUp() {
		owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");
		owner1.setEmail("george@example.com");
		owner1.setUnsubscribePreference(false);

		owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");
		owner2.setEmail("betty@example.com");
		owner2.setUnsubscribePreference(true); // Unsubscribed

		pet1 = new Pet();
		pet1.setId(10);
		pet1.setName("Leo");
		pet1.setOwner(owner1);
		pet1.setVaccinationDueDate(LocalDate.now().plusWeeks(2));

		pet2 = new Pet();
		pet2.setId(11);
		pet2.setName("Max");
		pet2.setOwner(owner2);
		pet2.setVaccinationDueDate(LocalDate.now().plusWeeks(2));

		ReflectionTestUtils.setField(vaccinationReminderService, "emailSubject", "Vaccination Reminder");
	}

	@Test
	void findPetsForReminderShouldReturnPetsInDateRange() {
		LocalDate startDate = LocalDate.now().plusWeeks(2);
		LocalDate endDate = startDate.plusDays(1);
		when(vaccinationReminderRepository.findPetsWithUpcomingVaccinations(startDate, endDate))
			.thenReturn(Arrays.asList(pet1, pet2));

		List<Pet> pets = vaccinationReminderService.findPetsForReminder(startDate, endDate);

		verify(vaccinationReminderRepository).findPetsWithUpcomingVaccinations(startDate, endDate);
		assert (pets.size() == 2);
		assert (pets.contains(pet1));
		assert (pets.contains(pet2));
	}

	@Test
	void sendVaccinationRemindersShouldSendEmailToSubscribedOwner() throws MailException {
		List<Pet> pets = Collections.singletonList(pet1);

		vaccinationReminderService.sendVaccinationReminders(pets);

		verify(emailService).sendVaccinationReminderEmail(eq(owner1.getEmail()), eq("Vaccination Reminder"), eq(pet1));
	}

	@Test
	void sendVaccinationRemindersShouldNotSendEmailToUnsubscribedOwner() throws MailException {
		List<Pet> pets = Collections.singletonList(pet2);

		vaccinationReminderService.sendVaccinationReminders(pets);

		verify(emailService, never()).sendVaccinationReminderEmail(any(), any(), any());
	}

	@Test
	void sendVaccinationRemindersShouldHandleMailException() throws MailException {
		List<Pet> pets = Collections.singletonList(pet1);
		doThrow(new MailException("Test mail error") {
		}).when(emailService).sendVaccinationReminderEmail(any(), any(), any());

		vaccinationReminderService.sendVaccinationReminders(pets);

		verify(emailService).sendVaccinationReminderEmail(eq(owner1.getEmail()), eq("Vaccination Reminder"), eq(pet1));
		// Expect no exception to be thrown, just logged
	}

	@Test
	void unsubscribeOwnerShouldUpdatePreference() {
		when(ownerRepository.findById(owner1.getId())).thenReturn(owner1);

		vaccinationReminderService.unsubscribeOwner(owner1.getId());

		assert (owner1.isUnsubscribePreference());
		verify(ownerRepository).save(owner1);
	}

	@Test
	void unsubscribeOwnerShouldDoNothingIfOwnerNotFound() {
		when(ownerRepository.findById(999)).thenReturn(null);

		vaccinationReminderService.unsubscribeOwner(999);

		verify(ownerRepository, never()).save(any(Owner.class));
	}

}