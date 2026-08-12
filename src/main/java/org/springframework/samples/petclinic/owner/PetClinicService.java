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

import java.util.Collection;

import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;

/**
 * @author Maciej Szalزد
 */
public interface PetClinicService {

	Owner findOwnerById(int id) throws org.springframework.samples.petclinic.exceptions.OwnerNotFoundException;

	Pet findPetById(int id);

	Visit findVisitById(int id);

	void saveVisit(Visit visit);

	void saveOwner(Owner owner);

	void savePet(Pet pet);

	Collection<PetType> findPetTypes();

	Collection<Vet> findAllVets();

	Collection<Owner> findOwners(String lastName);

	// Added for Visit History List Display
	Collection<Visit> findVisitsByPetId(int petId);

}
