package org.springframework.samples.petclinic.vaccination;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VaccineTypeRepositoryTests {

    @Autowired
    private VaccineTypeRepository vaccineTypeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldFindAllVaccineTypes() {
        VaccineType type1 = new VaccineType();
        type1.setName("Rabies");
        type1.setDefaultNextDueIntervalDays(365);
        entityManager.persist(type1);

        VaccineType type2 = new VaccineType();
        type2.setName("Distemper");
        type2.setDefaultNextDueIntervalDays(1095);
        entityManager.persist(type2);

        Collection<VaccineType> vaccineTypes = vaccineTypeRepository.findAll();
        assertThat(vaccineTypes).hasSize(2);
        assertThat(vaccineTypes).extracting(VaccineType::getName).containsExactlyInAnyOrder("Rabies", "Distemper");
    }

    @Test
    void shouldFindVaccineTypeById() {
        VaccineType type = new VaccineType();
        type.setName("Rabies");
        type.setDefaultNextDueIntervalDays(365);
        entityManager.persist(type);
        entityManager.flush();

        Optional<VaccineType> foundType = vaccineTypeRepository.findById(type.getId());
        assertThat(foundType).isPresent();
        assertThat(foundType.get().getName()).isEqualTo("Rabies");
    }

    @Test
    void shouldFindVaccineTypeByName() {
        VaccineType type = new VaccineType();
        type.setName("Rabies");
        type.setDefaultNextDueIntervalDays(365);
        entityManager.persist(type);
        entityManager.flush();

        Optional<VaccineType> foundType = vaccineTypeRepository.findByName("Rabies");
        assertThat(foundType).isPresent();
        assertThat(foundType.get().getName()).isEqualTo("Rabies");
    }

    @Test
    void shouldSaveVaccineType() {
        VaccineType type = new VaccineType();
        type.setName("New Vaccine");
        type.setDefaultNextDueIntervalDays(180);

        vaccineTypeRepository.save(type);
        entityManager.flush();
        entityManager.clear();

        Optional<VaccineType> foundType = vaccineTypeRepository.findByName("New Vaccine");
        assertThat(foundType).isPresent();
        assertThat(foundType.get().getDefaultNextDueIntervalDays()).isEqualTo(180);
    }
}
