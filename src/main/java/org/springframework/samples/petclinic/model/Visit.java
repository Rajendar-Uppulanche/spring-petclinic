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
package org.springframework.samples.petclinic.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Maciej Szalزد
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

	@Column(name = "visit_date")
	private LocalDateTime date;

	@Column(name = "description", length = 255)
	private String description;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	private Pet pet;

	@Column(name = "check_in_time")
	private OffsetDateTime checkInTime;

	@Column(name = "check_out_time")
	private OffsetDateTime checkOutTime;

	public LocalDateTime getDate() {
		return this.date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public boolean isDateDefined() {
		return this.date != null;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Pet getPet() {
		return pet;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

	public OffsetDateTime getCheckInTime() {
		return checkInTime;
	}

	public void setCheckInTime(OffsetDateTime checkInTime) {
		this.checkInTime = checkInTime;
	}

	public OffsetDateTime getCheckOutTime() {
		return checkOutTime;
	}

	public void setCheckOutTime(OffsetDateTime checkOutTime) {
		this.checkOutTime = checkOutTime;
	}

	public boolean isCheckInTimeDefined() {
		return this.checkInTime != null;
	}

	public boolean isCheckOutTimeDefined() {
		return this.checkOutTime != null;
	}

	public String getDuration() {
		if (this.checkInTime == null || this.checkOutTime == null) {
			return "In Progress";
		}
		Duration duration = Duration.between(this.checkInTime, this.checkOutTime);
		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		return String.format("%02d:%02d", hours, minutes);
	}

	public String getFormattedCheckInTime() {
		if (this.checkInTime == null) {
			return "";
		}
		// Assuming clinic's local timezone is UTC for simplicity in this example.
		// In a real application, this would be fetched from configuration or user profile.
		ZoneOffset clinicZoneOffset = ZoneOffset.UTC;
		return this.checkInTime.atZoneSameInstant(clinicZoneOffset).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	public String getFormattedCheckOutTime() {
		if (this.checkOutTime == null) {
			return "";
		}
		// Assuming clinic's local timezone is UTC for simplicity in this example.
		// In a real application, this would be fetched from configuration or user profile.
		ZoneOffset clinicZoneOffset = ZoneOffset.UTC;
		return this.checkOutTime.atZoneSameInstant(clinicZoneOffset).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

}
