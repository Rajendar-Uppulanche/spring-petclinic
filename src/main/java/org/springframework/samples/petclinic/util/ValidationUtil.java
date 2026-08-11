package org.springframework.samples.petclinic.util;

public class ValidationUtil {

    /**
     * Checks if an ID is invalid. For simplicity, we consider non-positive integers as invalid.
     * In a real application, this might involve more complex validation rules.
     * @param id The ID to validate.
     * @return true if the ID is invalid, false otherwise.
     */
    public static boolean isInvalidId(int id) {
        return id <= 0;
    }
}
