package org.springframework.samples.petclinic.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Vaccination;
import org.springframework.samples.petclinic.owner.VaccinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VaccinationReminderService {

    private static final Logger logger = LoggerFactory.getLogger(VaccinationReminderService.class);

    private final VaccinationRepository vaccinationRepository;
    private final OwnerRepository ownerRepository;
    private final NotificationService notificationService;

    public VaccinationReminderService(VaccinationRepository vaccinationRepository,
                                      OwnerRepository ownerRepository,
                                      NotificationService notificationService) {
        this.vaccinationRepository = vaccinationRepository;
        this.ownerRepository = ownerRepository;
        this.notificationService = notificationService;
    }

    // Scheduled to run daily at a specific time (e.g., 2 AM)
    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2 AM
    @Transactional(readOnly = true)
    public void sendVaccinationReminders() {
        logger.info("Starting vaccination reminder job...");

        LocalDate today = LocalDate.now();
        LocalDate twoWeeksFromNow = today.plusWeeks(2);
        LocalDate threeDaysFromNow = today.plusDays(3);

        // Find vaccinations due in 2 weeks
        List<Vaccination> twoWeekReminders = vaccinationRepository.findByDueDateBetween(today.plusDays(1), twoWeeksFromNow);
        sendRemindersForVaccinations(twoWeekReminders, "Upcoming Vaccination Reminder (2 Weeks)");

        // Find vaccinations due in 3 days
        List<Vaccination> threeDayReminders = vaccinationRepository.findByDueDateBetween(today.plusDays(1), threeDaysFromNow);
        sendRemindersForVaccinations(threeDayReminders, "Upcoming Vaccination Reminder (3 Days)");

        logger.info("Vaccination reminder job finished.");
    }

    private void sendRemindersForVaccinations(List<Vaccination> vaccinations, String subject) {
        for (Vaccination vaccination : vaccinations) {
            Pet pet = vaccination.getPet();
            if (pet == null) {
                logger.warn("Vaccination {} has no associated pet. Skipping reminder.", vaccination.getId());
                continue;
            }
            Optional<Owner> optionalOwner = ownerRepository.findById(pet.getOwner().getId());
            if (optionalOwner.isEmpty()) {
                logger.warn("Owner for pet {} (vaccination {}) not found. Skipping reminder.", pet.getId(), vaccination.getId());
                continue;
            }
            Owner owner = optionalOwner.get();

            if (owner.isReceivesVaccinationReminders() && owner.getEmail() != null && !owner.getEmail().isBlank()) {
                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("ownerFirstName", owner.getFirstName());
                templateVariables.put("petName", pet.getName());
                templateVariables.put("vaccinationType", vaccination.getType());
                templateVariables.put("dueDate", vaccination.getDueDate());
                templateVariables.put("ownerId", owner.getId());

                try {
                    notificationService.sendHtmlEmail(owner.getEmail(), subject, "vaccinationReminder", templateVariables);
                    logger.info("Sent reminder for vaccination {} to owner {}", vaccination.getId(), owner.getId());
                } catch (Exception e) {
                    logger.error("Failed to send reminder email for vaccination {} to owner {}: {}", vaccination.getId(), owner.getId(), e.getMessage());
                }
            } else {
                logger.info("Owner {} does not receive reminders or has no email. Skipping reminder for vaccination {}.", owner.getId(), vaccination.getId());
            }
        }
    }
}