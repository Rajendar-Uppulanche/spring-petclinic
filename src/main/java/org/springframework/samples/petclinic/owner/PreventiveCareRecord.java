package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "preventive_care_records")
public class PreventiveCareRecord extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    @NotNull
    private Pet pet;

    @Column(name = "record_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate recordDate;

    @NotBlank
    @Column(name = "care_type")
    private String careType; // e.g., Vaccination, Deworming, Flea/Tick Prevention

    @Column(name = "description", length = 2000)
    private String description;

    @NotBlank
    @Column(name = "outcome")
    private String outcome; // e.g., Successful, Pending, Adverse Reaction

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public String getCareType() {
        return careType;
    }

    public void setCareType(String careType) {
        this.careType = careType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
}
