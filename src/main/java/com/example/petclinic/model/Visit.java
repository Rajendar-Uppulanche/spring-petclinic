package com.example.petclinic.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

    @Column(name = "visit_date")
    private LocalDate date;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private Veterinarian veterinarian;

    public Visit() {
        this.date = LocalDate.now();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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
        return Objects.equals(date, visit.date) &&
               Objects.equals(description, visit.description) &&
               Objects.equals(pet, visit.pet) &&
               Objects.equals(veterinarian, visit.veterinarian);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), date, description, pet, veterinarian);
    }

    @Override
    public String toString() {
        return "Visit{" +
               "id=" + getId() +
               ", date=" + date +
               ", description='" + description + '\'' +
               ", pet=" + (pet != null ? pet.getName() : "null") +
               ", veterinarian=" + (veterinarian != null ? veterinarian.getName() : "null") +
               '}';
    }
}