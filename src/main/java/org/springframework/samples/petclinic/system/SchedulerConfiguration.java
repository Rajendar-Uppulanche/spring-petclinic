package org.springframework.samples.petclinic.system;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.samples.petclinic.service.VaccinationReminderService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.samples.petclinic.model.Pet;

@Configuration
@EnableScheduling
public class SchedulerConfiguration {

	private final VaccinationReminderService vaccinationReminderService;

	public SchedulerConfiguration(VaccinationReminderService vaccinationReminderService) {
		this.vaccinationReminderService = vaccinationReminderService;
	}

	@Scheduled(cron = "0 0 9 * * ?") // Every day at 9 AM
	public void sendTwoWeekVaccinationReminders() {
		LocalDate twoWeeksFromNow = LocalDate.now().plusWeeks(2);
		LocalDate twoWeeksAndOneDayFromNow = twoWeeksFromNow.plusDays(1);
		List<Pet> pets = vaccinationReminderService.findPetsForReminder(twoWeeksFromNow, twoWeeksAndOneDayFromNow);
		vaccinationReminderService.sendVaccinationReminders(pets);
		System.out.println("Sent " + pets.size() + " two-week vaccination reminders.");
	}

	@Scheduled(cron = "0 0 9 * * ?") // Every day at 9 AM
	public void sendThreeDayVaccinationReminders() {
		LocalDate threeDaysFromNow = LocalDate.now().plusDays(3);
		LocalDate threeDaysAndOneDayFromNow = threeDaysFromNow.plusDays(1);
		List<Pet> pets = vaccinationReminderService.findPetsForReminder(threeDaysFromNow, threeDaysAndOneDayFromNow);
		vaccinationReminderService.sendVaccinationReminders(pets);
		System.out.println("Sent " + pets.size() + " three-day vaccination reminders.");
	}

}