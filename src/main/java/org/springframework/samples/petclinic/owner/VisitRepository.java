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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository class for {@link Visit} domain objects
 *
 * @author Dave Syer
 */
public interface VisitRepository extends JpaRepository<Visit, Integer> {

	/**
	 * Retrieve all {@link Visit}s for a specific {@link Pet}.
	 * @param petId the ID of the Pet to retrieve visits for
	 * @return a Collection of Visits
	 */
	@Query("SELECT v FROM Visit v WHERE v.pet.id = :petId ORDER BY v.date DESC")
	Collection<Visit> findByPetId(@Param("petId") int petId);

	/**
	 * Retrieve all {@link Visit}s for a specific {@link Pet} with pagination and sorting.
	 * @param petId the ID of the Pet to retrieve visits for
	 * @param page the page number
	 * @param size the page size
	 * @param sort the sorting column
	 * @param direction the sorting direction
	 * @return a Page of Visits
	 */
	@Query(value = "SELECT v FROM Visit v WHERE v.pet.id = :petId ORDER BY v.date DESC", countQuery = "SELECT count(v) FROM Visit v WHERE v.pet.id = :petId")
	VisitPage findByPetId(@Param("petId") int petId, org.springframework.data.domain.Pageable pageable);

}
