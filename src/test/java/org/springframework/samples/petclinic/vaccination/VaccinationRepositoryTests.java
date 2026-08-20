package org.springframework.samples.petclinic.vaccination;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VaccinationRepositoryTests {

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Pet testPet;
    private VaccineType testVaccineType;

    @BeforeEach
    void setUp() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551023");
        entityManager.persist(owner);

        PetType petType = new PetType();
        petType.setName("dog");
        entityManager.persist(petType);

        testPet = new Pet();
        testPet.setName("Leo");
        testPet.setBirthDate(LocalDate.of(2020, 1, 1));
        testPet.setType(petType);
        testPet.setOwner(owner);
        entityManager.persist(testPet);

        testVaccineType = new VaccineType();
        testVaccineType.setName("Rabies");
        testVaccineType.setDefaultNextDueIntervalDays(365);
        entityManager.persist(testVaccineType);
        entityManager.flush();
    }

    @Test
    void shouldFindVaccinationById() {
        Vaccination vaccination = new Vaccination();
        vaccination.setPet(testPet);
        vaccination.setVaccineType(testVaccineType);
        vaccination.setDateAdministered(LocalDate.now());
        entityManager.persist(vaccination);
        entityManager.flush();

        Optional<Vaccination> foundVaccination = vaccinationRepository.findById(vaccination.getId());
        assertThat(foundVaccination).isPresent();
        assertThat(foundVaccination.get().getVaccineType().getName()).isEqualTo("Rabies");
    }

    @Test
    void shouldFindByPet() {
        Vaccination vaccination1 = new Vaccination();
        vaccination1.setPet(testPet);
        vaccination1.setVaccineType(testVaccineType);
        vaccination1.setDateAdministered(LocalDate.now().minusMonths(6));
        entityManager.persist(vaccination1);

        Vaccination vaccination2 = new Vaccination();
        vaccination2.setPet(testPet);
        vaccination2.setVaccineType(testVaccineType);
        vaccination2.setDateAdministered(LocalDate.now());
        entityManager.persist(vaccination2);
        entityManager.flush();

        Collection<Vaccination> vaccinations = vaccinationRepository.findByPet(testPet);
        assertThat(vaccinations).hasSize(2);
        assertThat(vaccinations).extracting(v -> v.getVaccineType().getName()).containsOnly("Rabies");
    }

    @Test
    void shouldSaveVaccination() {
        Vaccination vaccination = new Vaccination();
        vaccination.setPet(testPet);
        vaccination.setVaccineType(testVaccineType);
        vaccination.setDateAdministered(LocalDate.of(2023, 5, 10));
        vaccination.setAdministeringVet("Dr. Smith");
        vaccination.setBatchNumber("ABC-123");
        vaccination.setNextDueDate(LocalDate.of(2024, 5, 10));

        vaccinationRepository.save(vaccination);
        entityManager.flush();
        entityManager.clear();

        Optional<Vaccination> foundVaccination = vaccinationRepository.findById(vaccination.getId());
        assertThat(foundVaccination).isPresent();
        assertThat(foundVaccination.get().getAdministeringVet()).isEqualTo("Dr. Smith");
        assertThat(foundVaccination.get().getPet().getName()).isEqualTo("Leo");
    }

    @Test
    void shouldDeleteVaccination() {
        Vaccination vaccination = new Vaccination();
        vaccination.setPet(testPet);
        vaccination.setVaccineType(testVaccineType);
        vaccination.setDateAdministered(LocalDate.now());
        entityManager.persist(vaccination);
        entityManager.flush();

        Integer vaccinationId = vaccination.getId();
        vaccinationRepository.delete(vaccination);
        entityManager.flush();

        assertThat(vaccinationRepository.findById(vaccinationId)).isEmpty();
    }
}
