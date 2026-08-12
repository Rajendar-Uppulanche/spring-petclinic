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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 * @author Dave Syer
 * @author Synapse Builder
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

	@Column(name = "visit_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@NotBlank
	private String description;

	@Column(name = "check_in_time")
	private LocalDateTime checkInTime;

	@Column(name = "check_out_time")
	private LocalDateTime checkOutTime;

	/**
	 * Creates a new instance of Visit for tomorrow
	 */
	public Visit() {
		this.date = LocalDate.now().plusDays(1);
	}

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(LocalDateTime checkInTime) {
		this.checkInTime = checkInTime;
	}

	public LocalDateTime getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(LocalDateTime checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	/**
	 * Calculates the duration of the visit if both check-in and check-out times are present.
	 * Returns "In Progress" if only check-in time is present.
	 * Returns null if neither is present.
	 * @return Formatted duration string (HH:MM), "In Progress", or null.
	 */
	public String getDurationFormatted() {
		if (checkInTime != null && checkOutTime != null) {
			Duration duration = Duration.between(checkInTime, checkOutTime);
			long hours = duration.toHours();
			long minutes = duration.toMinutes() % 60;
			return String.format("%02d:%02d", hours, minutes);
		} else if (checkInTime != null) {
			return "In Progress";
		}
		return null;
	}

	/**
	 * Helper to check if the visit has been checked in.
	 * @return true if check-in time is set, false otherwise.
	 */
	public boolean isCheckedIn() {
		return this.checkInTime != null;
	}

	/**
	 * Helper to check if the visit has been checked out.
	 * @return true if check-out time is set, false otherwise.
	 */
	public boolean isCheckedOut() {
		return this.checkOutTime != null;
	}

}
