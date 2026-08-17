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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for `Owner` domain objects, encapsulating business logic.
 *
 * @author Wick Dynex
 */
@Service
public class OwnerService {

	private final OwnerRepository ownerRepository;

	public OwnerService(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	@Transactional(readOnly = true)
	public Page<Owner> searchOwnersByLastName(String searchTerm, Pageable pageable) {
		String sanitizedSearchTerm = (searchTerm != null) ? searchTerm.trim() : "";

		// Ensure consistent ordering by last name then first name for all searches
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("lastName", "firstName"));

		if (sanitizedSearchTerm.isEmpty()) {
			// If search term is empty, return all owners with default sorting
			return ownerRepository.findAll(sortedPageable);
		}
		// Otherwise, use the specific search method
		return ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAscFirstNameAsc(sanitizedSearchTerm, sortedPageable);
	}

	@Transactional(readOnly = true)
	public Optional<Owner> findById(Integer id) {
		return ownerRepository.findById(id);
	}

	@Transactional
	public Owner save(Owner owner) {
		return ownerRepository.save(owner);
	}

}
