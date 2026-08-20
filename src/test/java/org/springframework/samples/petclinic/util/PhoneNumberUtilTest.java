package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PhoneNumberUtilTest {

    @Test
    void testNormalizePhoneNumber_onlyDigits() {
        assertThat(PhoneNumberUtil.normalizePhoneNumber("1234567890")).isEqualTo("1234567890");
    }

    @Test
    void testNormalizePhoneNumber_withDashes() {
        assertThat(PhoneNumberUtil.normalizePhoneNumber("123-456-7890")).isEqualTo("1234567890");
    }

    @Test
    void testNormalizePhoneNumber_withParentheses() {
        assertThat(PhoneNumberUtil.normalizePhoneNumber("(123)456-7890")).isEqualTo("1234567890");
    }

    @Test
    void testNormalizePhoneNumber_withSpaces() {
        assertThat(PhoneNumberUtil.normalizePhoneNumber("123 456 7890")).isEqualTo("1234567890");
    }

    @Test
    void testNormalizePhoneNumber_mixedFormatting() {
        assertThat(PhoneNumberUtil.normalizePhoneNumber("+1 (123) 456-7890 ext. 123")).isEqualTo("11234567890123");
    }

    @Test
    void testNormalizePhoneNumber_emptyString() {
        assertThat(PhoneNumberUtil.normalizePhoneNumber("")).isEqualTo("");
    }

    @Test
    void testNormalizePhoneNumber_nullString() {
        assertThat(PhoneNumberUtil.normalizePhoneNumber(null)).isNull();
    }

    @Test
    void testNormalizePhoneNumber_onlyNonDigits() {
        assertThat(PhoneNumberUtil.normalizePhoneNumber("abc-def")).isEqualTo("");
    }

    @Test
    void testNormalizePhoneNumber_specialCharacters() {
        assertThat(PhoneNumberUtil.normalizePhoneNumber("!@#$%^&*()_+")).isEqualTo("");
    }
}
