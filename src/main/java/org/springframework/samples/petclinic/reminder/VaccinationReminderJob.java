package org.springframework.samples.petclinic.reminder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VaccinationReminderJob {

    private static final Logger logger = LoggerFactory.getLogger(VaccinationReminderJob.class);

    private final SchedulerService schedulerService;

    public VaccinationReminderJob(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    /**
     * Scheduled task to run daily and trigger the vaccination reminder logic.
     * The cron expression is configurable via 'vaccination.reminder.schedule.cron' property.
     * Defaults to 2 AM daily.
     */
    @Scheduled(cron = "${vaccination.reminder.schedule.cron:0 0 2 * * ?}")
    public void dailyVaccinationReminderCheck() {
        logger.info("Scheduled job 'dailyVaccinationReminderCheck' triggered.");
        schedulerService.runVaccinationReminders();
    }
}
