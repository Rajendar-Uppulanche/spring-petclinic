package com.example.petclinic.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

    @Column(name = "visit_date")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate visitDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private Veterinarian veterinarian;

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public String getDescription() {
        return description;
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

    public Veterinarian getVeterinarian() {
        return veterinarian;
    }

    public void setVeterinarian(Veterinarian veterinarian) {
        this.veterinarian = veterinarian;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Visit visit = (Visit) o;
        return Objects.equals(visitDate, visit.visitDate) &&
               Objects.equals(description, visit.description) &&
               Objects.equals(pet, visit.pet) &&
               Objects.equals(veterinarian, visit.veterinarian);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), visitDate, description, pet, veterinarian);
    }

    @Override
    public String toString() {
        return "Visit{" +
               "id=" + getId() +
               ", visitDate=" + visitDate +
               ", description='" + description + '\'' +
               ", pet=" + (pet != null ? pet.getName() : "null") +
               ", veterinarian=" + (veterinarian != null ? veterinarian.getName() : "null") +
               '}';
    }
}