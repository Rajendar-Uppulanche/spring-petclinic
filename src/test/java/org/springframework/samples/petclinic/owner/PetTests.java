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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link Pet}
 */
class PetTests {

	private Pet pet;

	@BeforeEach
	void setup() {
		pet = new Pet();
	}

	@Test
	void getAgeString_nullBirthDate_returnsAgeUnknown() {
		pet.setBirthDate(null);
		assertThat(pet.getAgeString()).isEqualTo("Age unknown");
	}

	@Test
	void getAgeString_lessThanOneMonthOld_returnsLessThanAMonth() {
		// Set birth date to today minus a few days
		pet.setBirthDate(LocalDate.now().minusDays(10));
		assertThat(pet.getAgeString()).isEqualTo("Less than a month");
	}

	@Test
	void getAgeString_exactlyOneMonthOld_returnsOneMonth() {
		// Set birth date to today minus one month
		pet.setBirthDate(LocalDate.now().minusMonths(1));
		assertThat(pet.getAgeString()).isEqualTo("1 month");
	}

	@Test
	void getAgeString_oneMonthAndHalf_roundsToOneMonth() {
		// 1 month and 14 days should round down to 1 month
		pet.setBirthDate(LocalDate.now().minusMonths(1).minusDays(14));
		assertThat(pet.getAgeString()).isEqualTo("1 month");
	}

	@Test
	void getAgeString_oneMonthAndHalfPlus_roundsToTwoMonths() {
		// 1 month and 15 days should round up to 2 months
		pet.setBirthDate(LocalDate.now().minusMonths(1).minusDays(15));
		assertThat(pet.getAgeString()).isEqualTo("2 months");
	}

	@Test
	void getAgeString_oneYearOld_returnsOneYear() {
		pet.setBirthDate(LocalDate.now().minusYears(1));
		assertThat(pet.getAgeString()).isEqualTo("1 year");
	}

	@Test
	void getAgeString_twoYearsThreeMonths_returnsCorrectString() {
		pet.setBirthDate(LocalDate.now().minusYears(2).minusMonths(3));
		assertThat(pet.getAgeString()).isEqualTo("2 years 3 months");
	}

	@Test
	void getAgeString_fiveYearsZeroMonths_returnsFiveYears() {
		pet.setBirthDate(LocalDate.now().minusYears(5).minusDays(10)); // Days don't round up to a month
		assertThat(pet.getAgeString()).isEqualTo("5 years");
	}

	@Test
	void getAgeString_elevenMonthsAndHalfPlus_roundsToOneYear() {
		// 11 months and 15 days should round up to 12 months, which is 1 year
		pet.setBirthDate(LocalDate.now().minusMonths(11).minusDays(15));
		assertThat(pet.getAgeString()).isEqualTo("1 year");
	}

	@Test
	void getAgeString_oneYearElevenMonthsAndHalfPlus_roundsToTwoYears() {
		// 1 year, 11 months and 15 days should round up to 2 years
		pet.setBirthDate(LocalDate.now().minusYears(1).minusMonths(11).minusDays(15));
		assertThat(pet.getAgeString()).isEqualTo("2 years");
	}

}
