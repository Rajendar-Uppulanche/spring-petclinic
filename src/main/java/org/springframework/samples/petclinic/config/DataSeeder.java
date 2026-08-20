package org.springframework.samples.petclinic.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.vaccination.VaccineType;
import org.springframework.samples.petclinic.vaccination.VaccineTypeRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test") // Do not run in test profile, as tests might manage their own data
public class DataSeeder implements CommandLineRunner {

    private final VaccineTypeRepository vaccineTypeRepository;

    public DataSeeder(VaccineTypeRepository vaccineTypeRepository) {
        this.vaccineTypeRepository = vaccineTypeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedVaccineTypes();
    }

    private void seedVaccineTypes() {
        List<VaccineType> initialVaccineTypes = Arrays.asList(
            createVaccineType("Rabies", 365), // 1 year
            createVaccineType("Distemper", 365 * 3), // 3 years
            createVaccineType("Parvovirus", 365 * 3), // 3 years
            createVaccineType("Bordetella", 365), // 1 year
            createVaccineType("Feline Leukemia", 365) // 1 year
        );

        for (VaccineType type : initialVaccineTypes) {
            if (vaccineTypeRepository.findByName(type.getName()).isEmpty()) {
                vaccineTypeRepository.save(type);
            }
        }
    }

    private VaccineType createVaccineType(String name, Integer defaultNextDueIntervalDays) {
        VaccineType type = new VaccineType();
        type.setName(name);
        type.setDefaultNextDueIntervalDays(defaultNextDueIntervalDays);
        return type;
    }
}
