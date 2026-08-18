package org.springframework.samples.petclinic.reminder;

public interface SchedulerService {
    /**
     * Runs the logic to identify and send vaccination reminders.
     */
    void runVaccinationReminders();
}
