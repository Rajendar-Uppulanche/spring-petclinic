package org.springframework.samples.petclinic.owner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

    @Column(name = "visit_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'SCHEDULED'")
    private VisitStatus status = VisitStatus.SCHEDULED; // Default value as per FR-025

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

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

    public VisitStatus getStatus() {
        return status;
    }

    public void setStatus(VisitStatus status) {
        this.status = status;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public boolean canTransitionTo(VisitStatus newStatus) {
        if (this.status == VisitStatus.COMPLETED || this.status == VisitStatus.CANCELLED) {
            // BR-004, BR-005: Completed and Cancelled visits cannot be changed to any other status
            return false;
        }

        switch (this.status) {
            case SCHEDULED:
                // BR-006: 'Scheduled' -> 'In Progress' -> 'Completed'; and 'Scheduled' -> 'Cancelled'.
                return newStatus == VisitStatus.IN_PROGRESS || newStatus == VisitStatus.CANCELLED;
            case IN_PROGRESS:
                // BR-006: 'In Progress' -> 'Completed'
                return newStatus == VisitStatus.COMPLETED;
            default:
                return false; // Should not happen for COMPLETED/CANCELLED due to early exit
        }
    }

    public void transitionTo(VisitStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new IllegalArgumentException(
                    "Invalid status transition from " + this.status + " to " + newStatus);
        }
        this.status = newStatus;
    }
}
