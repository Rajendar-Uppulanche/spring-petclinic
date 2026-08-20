package org.springframework.samples.petclinic.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.VaccinationReminderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VaccinationReminderService {

	private final VaccinationReminderRepository vaccinationReminderRepository;

	private final OwnerRepository ownerRepository;

	private final EmailService emailService;

	@Value("${petclinic.vaccination.reminder.email.subject}")
	private String emailSubject;

	public VaccinationReminderService(VaccinationReminderRepository vaccinationReminderRepository,
			OwnerRepository ownerRepository, EmailService emailService) {
		this.vaccinationReminderRepository = vaccinationReminderRepository;
		this.ownerRepository = ownerRepository;
		this.emailService = emailService;
	}

	@Transactional(readOnly = true)
	public List<Pet> findPetsForReminder(LocalDate startDate, LocalDate endDate) {
		return vaccinationReminderRepository.findPetsWithUpcomingVaccinations(startDate, endDate);
	}

	@Transactional
	public void sendVaccinationReminders(List<Pet> pets) {
		for (Pet pet : pets) {
			Owner owner = pet.getOwner();
			if (owner != null && !owner.isUnsubscribePreference()) {
				try {
					emailService.sendVaccinationReminderEmail(owner.getEmail(), emailSubject, pet);
				} catch (MailException e) {
					// Log the exception, but continue sending other emails
					System.err.println("Failed to send vaccination reminder email to " + owner.getEmail() + ": " + e.getMessage());
				}
			}
		}
	}

	@Transactional
	public void unsubscribeOwner(int ownerId) {
		Owner owner = ownerRepository.findById(ownerId);
		if (owner != null) {
			owner.setUnsubscribePreference(true);
			ownerRepository.save(owner);
		}
	}

}