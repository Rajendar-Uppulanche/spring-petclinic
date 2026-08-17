package com.example.petclinic.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet; // Assuming a Pet entity exists

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id") // This is the new column
    private Veterinarian veterinarian;

    public Visit() {
    }

    public Visit(LocalDate visitDate, String description, Pet pet, Veterinarian veterinarian) {
        this.visitDate = visitDate;
        this.description = description;
        this.pet = pet;
        this.veterinarian = veterinarian;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        Visit visit = (Visit) o;
        return Objects.equals(id, visit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Visit{" +
               "id=" + id +
               ", visitDate=" + visitDate +
               ", description='" + description + '\'' +
               ", pet=" + (pet != null ? pet.getId() : "null") +
               ", veterinarian=" + (veterinarian != null ? veterinarian.getId() : "null") +
               '}';
    }
}