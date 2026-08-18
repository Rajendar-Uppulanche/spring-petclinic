package org.springframework.samples.petclinic.reminder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.Vaccination;
import org.springframework.samples.petclinic.owner.VaccinationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class VaccinationReminderService implements SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(VaccinationReminderService.class);

    private final OwnerRepository ownerRepository;
    private final VaccinationRepository vaccinationRepository;
    private final EmailService emailService;

    @Value("${vaccination.reminder.first-interval-days:14}")
    private int firstReminderDays; // Default to 14 days (2 weeks)

    @Value("${vaccination.reminder.second-interval-days:3}")
    private int secondReminderDays; // Default to 3 days

    public VaccinationReminderService(OwnerRepository ownerRepository, VaccinationRepository vaccinationRepository, EmailService emailService) {
        this.ownerRepository = ownerRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional // Ensure all operations within this method are part of a single transaction
    public void runVaccinationReminders() {
        logger.info("Starting vaccination reminder job...");
        LocalDate today = LocalDate.now();

        // Find all owners who receive reminders
        List<Owner> owners = ownerRepository.findByReceivesVaccinationReminders(true);

        for (Owner owner : owners) {
            for (Pet pet : owner.getPets()) {
                Set<Vaccination> vaccinations = pet.getVaccinations();
                if (vaccinations == null || vaccinations.isEmpty()) {
                    continue; // No vaccinations for this pet
                }

                for (Vaccination vaccination : vaccinations) {
                    if (vaccination.getDueDate() == null) {
                        logger.warn("Vaccination {} for pet {} (owner {}) has no due date. Skipping reminder.",
                            vaccination.getId(), pet.getName(), owner.getLastName());
                        continue;
                    }

                    long daysUntilDue = today.until(vaccination.getDueDate()).getDays();

                    if (daysUntilDue == firstReminderDays || daysUntilDue == secondReminderDays) {
                        String unsubscribeLink = generateUnsubscribeLink(owner.getId()); // Security concern: use a token in real app
                        try {
                            emailService.sendVaccinationReminderEmail(
                                owner.getFirstName() + "." + owner.getLastName() + "@example.com", // Placeholder email
                                owner.getFirstName() + " " + owner.getLastName(),
                                pet.getName(),
                                vaccination.getType(),
                                vaccination.getDueDate(),
                                unsubscribeLink
                            );
                            logger.info("Sent {}-day reminder for {}'s {} ({} vaccination due on {}) to {}",
                                daysUntilDue, owner.getLastName(), pet.getName(), vaccination.getType(), vaccination.getDueDate(), owner.getFirstName() + "." + owner.getLastName() + "@example.com");
                        } catch (Exception e) {
                            logger.error("Failed to send vaccination reminder email for pet {} (owner {}): {}", pet.getName(), owner.getLastName(), e.getMessage());
                            // Depending on requirements, could retry, store failed emails, etc.
                        }
                    }
                }
            }
        }
        logger.info("Vaccination reminder job finished.");
    }

    private String generateUnsubscribeLink(Integer ownerId) {
        // In a real application, this would be a full URL to the unsubscribe endpoint
        // and ideally include a secure, time-limited token instead of just ownerId.
        return "http://localhost:8080/owners/" + ownerId + "/unsubscribeReminders";
    }
}
