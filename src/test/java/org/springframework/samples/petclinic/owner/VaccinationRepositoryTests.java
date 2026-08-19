package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VaccinationRepositoryTests {

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Autowired
    private PetRepository petRepository; 

    @Autowired
    private TestEntityManager entityManager;

    private Pet testPet;
    private Vaccination vaccination1;
    private Vaccination vaccination2;
    private Vaccination vaccination3;

    @BeforeEach
    void setUp() {
        PetType dog = new PetType();
        dog.setName("dog");
        entityManager.persist(dog);

        testPet = new Pet();
        testPet.setName("Buddy");
        testPet.setBirthDate(LocalDate.of(2020, 1, 1));
        testPet.setType(dog);
        entityManager.persist(testPet);

        vaccination1 = new Vaccination();
        vaccination1.setType("Rabies");
        vaccination1.setDueDate(LocalDate.now().plusDays(5));
        vaccination1.setPet(testPet);
        entityManager.persist(vaccination1);

        vaccination2 = new Vaccination();
        vaccination2.setType("Distemper");
        vaccination2.setDueDate(LocalDate.now().plusWeeks(3));
        vaccination2.setPet(testPet);
        entityManager.persist(vaccination2);

        vaccination3 = new Vaccination();
        vaccination3.setType("Parvovirus");
        vaccination3.setDueDate(LocalDate.now().minusDays(10)); 
        vaccination3.setPet(testPet);
        entityManager.persist(vaccination3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void shouldFindVaccinationById() {
        Optional<Vaccination> foundVaccination = vaccinationRepository.findById(vaccination1.getId());
        assertThat(foundVaccination).isPresent();
        assertThat(foundVaccination.get().getType()).isEqualTo("Rabies");
        assertThat(foundVaccination.get().getPet().getName()).isEqualTo("Buddy");
    }

    @Test
    void shouldFindVaccinationsByPetId() {
        List<Vaccination> vaccinations = vaccinationRepository.findByPetId(testPet.getId());
        assertThat(vaccinations).hasSize(3);
        assertThat(vaccinations).extracting(Vaccination::getType).containsExactlyInAnyOrder("Rabies", "Distemper", "Parvovirus");
    }

    @Test
    void shouldFindVaccinationsByDueDateBetween() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusWeeks(2); 

        List<Vaccination> vaccinations = vaccinationRepository.findByDueDateBetween(startDate, endDate);
        assertThat(vaccinations).hasSize(1);
        assertThat(vaccinations.get(0).getType()).isEqualTo("Rabies");
    }

    @Test
    void shouldSaveVaccination() {
        Vaccination newVaccination = new Vaccination();
        newVaccination.setType("Hepatitis");
        newVaccination.setDueDate(LocalDate.now().plusMonths(6));
        newVaccination.setPet(testPet);

        Vaccination savedVaccination = vaccinationRepository.save(newVaccination);
        assertThat(savedVaccination.getId()).isNotNull();
        assertThat(savedVaccination.getType()).isEqualTo("Hepatitis");

        Optional<Vaccination> found = vaccinationRepository.findById(savedVaccination.getId());
        assertThat(found).isPresent();
    }

    @Test
    void shouldDeleteVaccination() {
        vaccinationRepository.delete(vaccination1);
        Optional<Vaccination> found = vaccinationRepository.findById(vaccination1.getId());
        assertThat(found).isNotPresent();
    }
}