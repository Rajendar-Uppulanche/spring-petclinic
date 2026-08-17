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
package org.springframework.samples.petclinic.appointment;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.Optional;

/**
 * Repository interface for {@link Appointment} instances.
 *
 * @author Wick Dynex
 */
public interface AppointmentRepository extends Repository<Appointment, Integer>, JpaSpecificationExecutor<Appointment> {

	/**
	 * Retrieve all {@link Appointment}s from the data store.
	 * @return a {@link Collection} of {@link Appointment}s
	 */
	Collection<Appointment> findAll();

	/**
	 * Retrieve an {@link Appointment} from the data store by id.
	 * @param id the id to search for
	 * @return the {@link Appointment} if found
	 */
	Optional<Appointment> findById(Integer id);

	/**
	 * Save an {@link Appointment} to the data store.
	 * @param appointment the {@link Appointment} to save
	 */
	void save(Appointment appointment);

}