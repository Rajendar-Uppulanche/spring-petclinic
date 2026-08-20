package org.springframework.samples.petclinic.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendSimpleMessage(String to, String subject, String text) throws MailException {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}

	public void sendVaccinationReminderEmail(String to, String subject, Pet pet) throws MailException {
		// In a real application, this would use a templating engine (e.g., Thymeleaf)
		// to generate a rich HTML email. For simplicity, we'll use a plain text message.
		String emailBody = String.format(
				"Dear %s,\n\nThis is a friendly reminder that your pet %s's vaccination is due on %s.\n\n" +
						"Please contact us to book an appointment. You can reach us at 555-1234.\n\n" +
						"If you wish to unsubscribe from these reminders, please click here: [UNSUBSCRIBE_LINK_PLACEHOLDER]\n\n" +
						"Sincerely,\nThe PetClinic Team",
				pet.getOwner().getFirstName(), pet.getName(), pet.getVaccinationDueDate());

		// TODO: Replace [UNSUBSCRIBE_LINK_PLACEHOLDER] with an actual unsubscribe link
		// For now, we'll just send the plain text.

		sendSimpleMessage(to, subject, emailBody);
	}

}