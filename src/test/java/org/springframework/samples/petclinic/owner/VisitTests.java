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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link Visit} model and {@link VisitService}.
 *
 * @author Synapse Builder
 */
@ExtendWith(MockitoExtension.class)
class VisitTests {

	@Mock
	private VisitRepository visitRepository;

	@InjectMocks
	private VisitService visitService;

	private Visit visit;

	@BeforeEach
	void setUp() {
		visit = new Visit();
		visit.setId(1);
		visit.setDate(LocalDate.now());
		visit.setDescription("Routine checkup");
	}

	@Test
	void testGetDurationFormatted_noTimes() {
		assertThat(visit.getDurationFormatted()).isNull();
	}

	@Test
	void testGetDurationFormatted_inProgress() {
		visit.setCheckInTime(LocalDateTime.now(ZoneOffset.UTC));
		assertThat(visit.getDurationFormatted()).isEqualTo("In Progress");
	}

	@Test
	void testGetDurationFormatted_completed() {
		LocalDateTime checkIn = LocalDateTime.now(ZoneOffset.UTC).minusHours(2).minusMinutes(30);
		LocalDateTime checkOut = LocalDateTime.now(ZoneOffset.UTC);
		visit.setCheckInTime(checkIn);
		visit.setCheckOutTime(checkOut);
		assertThat(visit.getDurationFormatted()).isEqualTo("02:30");
	}

	@Test
	void testCheckIn_success() {
		when(visitRepository.findById(1)).thenReturn(Optional.of(visit));
		when(visitRepository.save(any(Visit.class))).thenReturn(visit);

		Visit checkedInVisit = visitService.checkIn(1);

		assertThat(checkedInVisit.getCheckInTime()).isNotNull();
		assertThat(checkedInVisit.getCheckOutTime()).isNull();
		assertThat(checkedInVisit.isCheckedIn()).isTrue();
		assertThat(checkedInVisit.isCheckedOut()).isFalse();
	}

	@Test
	void testCheckIn_visitNotFound() {
		when(visitRepository.findById(1)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> visitService.checkIn(1),
			"Visit not found with ID: 1");
	}

	@Test
	void testCheckIn_alreadyCheckedIn() {
		visit.setCheckInTime(LocalDateTime.now(ZoneOffset.UTC));
		when(visitRepository.findById(1)).thenReturn(Optional.of(visit));
		assertThrows(IllegalArgumentException.class, () -> visitService.checkIn(1),
			"Visit is already checked in.");
	}

	@Test
	void testCheckIn_futureCheckInInvalid() {
		// Set visit date far in the past to simulate check-in being more than 24 hours in future relative to scheduled date
		visit.setDate(LocalDate.now().minusYears(1));
		when(visitRepository.findById(1)).thenReturn(Optional.of(visit));
		assertThrows(IllegalArgumentException.class, () -> visitService.checkIn(1),
			"Check-in time cannot be more than 24 hours in the future relative to the scheduled date.");
	}

	@Test
	void testCheckOut_success() {
		visit.setCheckInTime(LocalDateTime.now(ZoneOffset.UTC).minusHours(1));
		when(visitRepository.findById(1)).thenReturn(Optional.of(visit));
		when(visitRepository.save(any(Visit.class))).thenReturn(visit);

		Visit checkedOutVisit = visitService.checkOut(1);

		assertThat(checkedOutVisit.getCheckOutTime()).isNotNull();
		assertThat(checkedOutVisit.isCheckedOut()).isTrue();
	}

	@Test
	void testCheckOut_visitNotFound() {
		when(visitRepository.findById(1)).thenReturn(Optional.empty());
		assertThrows(IllegalArgumentException.class, () -> visitService.checkOut(1),
			"Visit not found with ID: 1");
	}

	@Test
	void testCheckOut_notCheckedIn() {
		when(visitRepository.findById(1)).thenReturn(Optional.of(visit));
		assertThrows(IllegalArgumentException.class, () -> visitService.checkOut(1),
			"Visit must be checked in before checking out.");
	}

	@Test
	void testCheckOut_alreadyCheckedOut() {
		visit.setCheckInTime(LocalDateTime.now(ZoneOffset.UTC).minusHours(2));
		visit.setCheckOutTime(LocalDateTime.now(ZoneOffset.UTC).minusHours(1));
		when(visitRepository.findById(1)).thenReturn(Optional.of(visit));
		assertThrows(IllegalArgumentException.class, () -> visitService.checkOut(1),
			"Visit is already checked out.");
	}

	@Test
	void testCheckOut_earlierThanCheckIn() {
		LocalDateTime checkIn = LocalDateTime.now(ZoneOffset.UTC).plusHours(1);
		visit.setCheckInTime(checkIn);
		when(visitRepository.findById(1)).thenReturn(Optional.of(visit));
		assertThrows(IllegalArgumentException.class, () -> visitService.checkOut(1),
			"Check-out time cannot be earlier than check-in time.");
	}

}
