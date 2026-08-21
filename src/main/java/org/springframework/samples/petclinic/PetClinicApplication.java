package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; 

/**
 * PetClinic Spring Boot Application.
 *
 * @author Dave Syer
 * @author Maciej Walkowiak
 * @author Bassem Nouisser
 */
@SpringBootApplication
@EnableScheduling 
public class PetClinicApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }

}
