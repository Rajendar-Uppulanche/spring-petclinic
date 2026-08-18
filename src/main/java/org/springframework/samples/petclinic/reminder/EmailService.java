package org.springframework.samples.petclinic.reminder;

import java.time.LocalDate;

public interface EmailService {
    /**
     * Sends a vaccination reminder email to an owner.
     *
     * @param recipientEmail The email address of the recipient.
     * @param ownerName The full name of the owner.
     * @param petName The name of the pet.
     * @param vaccinationType The type of vaccination.
     * @param dueDate The due date of the vaccination.
     * @param unsubscribeLink A link for the owner to unsubscribe from reminders.
     */
    void sendVaccinationReminderEmail(String recipientEmail, String ownerName, String petName, String vaccinationType, LocalDate dueDate, String unsubscribeLink);
}
