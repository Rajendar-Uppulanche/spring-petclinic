package org.springframework.samples.petclinic.reminder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VaccinationReminderJobTests {

    @Mock
    private SchedulerService schedulerService;

    @InjectMocks
    private VaccinationReminderJob vaccinationReminderJob;

    @Test
    void dailyVaccinationReminderCheckCallsSchedulerService() {
        vaccinationReminderJob.dailyVaccinationReminderCheck();
        verify(schedulerService, times(1)).runVaccinationReminders();
    }
}
