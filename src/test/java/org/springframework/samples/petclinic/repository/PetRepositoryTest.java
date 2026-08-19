package org.springframework.samples.petclinic.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PetRepositoryTest {

    @Autowired
    PetRepository petRepository;

    @Test
    void findByOwnerIdWithVisitCount() {
        // Given an owner with ID 1 (from test data)
        // Owner 1 has 3 pets: Leo, Basil, Rosy
        // Leo (ID 1) has 2 visits
        // Basil (ID 2) has 0 visits
        // Rosy (ID 3) has 0 visits

        List<PetRepository.PetWithVisitCount> petsWithVisitCounts = petRepository.findByOwnerIdWithVisitCount(1);

        assertThat(petsWithVisitCounts).hasSize(3);

        // Verify Leo's visit count
        PetRepository.PetWithVisitCount leo = petsWithVisitCounts.stream()
            .filter(pwc -> "Leo".equals(pwc.getPet().getName()))
            .findFirst()
            .orElse(null);
        assertThat(leo).isNotNull();
        assertThat(leo.getVisitCount()).isEqualTo(2L);

        // Verify Basil's visit count
        PetRepository.PetWithVisitCount basil = petsWithVisitCounts.stream()
            .filter(pwc -> "Basil".equals(pwc.getPet().getName()))
            .findFirst()
            .orElse(null);
        assertThat(basil).isNotNull();
        assertThat(basil.getVisitCount()).isEqualTo(0L);

        // Verify Rosy's visit count
        PetRepository.PetWithVisitCount rosy = petsWithVisitCounts.stream()
            .filter(pwc -> "Rosy".equals(pwc.getPet().getName()))
            .findFirst()
            .orElse(null);
        assertThat(rosy).isNotNull();
        assertThat(rosy.getVisitCount()).isEqualTo(0L);

        // Test an owner with no pets
        List<PetRepository.PetWithVisitCount> noPets = petRepository.findByOwnerIdWithVisitCount(999);
        assertThat(noPets).isEmpty();
    }

    @Test
    void findByOwnerIdWithVisitCountForOwnerWithOnePetAndOneVisit() {
        // Assuming owner 2 has one pet (Jewel, ID 4) with one visit
        List<PetRepository.PetWithVisitCount> petsWithVisitCounts = petRepository.findByOwnerIdWithVisitCount(2);

        assertThat(petsWithVisitCounts).hasSize(1);

        PetRepository.PetWithVisitCount jewel = petsWithVisitCounts.get(0);
        assertThat(jewel.getPet().getName()).isEqualTo("Jewel");
        assertThat(jewel.getVisitCount()).isEqualTo(1L);
    }

    @Test
    void findByOwnerIdWithVisitCountForOwnerWithOnePetAndMultipleVisits() {
        // Assuming owner 3 has one pet (Iggy, ID 5) with 3 visits
        List<PetRepository.PetWithVisitCount> petsWithVisitCounts = petRepository.findByOwnerIdWithVisitCount(3);

        assertThat(petsWithVisitCounts).hasSize(1);

        PetRepository.PetWithVisitCount iggy = petsWithVisitCounts.get(0);
        assertThat(iggy.getPet().getName()).isEqualTo("Iggy");
        assertThat(iggy.getVisitCount()).isEqualTo(3L);
    }
}