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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Simple business object representing a pet's vaccination record.
 *
 * @author Wick Dynex
 */
@Entity
@Table(name = "vaccinations")
public class Vaccination extends BaseEntity {

    @Column(name = "date_administered")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate dateAdministered;

    @ManyToOne
    @JoinColumn(name = "vaccine_type_id")
    @NotNull
    private VaccineType vaccineType;

    @Column(name = "administering_vet")
    @NotEmpty
    private String administeringVet;

    @Column(name = "batch_number")
    @NotEmpty
    private String batchNumber;

    @Column(name = "next_due_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextDueDate; // Can be null if not applicable

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet; // This will be set by the Pet entity's one-to-many relationship

    // Transient field for overdue status, not persisted
    private transient boolean overdue;

    public LocalDate getDateAdministered() {
        return dateAdministered;
    }

    public void setDateAdministered(LocalDate dateAdministered) {
        this.dateAdministered = dateAdministered;
    }

    public VaccineType getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(VaccineType vaccineType) {
        this.vaccineType = vaccineType;
    }

    public String getAdministeringVet() {
        return administeringVet;
    }

    public void setAdministeringVet(String administeringVet) {
        this.administeringVet = administeringVet;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public boolean isOverdue() {
        if (nextDueDate == null) {
            return false;
        }
        return nextDueDate.isBefore(LocalDate.now());
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    @Override
    public String toString() {
        return "Vaccination{" +
               "id=" + getId() +
               ", dateAdministered=" + dateAdministered +
               ", vaccineType=" + (vaccineType != null ? vaccineType.getName() : "null") +
               ", administeringVet='" + administeringVet + "'" +
               ", batchNumber='" + batchNumber + "'" +
               ", nextDueDate=" + nextDueDate +
               ", petId=" + (pet != null ? pet.getId() : "null") +
               '}';
    }
}
