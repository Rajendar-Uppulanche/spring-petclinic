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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

/**
 * Repository class for <code>Owner</code> domain objects. All method names are compliant
 * with Spring Data naming conventions so this interface can easily be extended for Spring
 * Data. See:
 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Wick Dynex
 * @author SP-55
 */
public interface OwnerRepository extends Repository<Owner, Integer> {

	/**
	 * Retrieve all <code>PetType</code>s from the data store.
	 * @return a <code>Collection</code> of <code>PetType</code>s
	 */
	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	@Transactional(readOnly = true)
	List<PetType> findPetTypes();

	/**
	 * Retrieve <code>Owner</code>s from the data store by last name, returning all owners
	 * whose last name starts with the given name.
	 * @param lastName Value to search for
	 * @return a <code>Collection</code> of matching <code>Owner</code>s (or an empty
	 * <code>Collection</code> if none found)
	 */
	@Query("SELECT owner FROM Owner owner WHERE owner.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Page<Owner> findByLastName(@Param("lastName") String lastName, Pageable pageable);

	@Query("SELECT owner FROM Owner owner WHERE owner.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Page<Owner> findByLastNameStartingWith(@Param("lastName") String lastName, Pageable pageable);

	/**
	 * Retrieve an <code>Owner</code> from the data store by id.
	 * @param id the id to search for
	 * @return the <code>Owner</code> if found
	 */
	@Transactional(readOnly = true)
	Optional<Owner> findById(Integer id);

	/**
	 * Save an <code>Owner</code> to the data store, either inserting or updating it.
	 * @param owner the <code>Owner</code> to save
	 */
	void save(Owner owner);

	/**
	 * Save an <code>Owner</code> to the data store, either inserting or updating it.
	 * @param owner the <code>Owner</code> to save
	 */
	void saveAndFlush(Owner owner);

}
