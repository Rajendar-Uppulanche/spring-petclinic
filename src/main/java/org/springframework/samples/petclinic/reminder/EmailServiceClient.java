package org.springframework.samples.petclinic.reminder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailServiceClient implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceClient.class);

    @Override
    public void sendVaccinationReminderEmail(String recipientEmail, String ownerName, String petName, String vaccinationType, LocalDate dueDate, String unsubscribeLink) {
        // In a real application, this would integrate with an actual email sending library (e.g., JavaMailSender)
        // For this exercise, we'll just log the email content.
        logger.info("Sending vaccination reminder email to: {}", recipientEmail);
        logger.info("Subject: Upcoming Vaccination for {}'s {}", ownerName, petName);
        logger.info("Body: Dear {}, your pet {} is due for a {} vaccination on {}. Please schedule an appointment soon.", ownerName, petName, vaccinationType, dueDate);
        logger.info("Unsubscribe link: {}", unsubscribeLink);
        // Simulate potential email sending failure for error handling testing
        // if (Math.random() < 0.1) {
        //     throw new RuntimeException("Simulated email sending failure");
        // }
    }
}
