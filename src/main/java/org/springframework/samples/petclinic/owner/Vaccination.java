package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "vaccinations")
public class Vaccination extends BaseEntity {

    @Column(name = "type")
    @NotBlank
    private String type;

    @Column(name = "due_date")
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
}