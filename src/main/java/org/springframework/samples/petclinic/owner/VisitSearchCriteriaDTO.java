package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO for encapsulating search and filter parameters for visits.
 *
 * @author Synapse Builder
 */
public class VisitSearchCriteriaDTO {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String petName;
    private String ownerLastName;
    private String veterinarianFirstName;
    private String veterinarianLastName;
    private VisitStatus status;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getVeterinarianFirstName() {
        return veterinarianFirstName;
    }

    public void setVeterinarianFirstName(String veterinarianFirstName) {
        this.veterinarianFirstName = veterinarianFirstName;
    }

    public String getVeterinarianLastName() {
        return veterinarianLastName;
    }

    public void setVeterinarianLastName(String veterinarianLastName) {
        this.veterinarianLastName = veterinarianLastName;
    }

    public VisitStatus getStatus() {
        return status;
    }

    public void setStatus(VisitStatus status) {
        this.status = status;
    }
}
