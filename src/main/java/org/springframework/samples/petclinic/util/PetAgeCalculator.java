package org.springframework.samples.petclinic.util;

import java.time.LocalDate;
import java.time.Period;

public class PetAgeCalculator {

    public static String formatAge(LocalDate birthDate, LocalDate currentDate) {
        if (birthDate == null || currentDate == null || birthDate.isAfter(currentDate)) {
            return "";
        }

        Period p = Period.between(birthDate, currentDate);
        int years = p.getYears();
        int months = p.getMonths();

        if (years > 0) {
            if (months > 0) {
                return "(" + years + " year" + (years > 1 ? "s" : "") + ", " + months + " month" + (months > 1 ? "s" : "") + ")";
            } else {
                return "(" + years + " year" + (years > 1 ? "s" : "") + ")";
            }
        } else if (months > 0) {
            return "(" + months + " month" + (months > 1 ? "s" : "") + ")";
        } else {
            return "(< 1 month)";
        }
    }
}
