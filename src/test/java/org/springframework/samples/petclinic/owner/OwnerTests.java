package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.validation.ValidatorTests;

/**
 * @author Michael Isvy
 * @author Brian Clozel
 */
class OwnerTests extends ValidatorTests {

	@Test
	void shouldValidateWhenFirstNameIsEmpty() {

		Owner owner = new Owner();
		owner.setFirstName("");
		owner.setLastName("lastName");
		owner.setAddress("address");
		owner.setCity("city");
		owner.setTelephone("1234567890");

		Validator validator = createValidator();
		Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Owner> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("firstName");
		assertThat(violation.getMessage()).isEqualTo("must not be empty");
	}

	@Test
	void shouldValidateWhenLastNameIsEmpty() {

		Owner owner = new Owner();
		owner.setFirstName("firstName");
		owner.setLastName("");
		owner.setAddress("address");
		owner.setCity("city");
		owner.setTelephone("1234567890");

		Validator validator = createValidator();
		Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Owner> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("lastName");
		assertThat(violation.getMessage()).isEqualTo("must not be empty");
	}

	@Test
	void shouldValidateWhenAddressIsEmpty() {

		Owner owner = new Owner();
		owner.setFirstName("firstName");
		owner.setLastName("lastName");
		owner.setAddress("");
		owner.setCity("city");
		owner.setTelephone("1234567890");

		Validator validator = createValidator();
		Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Owner> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("address");
		assertThat(violation.getMessage()).isEqualTo("must not be empty");
	}

	@Test
	void shouldValidateWhenCityIsEmpty() {

		Owner owner = new Owner();
		owner.setFirstName("firstName");
		owner.setLastName("lastName");
		owner.setAddress("address");
		owner.setCity("");
		owner.setTelephone("1234567890");

		Validator validator = createValidator();
		Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Owner> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("city");
		assertThat(violation.getMessage()).isEqualTo("must not be empty");
	}

	@Test
	void shouldValidateWhenTelephoneIsEmpty() {

		Owner owner = new Owner();
		owner.setFirstName("firstName");
		owner.setLastName("lastName");
		owner.setAddress("address");
		owner.setCity("city");
		owner.setTelephone("");

		Validator validator = createValidator();
		Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Owner> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("telephone");
		assertThat(violation.getMessage()).isEqualTo("must not be empty");
	}

	@Test
	void shouldValidateWhenTelephoneHasNonDigits() {

		Owner owner = new Owner();
		owner.setFirstName("firstName");
		owner.setLastName("lastName");
		owner.setAddress("address");
		owner.setCity("city");
		owner.setTelephone("abc123def");

		Validator validator = createValidator();
		Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Owner> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("telephone");
		assertThat(violation.getMessage()).isEqualTo("Telephone number can only contain digits");
	}

	@Test
	void shouldValidateWhenTelephoneIsTooShort() {

		Owner owner = new Owner();
		owner.setFirstName("firstName");
		owner.setLastName("lastName");
		owner.setAddress("address");
		owner.setCity("city");
		owner.setTelephone("12345"); // 5 digits, too short

		Validator validator = createValidator();
		Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Owner> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("telephone");
		assertThat(violation.getMessage()).isEqualTo("Telephone number must be between 10 and 15 digits long");
	}

	@Test
	void shouldValidateWhenTelephoneIsTooLong() {

		Owner owner = new Owner();
		owner.setFirstName("firstName");
		owner.setLastName("lastName");
		owner.setAddress("address");
		owner.setCity("city");
		owner.setTelephone("1234567890123456"); // 16 digits, too long

		Validator validator = createValidator();
		Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

		assertThat(constraintViolations).hasSize(1);
		ConstraintViolation<Owner> violation = constraintViolations.iterator().next();
		assertThat(violation.getPropertyPath().toString()).isEqualTo("telephone");
		assertThat(violation.getMessage()).isEqualTo("Telephone number must be between 10 and 15 digits long");
	}

	@Test
	void shouldValidateWhenTelephoneIsValid() {

		Owner owner = new Owner();
		owner.setFirstName("firstName");
		owner.setLastName("lastName");
		owner.setAddress("address");
		owner.setCity("city");
		owner.setTelephone("1234567890"); // 10 digits, valid

		Validator validator = createValidator();
		Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

		assertThat(constraintViolations).isEmpty();
	}

	@Test
	void shouldValidateWhenTelephoneIsMaxValidLength() {

		Owner owner = new Owner();
		owner.setFirstName("firstName");
		owner.setLastName("lastName");
		owner.setAddress("address");
		owner.setCity("city");
		owner.setTelephone("123456789012345"); // 15 digits, valid

		Validator validator = createValidator();
		Set<ConstraintViolation<Owner>> constraintViolations = validator.validate(owner);

		assertThat(constraintViolations).isEmpty();
	}

	@Test
	void testGetPet() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Lucky");
		Pet pet2 = new Pet();
		pet2.setName("Lucky");
		owner.addPet(pet);
		assertThat(owner.getPet("Lucky")).isEqualTo(pet);
	}

	@Test
	void testGet  PetWith  DifferentCase() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Lucky");
		owner.addPet(pet);
		assertThat(owner.getPet("luCKy")).isEqualTo(pet);
	}

	@Test
	void testGetPetWithNewPet() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Lucky");
		pet.setBirthDate(LocalDate.now());
		owner.addPet(pet);
		Pet newPet = new Pet();
		newPet.setName("Lucky");
		assertThat(owner.getPet("Lucky", true)).isEqualTo(pet);
	}

	@Test
	void testGetPets() {
		Owner owner = new Owner();
		Pet pet = new Pet();
		pet.setName("Lucky");
		Pet pet2 = new Pet();
		pet2.setName("Daisy");
		owner.addPet(pet);
		owner.addPet(pet2);
		assertThat(owner.getPets()).containsExactly(pet2, pet);
	}

}