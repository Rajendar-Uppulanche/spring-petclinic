package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class VaccinationRepositoryTests {

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Test
    @Transactional
    void shouldSaveAndFindVaccination() {
        // Create an owner and a pet
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Anytown");
        owner.setTelephone("5551234567");
        owner.setEmail("john.doe@example.com");
        owner.setReceivesVaccinationReminders(true);
        ownerRepository.save(owner);

        Pet pet = new Pet();
        pet.setName("Buddy");
        pet.setBirthDate(LocalDate.of(2020, 1, 1));
        // Assuming PetType is handled elsewhere or can be null for this test
        owner.addPet(pet);
        ownerRepository.save(owner);

        // Create a vaccination
        Vaccination vaccination = new Vaccination();
        vaccination.setType("Rabies");
        vaccination.setDueDate(LocalDate.of(2024, 10, 26));
        vaccination.setPet(pet);

        // Save vaccination
        vaccinationRepository.save(vaccination);
        assertThat(vaccination.getId()).isNotNull();

        // Find vaccination by ID
        Optional<Vaccination> foundVaccination = vaccinationRepository.findById(vaccination.getId());
        assertThat(foundVaccination).isPresent();
        assertThat(foundVaccination.get().getType()).isEqualTo("Rabies");
        assertThat(foundVaccination.get().getPet().getName()).isEqualTo("Buddy");
    }

    @Test
    @Transactional
    void shouldFindByPetId() {
        // Create an owner and two pets
        Owner owner = new Owner();
        owner.setFirstName("Jane");
        owner.setLastName("Smith");
        owner.setAddress("456 Oak Ave");
        owner.setCity("Otherville");
        owner.setTelephone("5559876543");
        ownerRepository.save(owner);

        Pet pet1 = new Pet();
        pet1.setName("Max");
        pet1.setBirthDate(LocalDate.of(2019, 5, 10));
        owner.addPet(pet1);

        Pet pet2 = new Pet();
        pet2.setName("Bella");
        pet2.setBirthDate(LocalDate.of(2021, 3, 15));
        owner.addPet(pet2);
        ownerRepository.save(owner);

        // Add vaccinations to pet1
        Vaccination vac1 = new Vaccination();
        vac1.setType("Distemper");
        vac1.setDueDate(LocalDate.of(2024, 11, 1));
        vac1.setPet(pet1);
        vaccinationRepository.save(vac1);

        Vaccination vac2 = new Vaccination();
        vac2.setType("Parvo");
        vac2.setDueDate(LocalDate.of(2025, 1, 15));
        vac2.setPet(pet1);
        vaccinationRepository.save(vac2);

        // Add vaccination to pet2
        Vaccination vac3 = new Vaccination();
        vac3.setType("Rabies");
        vac3.setDueDate(LocalDate.of(2024, 12, 1));
        vac3.setPet(pet2);
        vaccinationRepository.save(vac3);

        List<Vaccination> pet1Vaccinations = vaccinationRepository.findByPetId(pet1.getId());
        assertThat(pet1Vaccinations).hasSize(2);
        assertThat(pet1Vaccinations).extracting(Vaccination::getType).containsExactlyInAnyOrder("Distemper", "Parvo");

        List<Vaccination> pet2Vaccinations = vaccinationRepository.findByPetId(pet2.getId());
        assertThat(pet2Vaccinations).hasSize(1);
        assertThat(pet2Vaccinations).extracting(Vaccination::getType).containsExactly("Rabies");
    }

    @Test
    @Transactional
    void shouldFindByDueDateBetween() {
        // Create an owner and a pet
        Owner owner = new Owner();
        owner.setFirstName("Alice");
        owner.setLastName("Wonder");
        owner.setAddress("789 Wonderland");
        owner.setCity("Fantasy");
        owner.setTelephone("5551112222");
        ownerRepository.save(owner);

        Pet pet = new Pet();
        pet.setName("Cheshire");
        pet.setBirthDate(LocalDate.of(2022, 2, 2));
        owner.addPet(pet);
        ownerRepository.save(owner);

        // Add vaccinations with different due dates
        Vaccination vacEarly = new Vaccination();
        vacEarly.setType("Flu");
        vacEarly.setDueDate(LocalDate.of(2024, 9, 1));
        vacEarly.setPet(pet);
        vaccinationRepository.save(vacEarly);

        Vaccination vacMid = new Vaccination();
        vacMid.setType("Lepto");
        vacMid.setDueDate(LocalDate.of(2024, 10, 15));
        vacMid.setPet(pet);
        vaccinationRepository.save(vacMid);

        Vaccination vacLate = new Vaccination();
        vacLate.setType("Bordetella");
        vacLate.setDueDate(LocalDate.of(2024, 11, 30));
        vacLate.setPet(pet);
        vaccinationRepository.save(vacLate);

        LocalDate startDate = LocalDate.of(2024, 10, 1);
        LocalDate endDate = LocalDate.of(2024, 10, 31);

        List<Vaccination> foundVaccinations = vaccinationRepository.findByDueDateBetween(startDate, endDate);
        assertThat(foundVaccinations).hasSize(1);
        assertThat(foundVaccinations.get(0).getType()).isEqualTo("Lepto");

        startDate = LocalDate.of(2024, 9, 1);
        endDate = LocalDate.of(2024, 11, 30);
        foundVaccinations = vaccinationRepository.findByDueDateBetween(startDate, endDate);
        assertThat(foundVaccinations).hasSize(3);
        assertThat(foundVaccinations).extracting(Vaccination::getType).containsExactlyInAnyOrder("Flu", "Lepto", "Bordetella");
    }
}