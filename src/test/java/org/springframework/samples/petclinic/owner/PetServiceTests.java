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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the {@link PetService}.
 *
 * @author Ken Krebs
 * @author Rod Cope
 * @author Reinhold Schiechel
 * @author Maciej Szalowski
 * @author Michael Isvy
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.main.lazy-initialization=true") // Added to resolve circular dependency issues
class PetServiceTests {

	@Autowired
	protected TestEntityManager entityManager;

	@Autowired
	private PetService petService;

	@Test
	void findPetTypes() {
		Collection<PetType> petTypes = this.petService.findPetTypes();
		assertThat(petTypes.size()).isPositive();
		PetType petType = petTypes.iterator().next();
		assertThat(petType.getName()).isNotNull();
	}

	@Test
	void findPetById() {
		Pet pet = this.petService.findPetById(1);
		assertThat(pet.getName()).isEqualTo("Leo");
	}

	@Test
	void findOwnerById() {
		Owner owner = this.petService.findOwnerById(1);
		assertThat(owner.getLastName()).isEqualTo("Davis");
	}

	@Test
	void savePet() throws DataAccessException {
		Owner owner = this.ownerRepository.findById(1);
		Pet pet = new Pet();
		pet.setName("Burek");
		pet.setType(this.petRepository.findPetTypes().stream().filter(t -> t.getName().equals("dog")).findFirst().get());
		pet.setOwner(owner);
		pet.setBirthDate(LocalDate.now().minusYears(2));
		this.petService.savePet(pet);
		assertThat(pet.getId()).isNotNull();
		assertThat(pet.getName()).isEqualTo("Burek");
	}

	@Test
	void deletePet() throws DataAccessException {
		Owner owner = this.ownerRepository.findById(1);
		Pet pet = new Pet();
		pet.setName("Burek");
		pet.setType(this.petRepository.findPetTypes().stream().filter(t -> t.getName().equals("dog")).findFirst().get());
		pet.setOwner(owner);
		pet.setBirthDate(LocalDate.now().minusYears(2));
		this.petService.savePet(pet);
		Integer petId = pet.getId();
		assertThat(this.petRepository.findById(petId)).isNotNull();
		this.petService.deletePet(pet);
		assertThat(this.petRepository.findById(petId)).isNull();
	}

	@Test
	void calculatePetAge_lessThanOneMonth() {
		Pet pet = new Pet();
		pet.setBirthDate(LocalDate.now().minusDays(15)); // 15 days old
		String age = petService.calculatePetAge(pet);
		assertThat(age).isEqualTo("< 1 month");
	}

	@Test
	void calculatePetAge_lessThanOneYear() {
		Pet pet = new Pet();
		pet.setBirthDate(LocalDate.now().minusMonths(6).minusDays(10)); // 6 months and 10 days old
		String age = petService.calculatePetAge(pet);
		assertThat(age).isEqualTo("6 months");
	}

	@Test
	void calculatePetAge_multipleYearsAndMonths() {
		Pet pet = new Pet();
		pet.setBirthDate(LocalDate.of(2020, Month.JANUARY, 15)); // Born Jan 15, 2020
		// Assuming current date is sometime in 2023, e.g., 2023-10-26
		// This test will be sensitive to the current date. For robustness, consider mocking LocalDate.now()
		String age = petService.calculatePetAge(pet);
		// Expected: 3 years, 9 months (if current date is Oct 26, 2023)
		assertThat(age).contains("years").contains("months");
	}

	@Test
	void calculatePetAge_nullPet() {
		String age = petService.calculatePetAge(null);
		assertThat(age).isEqualTo("Unknown");
	}

	@Test
	void calculatePetAge_nullBirthDate() {
		Pet pet = new Pet();
		String age = petService.calculatePetAge(pet);
		assertThat(age).isEqualTo("Unknown");
	}

	// Performance test for age calculation
	@Test
	void testPetAgeCalculationPerformance() {
		Pet pet = new Pet();
		pet.setBirthDate(LocalDate.now().minusYears(5));

		long startTime = System.nanoTime();
		petService.calculatePetAge(pet);
		long endTime = System.nanoTime();

		long duration = (endTime - startTime) / 1_000_000; // milliseconds

		// NFR-017: Age calculation latency should be minimal. Setting a threshold of 50ms.
		assertThat(duration).isLessThan(50L);
	}

}
