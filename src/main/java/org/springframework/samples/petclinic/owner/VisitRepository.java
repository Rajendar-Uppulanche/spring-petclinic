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

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for {@link Visit} domain objects
 *
 * @author Dave Syer
 */
public interface VisitRepository extends JpaRepository<Visit, Integer> {

	/**
	 * Find all visits for a given pet
	 * @param petId the ID of the pet to retrieve visits for
	 * @return a List of Visits
	 */
	@Query("SELECT v FROM Visit v WHERE v.pet.id = :petId")
	List<Visit> findByPetId(@Param("petId") int petId);

	/**
	 * Find visits by date range and/or keyword in description.
	 * @param fromDate The start date of the range (inclusive).
	 * @param toDate The end date of the range (inclusive).
	 * @param keyword The keyword to search for in the description (case-insensitive).
	 * @return A list of visits matching the criteria.
	 */
	@Query("SELECT v FROM Visit v WHERE (:fromDate IS NULL OR v.date >= :fromDate) AND (:toDate IS NULL OR v.date <= :toDate) AND (:keyword IS NULL OR LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
	List<Visit> findByDateBetweenOrDescriptionContainingIgnoreCase(@Param("fromDate") LocalDate fromDate,
			@Param("toDate") LocalDate toDate, @Param("keyword") String keyword);

}
