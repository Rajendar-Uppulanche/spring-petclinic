package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.system.EmailService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaccinationReminderServiceTests {

    @Mock
    private VaccinationRepository vaccinationRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VaccinationReminderService vaccinationReminderService;

    private Owner owner;
    private Pet pet;
    private Vaccination vaccinationTwoWeeks;
    private Vaccination vaccinationThreeDays;

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
        pet.setBirthDate(LocalDate.of(2020, 1, 1));

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
    }

    @Test
    void shouldSendTwoWeekReminders() {
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(vaccinationTwoWeeks));
        when(ownerRepository.findByPetId(pet.getId())).thenReturn(owner);

        vaccinationReminderService.sendTwoWeekReminders();

        ArgumentCaptor<String> emailBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1)).sendEmail(eq(owner.getEmail()), anyString(), emailBodyCaptor.capture());

        String emailBody = emailBodyCaptor.getValue();
        assertThat(emailBody).contains("Dear John,");
        assertThat(emailBody).contains("This is a 2-week reminder that your pet, Buddy, is due for its Rabies vaccination on " + LocalDate.now().plusWeeks(2) + ".");
        assertThat(emailBody).contains("You can book an appointment online at: [BOOKING_LINK_PLACEHOLDER]"); 
    }

    @Test
    void shouldSendThreeDayReminders() {
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(vaccinationThreeDays));
        when(ownerRepository.findByPetId(pet.getId())).thenReturn(owner);

        vaccinationReminderService.sendThreeDayReminders();

        ArgumentCaptor<String> emailBodyCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1)).sendEmail(eq(owner.getEmail()), anyString(), emailBodyCaptor.capture());

        String emailBody = emailBodyCaptor.getValue();
        assertThat(emailBody).contains("Dear John,");
        assertThat(emailBody).contains("This is a 3-day reminder that your pet, Buddy, is due for its Distemper vaccination on " + LocalDate.now().plusDays(3) + ".");
        assertThat(emailBody).contains("You can book an appointment online at: [BOOKING_LINK_PLACEHOLDER]"); 
    }

    @Test
    void shouldNotSendReminderIfOwnerUnsubscribed() {
        owner.setReceivesVaccinationReminders(false);
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(vaccinationTwoWeeks));
        when(ownerRepository.findByPetId(pet.getId())).thenReturn(owner);

        vaccinationReminderService.sendTwoWeekReminders();

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldNotSendReminderIfOwnerHasNoEmail() {
        owner.setEmail(null);
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(vaccinationTwoWeeks));
        when(ownerRepository.findByPetId(pet.getId())).thenReturn(owner);

        vaccinationReminderService.sendTwoWeekReminders();

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldNotSendReminderIfPetOrOwnerNotFound() {
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Arrays.asList(vaccinationTwoWeeks));
        when(ownerRepository.findByPetId(pet.getId())).thenReturn(null); 

        vaccinationReminderService.sendTwoWeekReminders();

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldHandleNoUpcomingVaccinations() {
        when(vaccinationRepository.findByDueDateBetween(any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(Collections.emptyList());

        vaccinationReminderService.sendTwoWeekReminders();
        vaccinationReminderService.sendThreeDayReminders();

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}