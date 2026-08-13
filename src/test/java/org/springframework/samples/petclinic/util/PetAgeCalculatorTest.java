package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PetAgeCalculatorTest {

    @Test
    void calculateAge_fullYearsAndMonths() {
        LocalDate birthDate = LocalDate.now().minusYears(2).minusMonths(3).minusDays(10);
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("2 years, 3 months");
    }

    @Test
    void calculateAge_fullYearsOnly() {
        LocalDate birthDate = LocalDate.now().minusYears(3);
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("3 years");
    }

    @Test
    void calculateAge_monthsOnly() {
        LocalDate birthDate = LocalDate.now().minusMonths(7).minusDays(15);
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("7 months");
    }

    @Test
    void calculateAge_lessThanOneMonth_days() {
        LocalDate birthDate = LocalDate.now().minusDays(15);
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("less than 1 month");
    }

    @Test
    void calculateAge_lessThanOneMonth_today() {
        LocalDate birthDate = LocalDate.now();
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("less than 1 month");
    }

    @Test
    void calculateAge_edgeCase_oneYearZeroMonths() {
        LocalDate birthDate = LocalDate.now().minusYears(1).plusDays(5); // Just over 1 year
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("1 year");
    }

    @Test
    void calculateAge_edgeCase_oneMonthZeroDays() {
        LocalDate birthDate = LocalDate.now().minusMonths(1);
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("1 month");
    }

    @Test
    void calculateAge_edgeCase_justUnderOneMonth() {
        LocalDate birthDate = LocalDate.now().minusMonths(1).plusDays(1);
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("less than 1 month");
    }

    @Test
    void calculateAge_nullBirthDate() {
        assertThat(PetAgeCalculator.calculateAge(null)).isEqualTo("");
    }

    @Test
    void calculateAge_futureBirthDate() {
        LocalDate birthDate = LocalDate.now().plusDays(5);
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("Born in the future");
    }

    @Test
    void calculateAge_multipleYearsAndMonths() {
        LocalDate birthDate = LocalDate.now().minusYears(5).minusMonths(11);
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("5 years, 11 months");
    }

    @Test
    void calculateAge_oneYearOneMonth() {
        LocalDate birthDate = LocalDate.now().minusYears(1).minusMonths(1);
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("1 year, 1 month");
    }

    @Test
    void calculateAge_twoMonths() {
        LocalDate birthDate = LocalDate.now().minusMonths(2);
        assertThat(PetAgeCalculator.calculateAge(birthDate)).isEqualTo("2 months");
    }
}
