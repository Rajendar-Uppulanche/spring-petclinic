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
package org.springframework.samples.petclinic.vet;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Repository class for {@link Vet} domain objects
 *
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Christoph Illies
 * @author Vitězslav Vondra
 */
public interface VetRepository extends Repository<Vet, Integer> {

	/**
	 * Retrieve all {@link Vet}s from the data store, returning a list of all {@link Vet}s.
	 * @return a collection of {@link Vet}s
	 */
	@Cacheable("vets")
	@Query("SELECT DISTINCT vet FROM Vet vet LEFT JOIN FETCH vet.specialties ORDER BY vet.lastName ASC")
	List<Vet> findAll() throws DataAccessException;

	/**
	 * Retrieve all {@link Vet}s from the data store, returning a paginated list of all {@link Vet}s.
	 * @param pageable the pagination information
	 * @return a paginated collection of {@link Vet}s
	 */
	@Cacheable("vets")
	Page<Vet> findAll(Pageable pageable);

	/**
	 * Retrieve {@link Vet}s by name.
	 * @param name the name to search for
	 * @return a collection of matching {@link Vet}s
	 */
	@Query("SELECT vet FROM Vet vet WHERE vet.firstName LIKE :name OR vet.lastName LIKE :name")
	Collection<Vet> findByName(@Param("name") String name);

	/**
	 * Retrieve {@link Vet}s by ID.
	 * @param id the ID to search for
	 * @return a collection of matching {@link Vet}s
	 */
	@Query("SELECT vet FROM Vet vet WHERE vet.id =:id")
	Optional<Vet> findById(@Param("id") Integer id);

}
