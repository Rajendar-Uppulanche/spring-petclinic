package com.example.petclinic.dto;

import com.example.petclinic.model.VisitType;

import java.time.LocalDate;

public class VisitResponseDTO {
    private Integer id;
    private Integer petId;
    private String petName;
    private LocalDate visitDate;
    private String description;
    private VisitType visitType;
    private PreventiveCareDetailsDTO preventiveCareDetails;
    private OwnerContactInfoDTO ownerContactInfo;

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

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
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

    public OwnerContactInfoDTO getOwnerContactInfo() {
        return ownerContactInfo;
    }

    public void setOwnerContactInfo(OwnerContactInfoDTO ownerContactInfo) {
        this.ownerContactInfo = ownerContactInfo;
    }
}