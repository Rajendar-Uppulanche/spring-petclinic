package com.example.petclinic.model;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael Isvy
 * @author Juergen Hoeller
 */
class OwnerTests {

    private Validator createValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        return validator;
    }

    @Test
    void shouldValidateWhenTelephoneIsValid() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("6085551000"); // Valid telephone

        Validator validator = createValidator();
        Errors errors = new BeanPropertyBindingResult(owner, "owner");
        validator.validate(owner, errors);
        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @Test
    void shouldValidateWhenTelephoneIs15Digits() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("123456789012345"); // Valid 15-digit telephone

        Validator validator = createValidator();
        Errors errors = new BeanPropertyBindingResult(owner, "owner");
        validator.validate(owner, errors);
        assertThat(errors.getErrorCount()).isEqualTo(0);
    }

    @Test
    void shouldNotValidateWhenTelephoneIsTooShort() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("123456789"); // Too short

        Validator validator = createValidator();
        Errors errors = new BeanPropertyBindingResult(owner, "owner");
        validator.validate(owner, errors);
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.hasFieldErrors("telephone")).isTrue();
        assertThat(errors.getFieldError("telephone").getDefaultMessage()).contains("between 10 and 15 digits");
    }

    @Test
    void shouldNotValidateWhenTelephoneIsTooLong() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("1234567890123456"); // Too long

        Validator validator = createValidator();
        Errors errors = new BeanPropertyBindingResult(owner, "owner");
        validator.validate(owner, errors);
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.hasFieldErrors("telephone")).isTrue();
        assertThat(errors.getFieldError("telephone").getDefaultMessage()).contains("between 10 and 15 digits");
    }

    @Test
    void shouldNotValidateWhenTelephoneContainsNonDigits() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone("608-555-1000"); // Contains hyphens

        Validator validator = createValidator();
        Errors errors = new BeanPropertyBindingResult(owner, "owner");
        validator.validate(owner, errors);
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.hasFieldErrors("telephone")).isTrue();
        assertThat(errors.getFieldError("telephone").getDefaultMessage()).contains("only digits");
    }

    @Test
    void shouldNotValidateWhenTelephoneIsEmpty() {
        Owner owner = new Owner();
        owner.setFirstName("George");
        owner.setLastName("Bush");
        owner.setAddress("110 W. Liberty St.");
        owner.setCity("Madison");
        owner.setTelephone(""); // Empty

        Validator validator = createValidator();
        Errors errors = new BeanPropertyBindingResult(owner, "owner");
        validator.validate(owner, errors);
        assertThat(errors.getErrorCount()).isEqualTo(1);
        assertThat(errors.hasFieldErrors("telephone")).isTrue();
        assertThat(errors.getFieldError("telephone").getDefaultMessage()).contains("must not be empty");
    }
}