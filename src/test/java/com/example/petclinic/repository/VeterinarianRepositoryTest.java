package com.example.petclinic.repository;

import com.example.petclinic.model.Veterinarian;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VeterinarianRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VeterinarianRepository veterinarianRepository;

    @Test
    void shouldFindVeterinarianById() {
        Veterinarian vet = new Veterinarian();
        vet.setName("Dr. Test Vet");
        vet.setContactInformation("test@example.com");
        entityManager.persist(vet);
        entityManager.flush();

        Veterinarian foundVet = veterinarianRepository.findById(vet.getId()).orElse(null);
        assertThat(foundVet).isNotNull();
        assertThat(foundVet.getName()).isEqualTo("Dr. Test Vet");
    }

    @Test
    void shouldSaveVeterinarian() {
        Veterinarian vet = new Veterinarian();
        vet.setName("Dr. New Vet");
        vet.setContactInformation("new@example.com");

        Veterinarian savedVet = veterinarianRepository.save(vet);
        assertThat(savedVet.getId()).isNotNull();
        assertThat(savedVet.getName()).isEqualTo("Dr. New Vet");

        Veterinarian foundVet = entityManager.find(Veterinarian.class, savedVet.getId());
        assertThat(foundVet).isEqualTo(savedVet);
    }
}