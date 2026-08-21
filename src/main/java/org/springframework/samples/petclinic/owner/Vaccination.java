package org.springframework.samples.petclinic.owner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "vaccinations")
public class Vaccination extends BaseEntity {

    @Column(name = "type")
    @NotBlank
    private String type;

    @Column(name = "due_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    @Override
    public String toString() {
        return "Vaccination{" +
               "id=" + getId() +
               ", type='" + type + "'" +
               ", dueDate=" + dueDate +
               ", petId=" + (pet != null ? pet.getId() : "null") +
               '}';
    }
}