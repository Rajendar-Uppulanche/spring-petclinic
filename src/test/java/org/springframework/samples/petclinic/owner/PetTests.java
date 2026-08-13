package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class PetTests {

    @Test
    void getAge_nullBirthDate() {
        Pet pet = new Pet();
        pet.setBirthDate(null);
        assertThat(pet.getAge()).isEqualTo("Age unknown");
    }

    @Test
    void getAge_futureBirthDate() {
        Pet pet = new Pet();
        pet.setBirthDate(LocalDate.now().plusDays(1));
        assertThat(pet.getAge()).isEqualTo("Age unknown");
    }

    @Test
    void getAge_exactYearsAndMonths() {
        Pet pet = new Pet();
        // 1 year, 6 months ago
        pet.setBirthDate(LocalDate.now().minusYears(1).minusMonths(6));
        assertThat(pet.getAge()).isEqualTo("1 year 6 months");
    }

    @Test
    void getAge_onlyYears() {
        Pet pet = new Pet();
        pet.setBirthDate(LocalDate.now().minusYears(2));
        assertThat(pet.getAge()).isEqualTo("2 years");
    }

    @Test
    void getAge_onlyMonths() {
        Pet pet = new Pet();
        pet.setBirthDate(LocalDate.now().minusMonths(3));
        assertThat(pet.getAge()).isEqualTo("3 months");
    }

    @Test
    void getAge_lessThanAMonth() {
        Pet pet = new Pet();
        pet.setBirthDate(LocalDate.now().minusDays(15));
        assertThat(pet.getAge()).isEqualTo("0 years 0 months");
    }

    @Test
    void getAge_today() {
        Pet pet = new Pet();
        pet.setBirthDate(LocalDate.now());
        assertThat(pet.getAge()).isEqualTo("0 years 0 months");
    }

    @Test
    void getAge_oneYearOneMonth() {
        Pet pet = new Pet();
        pet.setBirthDate(LocalDate.now().minusYears(1).minusMonths(1));
        assertThat(pet.getAge()).isEqualTo("1 year 1 month");
    }

    @Test
    void getAge_multipleYearsMultipleMonths() {
        Pet pet = new Pet();
        pet.setBirthDate(LocalDate.now().minusYears(5).minusMonths(11));
        assertThat(pet.getAge()).isEqualTo("5 years 11 months");
    }

    @Test
    void getAge_justTurnedAYear() {
        Pet pet = new Pet();
        pet.setBirthDate(LocalDate.now().minusYears(1));
        assertThat(pet.getAge()).isEqualTo("1 year");
    }

    @Test
    void getAge_justTurnedAMonth() {
        Pet pet = new Pet();
        pet.setBirthDate(LocalDate.now().minusMonths(1));
        assertThat(pet.getAge()).isEqualTo("1 month");
    }
}
