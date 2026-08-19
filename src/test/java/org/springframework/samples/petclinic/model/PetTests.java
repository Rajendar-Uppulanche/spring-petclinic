package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.validation.PastOrPresentDate;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PetTests {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldValidatePastBirthDate() {
        Pet pet = new Pet();
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.now().minusYears(1));
        pet.setType(new PetType());
        Owner owner = new Owner();
        owner.setFirstName("George");
        pet.setOwner(owner);

        Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldValidatePresentBirthDate() {
        Pet pet = new Pet();
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.now());
        pet.setType(new PetType());
        Owner owner = new Owner();
        owner.setFirstName("George");
        pet.setOwner(owner);

        Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
        assertThat(violations).isEmpty();
    }

    @Test
    void shouldNotValidateFutureBirthDate() {
        Pet pet = new Pet();
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.now().plusDays(1));
        pet.setType(new PetType());
        Owner owner = new Owner();
        owner.setFirstName("George");
        pet.setOwner(owner);

        Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
        assertThat(violations).hasSize(1);
        ConstraintViolation<Pet> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("birthDate");
        assertThat(violation.getMessage()).contains("future");
        assertThat(violation.getConstraintDescriptor().getAnnotation()).isInstanceOf(PastOrPresentDate.class);
    }

    @Test
    void shouldNotValidateNullBirthDateIfNotNullPresent() {
        Pet pet = new Pet();
        pet.setName("Leo");
        pet.setBirthDate(null);
        pet.setType(new PetType());
        Owner owner = new Owner();
        owner.setFirstName("George");
        pet.setOwner(owner);

        Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
        assertThat(violations).hasSize(1);
        ConstraintViolation<Pet> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("birthDate");
        assertThat(violation.getMessage()).contains("null");
    }
}