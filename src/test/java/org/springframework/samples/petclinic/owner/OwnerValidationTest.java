package org.springframework.samples.petclinic.owner;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.validation.ValidPhoneNumber;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Owner createValidOwner() {
        Owner owner = new Owner();
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setAddress("123 Main St");
        owner.setCity("Anytown");
        owner.setTelephone("1234567890"); // Valid 10-digit
        return owner;
    }

    @Test
    void testValidPhoneNumber() {
        Owner owner = createValidOwner();
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidPhoneNumber_withFormatting() {
        Owner owner = createValidOwner();
        owner.setTelephone("(123) 456-7890"); // Valid 10-digit with formatting
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidPhoneNumber_15Digits() {
        Owner owner = createValidOwner();
        owner.setTelephone("123456789012345"); // Valid 15-digit
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidPhoneNumber_tooShort() {
        Owner owner = createValidOwner();
        owner.setTelephone("123456789"); // 9 digits
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
        assertThat(violations.iterator().next().getMessage()).contains("between 10 and 15 digits");
    }

    @Test
    void testInvalidPhoneNumber_tooShortWithFormatting() {
        Owner owner = createValidOwner();
        owner.setTelephone("(123) 456-789"); // 9 digits after stripping
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
        assertThat(violations.iterator().next().getMessage()).contains("between 10 and 15 digits");
    }

    @Test
    void testInvalidPhoneNumber_tooLong() {
        Owner owner = createValidOwner();
        owner.setTelephone("1234567890123456"); // 16 digits
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
        assertThat(violations.iterator().next().getMessage()).contains("between 10 and 15 digits");
    }

    @Test
    void testInvalidPhoneNumber_tooLongWithFormatting() {
        Owner owner = createValidOwner();
        owner.setTelephone("+1 (123) 456-7890 ext. 12345"); // 16 digits after stripping
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
        assertThat(violations.iterator().next().getMessage()).contains("between 10 and 15 digits");
    }

    @Test
    void testInvalidPhoneNumber_onlyNonDigits() {
        Owner owner = createValidOwner();
        owner.setTelephone("abc-def"); // 0 digits after stripping
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
        assertThat(violations.iterator().next().getMessage()).contains("between 10 and 15 digits");
    }

    @Test
    void testBlankPhoneNumber() {
        Owner owner = createValidOwner();
        owner.setTelephone(""); // Handled by @NotBlank
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
        assertThat(violations.iterator().next().getMessage()).isEqualTo("must not be blank");
    }

    @Test
    void testNullPhoneNumber() {
        Owner owner = createValidOwner();
        owner.setTelephone(null); // Handled by @NotBlank
        Set<ConstraintViolation<Owner>> violations = validator.validate(owner);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("telephone");
        assertThat(violations.iterator().next().getMessage()).isEqualTo("must not be blank");
    }
}
