package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.system.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VaccinationReminderService {

    private final VaccinationRepository vaccinationRepository;
    private final OwnerRepository ownerRepository;
    private final EmailService emailService;

    public VaccinationReminderService(VaccinationRepository vaccinationRepository,
                                      OwnerRepository ownerRepository,
                                      EmailService emailService) {
        this.vaccinationRepository = vaccinationRepository;
        this.ownerRepository = ownerRepository;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public void sendTwoWeekReminders() {
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksFromNow = today.plusWeeks(2);
        List<Vaccination> upcomingVaccinations = vaccinationRepository.findByDueDateBetween(today.plusDays(1), twoWeeksFromNow); 

        for (Vaccination vaccination : upcomingVaccinations) {
            sendReminderEmail(vaccination, "2-week");
        }
    }

    @Transactional(readOnly = true)
    public void sendThreeDayReminders() {
        LocalDate today = LocalDate.now();
        LocalDate threeDaysFromNow = today.plusDays(3);
        List<Vaccination> upcomingVaccinations = vaccinationRepository.findByDueDateBetween(today.plusDays(1), threeDaysFromNow); 

        for (Vaccination vaccination : upcomingVaccinations) {
            sendReminderEmail(vaccination, "3-day");
        }
    }

    private void sendReminderEmail(Vaccination vaccination, String reminderType) {
        Pet pet = vaccination.getPet();
        if (pet == null) {
            return;
        }
        Owner owner = ownerRepository.findByPetId(pet.getId()); 
        if (owner == null) {
            return;
        }

        if (owner.getReceivesVaccinationReminders() && owner.getEmail() != null && !owner.getEmail().isEmpty()) {
            String subject = String.format("Vaccination Reminder for %s - %s", pet.getName(), vaccination.getType());
            String body = String.format(
                "Dear %s,\n\n" +
                "This is a %s reminder that your pet, %s, is due for its %s vaccination on %s.\n\n" +
                "Please ensure your pet receives this vaccination to stay healthy.\n" +
                "You can book an appointment online at: [BOOKING_LINK_PLACEHOLDER]\n\n" + 
                "Thank you,\n" +
                "The PetClinic Team",
                owner.getFirstName(), reminderType, pet.getName(), vaccination.getType(), vaccination.getDueDate()
            );
            emailService.sendEmail(owner.getEmail(), subject, body);
        }
    }
}