package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PetTests {

    @Test
    void testGetAge() {
        Pet pet = new Pet();
        pet.setBirthDate(LocalDate.now().minusYears(5).minusMonths(3));
        assertThat(pet.getAge()).isEqualTo(5);

        pet.setBirthDate(LocalDate.now()); // Born today
        assertThat(pet.getAge()).isEqualTo(0);

        pet.setBirthDate(null); // No birth date
        assertThat(pet.getAge()).isEqualTo(0);
    }

    @Test
    void testAddPreventiveCare() {
        Pet pet = new Pet();
        PreventiveCare pc1 = new PreventiveCare();
        pc1.setType("Vaccination");
        PreventiveCare pc2 = new PreventiveCare();
        pc2.setType("Treatment");

        pet.addPreventiveCare(pc1);
        pet.addPreventiveCare(pc2);

        assertThat(pet.getPreventiveCares()).hasSize(2);
        assertThat(pc1.getPet()).isEqualTo(pet);
        assertThat(pc2.getPet()).isEqualTo(pet);
    }

}