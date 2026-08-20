package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PetValidatorTests {

	private PetValidator validator;

	@BeforeEach
	void setUp() {
		validator = new PetValidator();
	}

	@Test
	void shouldSupportPetClass() {
		assertThat(validator.supports(Pet.class)).isTrue();
		assertThat(validator.supports(Owner.class)).isFalse();
	}

	@Test
	void shouldRejectEmptyName() {
		Pet pet = new Pet();
		pet.setName("");
		pet.setBirthDate(LocalDate.now());
		pet.setType(new PetType());
		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("name")).isTrue();
		assertThat(errors.getFieldError("name").getCode()).isEqualTo("required");
	}

	@Test
	void shouldRejectNullTypeForNewPet() {
		Pet pet = new Pet();
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now());
		// pet.setType(null); // Type is null by default
		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("type")).isTrue();
		assertThat(errors.getFieldError("type").getCode()).isEqualTo("required");
	}

	@Test
	void shouldRejectNullBirthDate() {
		Pet pet = new Pet();
		pet.setName("Leo");
		pet.setType(new PetType());
		// pet.setBirthDate(null); // BirthDate is null by default
		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
		assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("required");
	}

	@Test
	void shouldRejectFutureBirthDate() {
		Pet pet = new Pet();
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now().plusDays(1)); // Future date
		pet.setType(new PetType());
		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);
		assertThat(errors.hasFieldErrors("birthDate")).isTrue();
		assertThat(errors.getFieldError("birthDate").getCode()).isEqualTo("birthDate.future");
	}

	@Test
	void shouldAcceptValidPet() {
		Pet pet = new Pet();
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now().minusYears(1)); // Past date
		pet.setType(new PetType());
		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);
		assertThat(errors.hasErrors()).isFalse();
	}

	@Test
	void shouldAcceptPresentBirthDate() {
		Pet pet = new Pet();
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now()); // Present date
		pet.setType(new PetType());
		Errors errors = new BeanPropertyBindingResult(pet, "pet");
		validator.validate(pet, errors);
		assertThat(errors.hasErrors()).isFalse();
	}

}
