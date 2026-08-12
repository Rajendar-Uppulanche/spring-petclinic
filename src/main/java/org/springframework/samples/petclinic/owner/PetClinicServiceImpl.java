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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Maciej Szalزد
 */
@Service
class PetClinicServiceImpl implements PetClinicService {

	private final VisitRepository visitRepository;
	private final OwnerRepository ownerRepository;
	private final PetRepository petRepository;
	private final VetRepository vetRepository;

	@Autowired
	public PetClinicServiceImpl(VisitRepository visitRepository, OwnerRepository ownerRepository, PetRepository petRepository, VetRepository vetRepository) {
		this.visitRepository = visitRepository;
		this.ownerRepository = ownerRepository;
		this.petRepository = petRepository;
		this.vetRepository = vetRepository;
	}

	@Transactional(readOnly = true)
	public Collection<PetType> findPetTypes() {
		return petRepository.findPetTypes();
	}

	@Transactional(readOnly = true)
	public Owner findOwnerById(int id) throws org.springframework.samples.petclinic.exceptions.OwnerNotFoundException {
		return ownerRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public Pet findPetById(int id) {
		return petRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public Visit findVisitById(int id) {
		return visitRepository.findById(id);
	}

	@Transactional
	public void saveVisit(Visit visit) {
		visitRepository.save(visit);
	}

	@Transactional
	public void saveOwner(Owner owner) {
		ownerRepository.save(owner);
	}

	@Transactional
	public void savePet(Pet pet) {
		petRepository.save(pet);
	}

	@Transactional(readOnly = true)
	public Collection<Vet> findAllVets() {
		return vetRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Collection<Owner> findOwners(String lastName) {
		return ownerRepository.findByLastName(lastName);
	}

	// Added for Visit History List Display
	@Override
	@Transactional(readOnly = true)
	public Collection<Visit> findVisitsByPetId(int petId) {
		return visitRepository.findByPetId(petId);
	}

}
