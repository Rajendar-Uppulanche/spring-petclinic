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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

	/**
	 * Holds date that will be filled by controller
	 */
	@Column(name = "visit_date")
	@NotNull
	private LocalDate date;

	@Column(name = "description", length = 256)
	@NotBlank
	private String description;

	@Column(name = "check_in_time")
	private LocalDateTime checkInTime;

	@Column(name = "check_out_time")
	private LocalDateTime checkOutTime;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	private Pet pet;

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public boolean isNew() {
		return this.id == null;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Pet getPet() {
		return this.pet;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
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

}
