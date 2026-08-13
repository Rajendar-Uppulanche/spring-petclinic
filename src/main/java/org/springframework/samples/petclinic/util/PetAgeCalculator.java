package org.springframework.samples.petclinic.util;

import java.time.LocalDate;
import java.time.Period;

public class PetAgeCalculator {

    public static String calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return "";
        }

        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today)) {
            // This case should ideally not happen for valid pet birth dates,
            // but handling it gracefully.
            return "Born in the future";
        }

        Period period = Period.between(birthDate, today);

        int years = period.getYears();
        int months = period.getMonths();

        if (years > 0 && months > 0) {
            return years + " year" + (years > 1 ? "s" : "") + ", " + months + " month" + (months > 1 ? "s" : "");
        } else if (years > 0) {
            return years + " year" + (years > 1 ? "s" : "");
        } else if (months > 0) {
            return months + " month" + (months > 1 ? "s" : "");
        } else {
            // If years and months are both 0, it means the age is less than a month.
            // Or it's the exact birth date (0 days, 0 months, 0 years).
            // For simplicity, anything less than 1 month is "less than 1 month".
            return "less than 1 month";
        }
    }
}
