package com.example.petclinic.dto;

import com.example.petclinic.model.VisitType;

import java.time.LocalDate;

public class VisitRequestDTO {
    private Integer id;
    private Integer petId;
    private LocalDate visitDate;
    private String description;
    private VisitType visitType;
    private PreventiveCareDetailsDTO preventiveCareDetails;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPetId() {
        return petId;
    }

    public void setPetId(Integer petId) {
        this.petId = petId;
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

    public VisitType getVisitType() {
        return visitType;
    }

    public void setVisitType(VisitType visitType) {
        this.visitType = visitType;
    }

    public PreventiveCareDetailsDTO getPreventiveCareDetails() {
        return preventiveCareDetails;
    }

    public void setPreventiveCareDetails(PreventiveCareDetailsDTO preventiveCareDetails) {
        this.preventiveCareDetails = preventiveCareDetails;
    }
}