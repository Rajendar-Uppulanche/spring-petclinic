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
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

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

	public enum VisitType {
		REGULAR,
		PREVENTIVE
	}

	@Column(name = "visit_type")
	@Enumerated(EnumType.STRING)
	private VisitType visitType;

	@Embedded
	private PreventiveCareDetails preventiveCareDetails;

	/**
	 * Creates a new instance of Visit for tomorrow
	 */
	public Visit() {
		this.date = LocalDate.now().plusDays(1);
		this.visitType = VisitType.REGULAR; // Default to REGULAR
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

	public VisitType getVisitType() {
		return visitType;
	}

	public void setVisitType(VisitType visitType) {
		this.visitType = visitType;
	}

	public PreventiveCareDetails getPreventiveCareDetails() {
		return preventiveCareDetails;
	}

	public void setPreventiveCareDetails(PreventiveCareDetails preventiveCareDetails) {
		this.preventiveCareDetails = preventiveCareDetails;
	}

	@Embeddable
	public static class PreventiveCareDetails {
		@Column(name = "vaccine_name")
		private String vaccineName;

		@Column(name = "dosage")
		private String dosage;

		@Column(name = "next_due_date")
		@DateTimeFormat(pattern = "yyyy-MM-dd")
		private LocalDate nextDueDate;

		public String getVaccineName() {
			return vaccineName;
		}

		public void setVaccineName(String vaccineName) {
			this.vaccineName = vaccineName;
		}

		public String getDosage() {
			return dosage;
		}

		public void setDosage(String dosage) {
			this.dosage = dosage;
		}

		public LocalDate getNextDueDate() {
			return nextDueDate;
		}

		public void setNextDueDate(LocalDate nextDueDate) {
			this.nextDueDate = nextDueDate;
		}
	}
}
