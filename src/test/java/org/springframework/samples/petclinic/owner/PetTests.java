/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the {@link Pet} entity.
 */
class PetTests {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void shouldNotValidateWhenBirthDateIsInFuture() {
		Pet pet = new Pet();
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now().plusDays(1)); // Future date

		Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
		assertThat(violations).isNotEmpty();
		assertThat(violations).anyMatch(violation ->
			"birthDate".equals(violation.getPropertyPath().toString()) &&
			"must be a date in the past or in the present".equals(violation.getMessage())
		);
	}

	@Test
	void shouldValidateWhenBirthDateIsInPast() {
		Pet pet = new Pet();
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now().minusDays(1)); // Past date

		Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
		// Expecting violations for other fields like 'type' if not set, but not for birthDate
		// For this test, we only care that birthDate itself is valid.
		assertThat(violations).noneMatch(violation ->
			"birthDate".equals(violation.getPropertyPath().toString()) &&
			"must be a date in the past or in the present".equals(violation.getMessage())
		);
	}

	@Test
	void shouldValidateWhenBirthDateIsPresent() {
		Pet pet = new Pet();
		pet.setName("Leo");
		pet.setBirthDate(LocalDate.now()); // Present date

		Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
		assertThat(violations).noneMatch(violation ->
			"birthDate".equals(violation.getPropertyPath().toString()) &&
			"must be a date in the past or in the present".equals(violation.getMessage())
		);
	}

	@Test
	void shouldNotValidateWhenNameIsBlank() {
		Pet pet = new Pet();
		pet.setName("");
		pet.setBirthDate(LocalDate.now());

		Set<ConstraintViolation<Pet>> violations = validator.validate(pet);
		assertThat(violations).isNotEmpty();
		assertThat(violations).anyMatch(violation ->
			"name".equals(violation.getPropertyPath().toString()) &&
			"must not be blank".equals(violation.getMessage())
		);
	}

}
