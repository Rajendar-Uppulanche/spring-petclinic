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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

/**
 * Service class for managing visits, including check-in and check-out operations.
 *
 * @author Synapse Builder
 */
@Service
public class VisitService {

	private final VisitRepository visitRepository;

	public VisitService(VisitRepository visitRepository) {
		this.visitRepository = visitRepository;
	}

	/**
	 * Handles the check-in process for a visit.
	 * @param visitId The ID of the visit to check in.
	 * @return The updated Visit object.
	 * @throws IllegalArgumentException if the visit is not found or check-in time is invalid.
	 */
	@Transactional
	public Visit checkIn(Integer visitId) {
		Optional<Visit> optionalVisit = visitRepository.findById(visitId);
		if (optionalVisit.isEmpty()) {
			throw new IllegalArgumentException("Visit not found with ID: " + visitId);
		}

		Visit visit = optionalVisit.get();
		if (visit.getCheckInTime() != null) {
			throw new IllegalArgumentException("Visit is already checked in.");
		}

		LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);

		// BR-005: Check-in time MUST NOT be set more than 24 hours in the future
		if (visit.getDate().atStartOfDay().plusDays(1).isBefore(nowUtc.minusHours(24))) { // Assuming visit.date is the scheduled date
			throw new IllegalArgumentException("Check-in time cannot be more than 24 hours in the future relative to the scheduled date.");
		}

		visit.setCheckInTime(nowUtc);
		return visitRepository.save(visit);
	}

	/**
	 * Handles the check-out process for a visit.
	 * @param visitId The ID of the visit to check out.
	 * @return The updated Visit object.
	 * @throws IllegalArgumentException if the visit is not found, not checked in, or check-out time is invalid.
	 */	@Transactional
	public Visit checkOut(Integer visitId) {
		Optional<Visit> optionalVisit = visitRepository.findById(visitId);
		if (optionalVisit.isEmpty()) {
			throw new IllegalArgumentException("Visit not found with ID: " + visitId);
		}

		Visit visit = optionalVisit.get();
		if (visit.getCheckInTime() == null) {
			throw new IllegalArgumentException("Visit must be checked in before checking out.");
		}
		if (visit.getCheckOutTime() != null) {
			throw new IllegalArgumentException("Visit is already checked out.");
		}

		LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);

		// BR-004: Check-out time MUST NOT be earlier than check-in time
		if (nowUtc.isBefore(visit.getCheckInTime())) {
			throw new IllegalArgumentException("Check-out time cannot be earlier than check-in time.");
		}

		visit.setCheckOutTime(nowUtc);
		return visitRepository.save(visit);
	}

	/**
	 * Retrieves a visit by its ID.
	 * @param visitId The ID of the visit.
	 * @return The Visit object.
	 * @throws IllegalArgumentException if the visit is not found.
	 */
	public Visit findVisitById(Integer visitId) {
		return visitRepository.findById(visitId)
			.orElseThrow(() -> new IllegalArgumentException("Visit not found with ID: " + visitId));
	}

}
