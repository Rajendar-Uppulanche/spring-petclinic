package com.example.petclinic.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "visit_date")
    private LocalDate visitDate;

    private String description;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @Enumerated(EnumType.STRING)
    @Column(name = "visit_type", nullable = false)
    private VisitType visitType;

    @Embedded
    private PreventiveCareDetails preventiveCareDetails;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}