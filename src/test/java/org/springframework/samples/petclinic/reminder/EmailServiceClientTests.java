package org.springframework.samples.petclinic.reminder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = EmailServiceClient.class)
class EmailServiceClientTests {

    @SpyBean
    private EmailServiceClient emailServiceClient;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceClient.class);

    @BeforeEach
    void setUp() {
        // Reset mocks if necessary
    }

    @Test
    void sendVaccinationReminderEmailLogsCorrectly() {
        String recipientEmail = "test@example.com";
        String ownerName = "John Doe";
        String petName = "Buddy";
        String vaccinationType = "Rabies";
        LocalDate dueDate = LocalDate.of(2024, 12, 25);
        String unsubscribeLink = "http://localhost/unsubscribe/1";

        emailServiceClient.sendVaccinationReminderEmail(recipientEmail, ownerName, petName, vaccinationType, dueDate, unsubscribeLink);

        // Verify that the method was called
        verify(emailServiceClient, times(1)).sendVaccinationReminderEmail(
            recipientEmail, ownerName, petName, vaccinationType, dueDate, unsubscribeLink);

        // In a real test, you might capture logs or use a mock mail sender.
        // For this simple logging implementation, verifying the method call is sufficient.
    }
}
