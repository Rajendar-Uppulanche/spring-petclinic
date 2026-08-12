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

import org.springframework.data.repository.Repository;

import java.util.Optional;

/**
 * Repository class for <code>Visit</code> domain objects All method names are compliant with Spring Data naming
 * conventions so this interface can easily be extended for Spring Data.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Synapse Builder
 */
public interface VisitRepository extends Repository<Visit, Integer> {

	/**
	 * Save a {@link Visit} to the data store.
	 * @param visit the {@link Visit} to save
	 */
	void save(Visit visit);

	/**
	 * Retrieve a {@link Visit} by its ID.
	 * @param id the ID to search for
	 * @return the {@link Visit} if found
	 */
	Optional<Visit> findById(Integer id);

}
