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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.List;

/**
 * Mostly used to call various repositories. Therefore, this class is not a "service" in the sense
 * that it implements business logic. It just orchestrates calls to data access methods.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Service
public class PetService {

	private final PetRepository petRepository;

	private final OwnerRepository ownerRepository;

	@Autowired
	public PetService(PetRepository petRepository, OwnerRepository ownerRepository) {
		this.petRepository = petRepository;
		this.ownerRepository = ownerRepository;
	}

	@Transactional(readOnly = true)
	@Cacheable(key = "#root.target.getOwnerById(#ownerId)")
	public Owner findOwnerById(int ownerId) throws DataAccessException {
		return ownerRepository.findById(ownerId);
	}

	@Transactional(readOnly = true)
	public Collection<PetType> findPetTypes() throws DataAccessException {
		return petRepository.findPetTypes();
	}

	@Transactional(readOnly = true)
	public Pet findPetById(int id) throws DataAccessException {
		return petRepository.findById(id);
	}

	@Transactional(rollbackFor = Exception.class)
	public void savePet(Pet pet) throws DataAccessException {
		petRepository.save(pet);
	}

	@Transactional(rollbackFor = Exception.class)
	public void deletePet(Pet pet) throws DataAccessException {
		petRepository.delete(pet);
	}

	/**
	 * Calculates the age of a pet in years and months.
	 *
	 * @param pet The pet for which to calculate the age.
	 * @return A string representing the pet's age (e.g., "2 years, 3 months", "< 1 month").
	 */
	@Transactional(readOnly = true)
	public String calculatePetAge(Pet pet) {
		if (pet == null || pet.getBirthDate() == null) {
			return "Unknown";
		}

		LocalDate birthDate = pet.getBirthDate();
		LocalDate currentDate = LocalDate.now();
		Period period = Period.between(birthDate, currentDate);

		int years = period.getYears();
		int months = period.getMonths();

		if (years == 0 && months == 0) {
			return "< 1 month";
		} else if (years == 0) {
			return months + " months";
		} else {
			return years + " years, " + months + " months";
		}
	}

	// For cache key generation
	public int getOwnerById(int ownerId) {
		return ownerId;
	}
}
