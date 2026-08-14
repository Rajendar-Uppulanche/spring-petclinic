package org.springframework.samples.petclinic.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Entity
@Table(name = "preventive_care")
public class PreventiveCare extends BaseEntity {

    @Column(name = "care_date")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate careDate;

    @NotEmpty
    @Column(name = "type")
    private String type; // e.g., Vaccination, Treatment, Diagnosis

    @NotEmpty
    @Column(name = "description")
    private String description;

    @Column(name = "notes")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    public LocalDate getCareDate() {
        return careDate;
    }

    public void setCareDate(LocalDate careDate) {
        this.careDate = careDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    @Override
    public String toString() {
        return "PreventiveCare{" +
               "careDate=" + careDate +
               ", type='" + type + '\'' +
               ", description='" + description + '\'' +
               ", notes='" + notes + '\'' +
               ", pet=" + (pet != null ? pet.getName() : "null") +
               '}';
    }
}
