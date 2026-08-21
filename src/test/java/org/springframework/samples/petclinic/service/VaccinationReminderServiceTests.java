package org.springframework.samples.petclinic.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Vaccination;
import org.springframework.samples.petclinic.owner.VaccinationRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class VaccinationReminderServiceTests {

    @Mock
    private VaccinationRepository vaccinationRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private VaccinationReminderService vaccinationReminderService;

    private Owner owner;
    private Pet pet;
    private Vaccination vaccinationTwoWeeks;
    private Vaccination vaccinationThreeDays;
    private Vaccination vaccinationPastDue;
    private Vaccination vaccinationFarFuture;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setEmail("john.doe@example.com");
        owner.setReceivesVaccinationReminders(true);

        pet = new Pet();
        pet.setId(10);
        pet.setName("Buddy");
        pet.setOwner(owner);

        vaccinationTwoWeeks = new Vaccination();
        vaccinationTwoWeeks.setId(100);
        vaccinationTwoWeeks.setType("Rabies");
        vaccinationTwoWeeks.setDueDate(LocalDate.now().plusWeeks(2));
        vaccinationTwoWeeks.setPet(pet);

        vaccinationThreeDays = new Vaccination();
        vaccinationThreeDays.setId(101);
        vaccinationThreeDays.setType("Distemper");
        vaccinationThreeDays.setDueDate(LocalDate.now().plusDays(3));
        vaccinationThreeDays.setPet(pet);

        vaccinationPastDue = new Vaccination();
        vaccinationPastDue.setId(102);
        vaccinationPastDue.setType("Parvo");
        vaccinationPastDue.setDueDate(LocalDate.now().minusDays(5));
        vaccinationPastDue.setPet(pet);

        vaccinationFarFuture = new Vaccination();
        vaccinationFarFuture.setId(103);
        vaccinationFarFuture.setType("Lepto");
        vaccinationFarFuture.setDueDate(LocalDate.now().plusMonths(3));
        vaccinationFarFuture.setPet(pet);
    }

    @Test
    void shouldSendRemindersForVaccinationsDueInTwoWeeksAndThreeDays() {
        // Mock repository calls for 2-week and 3-day windows
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), eq(LocalDate.now().plusWeeks(2))))
                .thenReturn(Arrays.asList(vaccinationTwoWeeks));
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), eq(LocalDate.now().plusDays(3))))
                .thenReturn(Arrays.asList(vaccinationThreeDays));

        when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        vaccinationReminderService.sendVaccinationReminders();

        // Verify notification service was called for both reminders
        verify(notificationService, times(1)).sendHtmlEmail(
                eq(owner.getEmail()), eq("Upcoming Vaccination Reminder (2 Weeks)"), eq("vaccinationReminder"), anyMap());
        verify(notificationService, times(1)).sendHtmlEmail(
                eq(owner.getEmail()), eq("Upcoming Vaccination Reminder (3 Days)"), eq("vaccinationReminder"), anyMap());
    }

    @Test
    void shouldNotSendRemindersIfOwnerUnsubscribed() {
        owner.setReceivesVaccinationReminders(false);
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(vaccinationTwoWeeks));
        when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        vaccinationReminderService.sendVaccinationReminders();

        verify(notificationService, never()).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldNotSendRemindersIfOwnerHasNoEmail() {
        owner.setEmail(null);
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(vaccinationTwoWeeks));
        when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        vaccinationReminderService.sendVaccinationReminders();

        verify(notificationService, never()).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldNotSendRemindersForVaccinationsOutsideWindow() {
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), eq(LocalDate.now().plusWeeks(2))))
                .thenReturn(Collections.emptyList());
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), eq(LocalDate.now().plusDays(3))))
                .thenReturn(Collections.emptyList());

        vaccinationReminderService.sendVaccinationReminders();

        verify(notificationService, never()).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldHandleMissingOwnerGracefully() {
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(vaccinationTwoWeeks));
        when(ownerRepository.findById(owner.getId())).thenReturn(Optional.empty());

        vaccinationReminderService.sendVaccinationReminders();

        verify(notificationService, never()).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldHandleVaccinationWithoutPetGracefully() {
        vaccinationTwoWeeks.setPet(null);
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(vaccinationTwoWeeks));

        vaccinationReminderService.sendVaccinationReminders();

        verify(notificationService, never()).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void shouldContinueProcessingEvenIfOneEmailFails() {
        // Simulate two vaccinations due
        Vaccination anotherVaccination = new Vaccination();
        anotherVaccination.setId(104);
        anotherVaccination.setType("Another Vac");
        anotherVaccination.setDueDate(LocalDate.now().plusWeeks(2));
        anotherVaccination.setPet(pet);

        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList(vaccinationTwoWeeks, anotherVaccination));
        when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));

        // Make the first email send fail
        doThrow(new RuntimeException("Email send failed")).when(notificationService).sendHtmlEmail(
                eq(owner.getEmail()), eq("Upcoming Vaccination Reminder (2 Weeks)"), eq("vaccinationReminder"), anyMap());

        vaccinationReminderService.sendVaccinationReminders();

        // Verify that notificationService was still attempted for both, even if one failed
        verify(notificationService, times(2)).sendHtmlEmail(
                eq(owner.getEmail()), eq("Upcoming Vaccination Reminder (2 Weeks)"), eq("vaccinationReminder"), anyMap());
    }
}