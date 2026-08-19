/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; 
import org.springframework.scheduling.annotation.Scheduled; 
import org.springframework.samples.petclinic.owner.VaccinationReminderService; 
import org.springframework.beans.factory.annotation.Autowired; 

/**
 * PetClinic Spring Boot Application.
 *
 * @author Dave Syer
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Mark Fisher
 * @author Michael Isvy
 * @author Wick Dynex
 */
@SpringBootApplication
@EnableScheduling 
public class PetClinicApplication {

    @Autowired 
    private VaccinationReminderService vaccinationReminderService; 

	public static void main(String[] args) {
		SpringApplication.run(PetClinicApplication.class, args);
	}

    @Scheduled(cron = "0 0 9 * * *") 
    public void runTwoWeekReminders() {
        vaccinationReminderService.sendTwoWeekReminders();
    }

    @Scheduled(cron = "0 0 10 * * *") 
    public void runThreeDayReminders() {
        vaccinationReminderService.sendThreeDayReminders();
    }
}