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
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Wick Dynex
 */
@Service
public class OwnerService {

	private final OwnerRepository ownerRepository;

	public OwnerService(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	@Transactional(readOnly = true)
	public Optional<Owner> findOwnerById(Integer id) {
		return ownerRepository.findById(id);
	}

	@Transactional
	public void saveOwner(Owner owner) {
		ownerRepository.save(owner);
	}

	/**
	 * Retrieve {@link Owner}s from the data store by last name, supporting partial and
	 * case-insensitive matching. If the lastName is empty, all owners are returned.
	 * Results are sorted alphabetically by last name.
	 * @param lastName Value to search for (can be empty for all owners)
	 * @param pageable Pagination information
	 * @return a Page of matching {@link Owner}s
	 */
	@Transactional(readOnly = true)
	public Page<Owner> findOwnersByLastName(String lastName, Pageable pageable) {
		if (lastName == null || lastName.isBlank()) {
			return ownerRepository.findAllByOrderByLastNameAsc(pageable);
		}
		return ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc(lastName, pageable);
	}

	@Transactional(readOnly = true)
	public Collection<Owner> findAllOwners() {
		return ownerRepository.findAll();
	}

}
