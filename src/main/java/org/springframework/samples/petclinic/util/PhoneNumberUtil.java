package org.springframework.samples.petclinic.util;

public class PhoneNumberUtil {

    /**
     * Strips all non-numeric characters from a phone number string.
     *
     * @param phoneNumber The input phone number string.
     * @return The normalized phone number string containing only digits, or null if the input is null.
     */
    public static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return phoneNumber.replaceAll("[^0-9]", "");
    }
}
