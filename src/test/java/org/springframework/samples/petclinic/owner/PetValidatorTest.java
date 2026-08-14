package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PetValidatorTest {

	private PetValidator petValidator;
	private Pet pet;
	private Errors errors;

	@BeforeEach
	void setUp() {
		petValidator = new PetValidator();
		pet = new Pet();
		pet.setName("Test Pet");
		pet.setBirthDate(LocalDate.of(2020, 1, 1));
		pet.setType(new PetType()); // Assuming PetType is not null for valid pet
		errors = new BeanPropertyBindingResult(pet, "pet");
	}

	@Test
	void shouldSupportPetClass() {
		assertThat(petValidator.supports(Pet.class)).isTrue();
		assertThat(petValidator.supports(Owner.class)).isFalse();
	}

	@Test
	void shouldValidateValidPet() {
		petValidator.validate(pet, errors);
		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldRejectEmptyName() {
		pet.setName("");
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	@Test
	void shouldRejectNullBirthDate() {
		pet.setBirthDate(null);
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
		assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("required");
	}

	@Test
	void shouldRejectFutureBirthDate() {
		pet.setBirthDate(LocalDate.now().plusDays(1)); // Set birth date to tomorrow
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
		assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("notFutureDate");
		assertThat(errors.getFieldError("birthDate").getDefaultMessage()).isEqualTo("Birth date cannot be in the future");
	}

	@Test
	void shouldAcceptCurrentDateAsBirthDate() {
		pet.setBirthDate(LocalDate.now()); // Set birth date to today
		petValidator.validate(pet, errors);
		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldRejectNullTypeForNewPet() {
		pet.setId(null); // Make it a new pet
		pet.setType(null);
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("type")).isTrue();
		assertThat(errors.getFieldError("type").getCode()).isEqualTo("required");
	}

	@Test
	void shouldAcceptNullTypeForExistingPet() {
		pet.setId(1); // Make it an existing pet
		pet.setType(null);
		petValidator.validate(pet, errors);
		// Only name and birthDate are validated for existing pets if type is null
		// The original validator only checks type for new pets.
		// So, this test should pass without type error.
		assertThat(errors.hasFieldErrors("type")).isFalse();
	}
}
