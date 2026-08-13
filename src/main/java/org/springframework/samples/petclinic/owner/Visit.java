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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 * @author Dave Syer
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

	@Column(name = "visit_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@NotBlank
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private VisitStatus status;

	/**
	 * Creates a new instance of Visit for tomorrow
	 */
	public Visit() {
		this.date = LocalDate.now().plusDays(1);
		this.status = VisitStatus.SCHEDULED;
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

	public VisitStatus getStatus() {
		return status;
	}

	public void setStatus(VisitStatus status) {
		this.status = status;
	}

	/**
	 * Validates if a transition from the current status to a new status is allowed.
	 * @param newStatus The status to transition to.
	 * @return true if the transition is valid, false otherwise.
	 */
	public boolean isValidTransition(VisitStatus newStatus) {
		if (this.status == newStatus) {
			return true; // No change is always valid
		}

		switch (this.status) {
			case SCHEDULED:
				return newStatus == VisitStatus.IN_PROGRESS || newStatus == VisitStatus.CANCELLED;
			case IN_PROGRESS:
				return newStatus == VisitStatus.COMPLETED;
			case COMPLETED:
				return false; // Completed visits cannot transition to any other status
			case CANCELLED:
				return false; // Cancelled visits cannot transition to any other status
			default:
				return false;
		}
	}
}
