package com.example.petclinic.repository;

import com.example.petclinic.model.Veterinarian;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VeterinarianRepositoryTest {

    @Autowired
    private VeterinarianRepository veterinarianRepository;

    private Veterinarian vet1;
    private Veterinarian vet2;

    @BeforeEach
    void setUp() {
        veterinarianRepository.deleteAll();

        vet1 = new Veterinarian();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        vet1.setContactInformation("james.carter@example.com");
        veterinarianRepository.save(vet1);

        vet2 = new Veterinarian();
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        vet2.setContactInformation("helen.leary@example.com");
        veterinarianRepository.save(vet2);
    }

    @Test
    void testSaveVeterinarian() {
        Veterinarian newVet = new Veterinarian();
        newVet.setFirstName("John");
        newVet.setLastName("Doe");
        newVet.setContactInformation("john.doe@example.com");
        Veterinarian savedVet = veterinarianRepository.save(newVet);

        assertThat(savedVet).isNotNull();
        assertThat(savedVet.getId()).isNotNull();
        assertThat(savedVet.getFirstName()).isEqualTo("John");
    }

    @Test
    void testFindAllVeterinarians() {
        Iterable<Veterinarian> vets = veterinarianRepository.findAll();
        assertThat(vets).hasSize(2);
    }

    @Test
    void testFindVeterinarianById() {
        Optional<Veterinarian> foundVet = veterinarianRepository.findById(vet1.getId());
        assertThat(foundVet).isPresent();
        assertThat(foundVet.get().getLastName()).isEqualTo("Carter");
    }

    @Test
    void testDeleteVeterinarian() {
        veterinarianRepository.deleteById(vet1.getId());
        Optional<Veterinarian> foundVet = veterinarianRepository.findById(vet1.getId());
        assertThat(foundVet).isNotPresent();
    }
}
