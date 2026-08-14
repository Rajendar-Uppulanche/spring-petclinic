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
		errors = new BeanPropertyBindingResult(pet, "pet");
	}

	@Test
	void shouldSupportPetClass() {
		assertThat(petValidator.supports(Pet.class)).isTrue();
		assertThat(petValidator.supports(Object.class)).isFalse();
	}

	@Test
	void shouldRejectEmptyName() {
		pet.setName("");
		pet.setBirthDate(LocalDate.now());
		pet.setType(new PetType());
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	@Test
	void shouldRejectNullBirthDate() {
		pet.setName("Leo");
		pet.setBirthDate(null);
		pet.setType(new PetType());
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
		assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("required");
	}

	@Test
	void shouldRejectFutureBirthDate() {
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now().plusDays(1)); // Future date
		pet.setType(new PetType());
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
		assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("notFuture");
	}

	@Test
	void shouldAcceptPastOrPresentBirthDate() {
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now().minusDays(1)); // Past date
		pet.setType(new PetType());
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("birthDate")).isFalse();

		pet.setBirthDate(LocalDate.now()); // Present date
		errors = new BeanPropertyBindingResult(pet, "pet"); // Reset errors for new validation
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("birthDate")).isFalse();
	}

	@Test
	void shouldRejectNullTypeForNewPet() {
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now());
		pet.setType(null);
		// Simulate a new pet
		pet.setId(null); // Ensure isNew() returns true
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("type")).isTrue();
		assertThat(errors.getFieldError("type").getCode()).isEqualTo("required");
	}

	@Test
	void shouldAcceptNullTypeForExistingPet() {
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now());
		pet.setType(null);
		// Simulate an existing pet
		pet.setId(1); // Ensure isNew() returns false
		petValidator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("type")).isFalse(); // Type is not required for existing pet
	}

	@Test
	void shouldAcceptValidPet() {
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now().minusYears(1));
		pet.setType(new PetType());
		petValidator.validate(pet, errors);
		assertThat(errors.hasErrors()).isFalse();
	}
}
