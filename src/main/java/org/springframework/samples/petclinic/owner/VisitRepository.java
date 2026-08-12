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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repository class data access object for {@link Visit} domain objects
 *
 * @author Dave Syer
 */
public interface VisitRepository extends JpaRepository<Visit, Integer> {

    /**
     * Retrieve all {@link Visit}s for a given pet id.
     * @param petId The ID of the pet to retrieve visits for.
     * @return a Collection of Visits
     */
    Collection<Visit> findByPetId(int petId);

    /**
     * Retrieve a paginated and sorted list of {@link Visit}s for a given pet id.
     * @param petId The ID of the pet to retrieve visits for.
     * @param pageable The pagination and sorting information.
     * @return a Page of Visits
     */
    Page<Visit> findByPetId(int petId, Pageable pageable);

}
