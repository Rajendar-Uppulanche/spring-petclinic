package org.springframework.samples.petclinic.reminder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Vaccination;
import org.springframework.samples.petclinic.owner.VaccinationRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaccinationReminderServiceTests {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private VaccinationRepository vaccinationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VaccinationReminderService vaccinationReminderService;

    private Owner owner;
    private Pet pet;
    private Vaccination vaccination14Days;
    private Vaccination vaccination3Days;
    private Vaccination vaccinationNoReminder;
    private Vaccination vaccinationNoDueDate;

    @BeforeEach
    void setUp() {
        // Set reminder intervals for testing
        ReflectionTestUtils.setField(vaccinationReminderService, "firstReminderDays", 14);
        ReflectionTestUtils.setField(vaccinationReminderService, "secondReminderDays", 3);

        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setReceivesVaccinationReminders(true);
        owner.setAddress("123 Main St");
        owner.setCity("Anytown");
        owner.setTelephone("5551234567");

        pet = new Pet();
        pet.setId(1);
        pet.setName("Buddy");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        pet.setType(new PetType());
        pet.getType().setName("dog");
        owner.addPet(pet);

        vaccination14Days = new Vaccination();
        vaccination14Days.setId(1);
        vaccination14Days.setType("Rabies");
        vaccination14Days.setAdministrationDate(LocalDate.now().minusMonths(11));
        vaccination14Days.setDueDate(LocalDate.now().plusDays(14));
        vaccination14Days.setPet(pet);

        vaccination3Days = new Vaccination();
        vaccination3Days.setId(2);
        vaccination3Days.setType("Distemper");
        vaccination3Days.setAdministrationDate(LocalDate.now().minusMonths(5));
        vaccination3Days.setDueDate(LocalDate.now().plusDays(3));
        vaccination3Days.setPet(pet);

        vaccinationNoReminder = new Vaccination();
        vaccinationNoReminder.setId(3);
        vaccinationNoReminder.setType("Parvo");
        vaccinationNoReminder.setAdministrationDate(LocalDate.now().minusMonths(1));
        vaccinationNoReminder.setDueDate(LocalDate.now().plusDays(7)); // Not 3 or 14 days
        vaccinationNoReminder.setPet(pet);

        vaccinationNoDueDate = new Vaccination();
        vaccinationNoDueDate.setId(4);
        vaccinationNoDueDate.setType("Lepto");
        vaccinationNoDueDate.setAdministrationDate(LocalDate.now().minusMonths(2));
        vaccinationNoDueDate.setDueDate(null); // Missing due date
        vaccinationNoDueDate.setPet(pet);

        Set<Vaccination> vaccinations = new HashSet<>();
        vaccinations.add(vaccination14Days);
        vaccinations.add(vaccination3Days);
        vaccinations.add(vaccinationNoReminder);
        vaccinations.add(vaccinationNoDueDate);
        pet.setVaccinations(vaccinations);
    }

    @Test
    void runVaccinationRemindersSendsEmailsForUpcomingVaccinations() {
        when(ownerRepository.findByReceivesVaccinationReminders(true)).thenReturn(List.of(owner));

        vaccinationReminderService.runVaccinationReminders();

        // Verify emailService was called for 14-day reminder
        verify(emailService, times(1)).sendVaccinationReminderEmail(
            eq("John.Doe@example.com"),
            eq("John Doe"),
            eq("Buddy"),
            eq("Rabies"),
            eq(LocalDate.now().plusDays(14)),
            anyString()
        );

        // Verify emailService was called for 3-day reminder
        verify(emailService, times(1)).sendVaccinationReminderEmail(
            eq("John.Doe@example.com"),
            eq("John Doe"),
            eq("Buddy"),
            eq("Distemper"),
            eq(LocalDate.now().plusDays(3)),
            anyString()
        );

        // Verify emailService was NOT called for vaccinationNoReminder
        verify(emailService, never()).sendVaccinationReminderEmail(
            anyString(), anyString(), anyString(), eq("Parvo"), any(LocalDate.class), anyString()
        );

        // Verify emailService was NOT called for vaccinationNoDueDate
        verify(emailService, never()).sendVaccinationReminderEmail(
            anyString(), anyString(), anyString(), eq("Lepto"), any(LocalDate.class), anyString()
        );
    }

    @Test
    void runVaccinationRemindersDoesNotSendIfOwnerUnsubscribed() {
        owner.setReceivesVaccinationReminders(false);
        when(ownerRepository.findByReceivesVaccinationReminders(true)).thenReturn(Collections.emptyList());

        vaccinationReminderService.runVaccinationReminders();

        verify(emailService, never()).sendVaccinationReminderEmail(anyString(), anyString(), anyString(), anyString(), any(LocalDate.class), anyString());
    }

    @Test
    void runVaccinationRemindersHandlesEmailSendingFailure() {
        when(ownerRepository.findByReceivesVaccinationReminders(true)).thenReturn(List.of(owner));
        // Simulate email sending failure for the first reminder
        when(emailService.sendVaccinationReminderEmail(
            anyString(), anyString(), anyString(), eq("Rabies"), any(LocalDate.class), anyString()))
            .thenThrow(new RuntimeException("Email server down"));

        vaccinationReminderService.runVaccinationReminders();

        // Verify that the first email attempt was made and failed
        verify(emailService, times(1)).sendVaccinationReminderEmail(
            eq("John.Doe@example.com"),
            eq("John Doe"),
            eq("Buddy"),
            eq("Rabies"),
            eq(LocalDate.now().plusDays(14)),
            anyString()
        );
        // Verify that the second email (for Distemper) was still attempted and succeeded (no exception for it)
        verify(emailService, times(1)).sendVaccinationReminderEmail(
            eq("John.Doe@example.com"),
            eq("John Doe"),
            eq("Buddy"),
            eq("Distemper"),
            eq(LocalDate.now().plusDays(3)),
            anyString()
        );
    }

    @Test
    void runVaccinationRemindersGeneratesCorrectUnsubscribeLink() {
        when(ownerRepository.findByReceivesVaccinationReminders(true)).thenReturn(List.of(owner));

        vaccinationReminderService.runVaccinationReminders();

        ArgumentCaptor<String> unsubscribeLinkCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(2)).sendVaccinationReminderEmail(
            anyString(), anyString(), anyString(), anyString(), any(LocalDate.class), unsubscribeLinkCaptor.capture());

        List<String> capturedLinks = unsubscribeLinkCaptor.getAllValues();
        assertThat(capturedLinks).containsExactlyInAnyOrder(
            "http://localhost:8080/owners/1/unsubscribeReminders",
            "http://localhost:8080/owners/1/unsubscribeReminders"
        );
    }

    @Test
    void runVaccinationRemindersNoVaccinations() {
        pet.setVaccinations(Collections.emptySet());
        when(ownerRepository.findByReceivesVaccinationReminders(true)).thenReturn(List.of(owner));

        vaccinationReminderService.runVaccinationReminders();

        verify(emailService, never()).sendVaccinationReminderEmail(anyString(), anyString(), anyString(), anyString(), any(LocalDate.class), anyString());
    }

    @Test
    void runVaccinationRemindersNoPets() {
        owner.getPets().clear();
        when(ownerRepository.findByReceivesVaccinationReminders(true)).thenReturn(List.of(owner));

        vaccinationReminderService.runVaccinationReminders();

        verify(emailService, never()).sendVaccinationReminderEmail(anyString(), anyString(), anyString(), anyString(), any(LocalDate.class), anyString());
    }
}
