package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class PetAgeCalculatorTests {

    private final LocalDate TODAY = LocalDate.of(2023, 10, 26); // Consistent current date for testing

    @Test
    void testAgeInYearsAndMonths() {
        // 2 years, 3 months
        LocalDate birthDate = TODAY.minusYears(2).minusMonths(3);
        assertThat(PetAgeCalculator.formatAge(birthDate, TODAY)).isEqualTo("(2 years, 3 months)");
    }

    @Test
    void testAgeInYearsOnly() {
        // 5 years
        LocalDate birthDate = TODAY.minusYears(5);
        assertThat(PetAgeCalculator.formatAge(birthDate, TODAY)).isEqualTo("(5 years)");
    }

    @Test
    void testAgeInMonthsOnly() {
        // 7 months
        LocalDate birthDate = TODAY.minusMonths(7);
        assertThat(PetAgeCalculator.formatAge(birthDate, TODAY)).isEqualTo("(7 months)");
    }

    @Test
    void testAgeLessThanOneMonth() {
        // 15 days
        LocalDate birthDate = TODAY.minusDays(15);
        assertThat(PetAgeCalculator.formatAge(birthDate, TODAY)).isEqualTo("(< 1 month)");
        // 0 days (born today)
        assertThat(PetAgeCalculator.formatAge(TODAY, TODAY)).isEqualTo("(< 1 month)");
    }

    @Test
    void testAgeOneYearOneMonth() {
        LocalDate birthDate = TODAY.minusYears(1).minusMonths(1);
        assertThat(PetAgeCalculator.formatAge(birthDate, TODAY)).isEqualTo("(1 year, 1 month)");
    }

    @Test
    void testAgeOneYearZeroMonths() {
        LocalDate birthDate = TODAY.minusYears(1);
        assertThat(PetAgeCalculator.formatAge(birthDate, TODAY)).isEqualTo("(1 year)");
    }

    @Test
    void testAgeZeroYearsOneMonth() {
        LocalDate birthDate = TODAY.minusMonths(1);
        assertThat(PetAgeCalculator.formatAge(birthDate, TODAY)).isEqualTo("(1 month)");
    }

    @Test
    void testAgeWithNullBirthDate() {
        assertThat(PetAgeCalculator.formatAge(null, TODAY)).isEqualTo("");
    }

    @Test
    void testAgeWithNullCurrentDate() {
        LocalDate birthDate = TODAY.minusYears(1);
        assertThat(PetAgeCalculator.formatAge(birthDate, null)).isEqualTo("");
    }

    @Test
    void testAgeWithBirthDateInFuture() {
        LocalDate birthDate = TODAY.plusDays(10);
        assertThat(PetAgeCalculator.formatAge(birthDate, TODAY)).isEqualTo("");
    }

    @Test
    void testAgeWithLeapYear() {
        LocalDate birthDate = LocalDate.of(2020, 2, 29); // Leap year
        LocalDate currentDate = LocalDate.of(2023, 2, 28);
        assertThat(PetAgeCalculator.formatAge(birthDate, currentDate)).isEqualTo("(2 years, 11 months)");
    }
}
