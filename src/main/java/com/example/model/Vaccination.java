package com.example.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "vaccinations")
public class Vaccination extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "vaccine_type_id")
    @NotNull
    private VaccineType vaccineType;

    @Column(name = "date_administered")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    @NotNull
    private LocalDate dateAdministered;

    @Column(name = "administering_vet")
    @NotEmpty
    private String administeringVet;

    @Column(name = "batch_number")
    @NotEmpty
    private String batchNumber;

    @Column(name = "next_due_date")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate nextDueDate;

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public VaccineType getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(VaccineType vaccineType) {
        this.vaccineType = vaccineType;
    }

    public LocalDate getDateAdministered() {
        return dateAdministered;
    }

    public void setDateAdministered(LocalDate dateAdministered) {
        this.dateAdministered = dateAdministered;
    }

    public String getAdministeringVet() {
        return administeringVet;
    }

    public void setAdministeringVet(String administeringVet) {
        this.administeringVet = administeringVet;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}
