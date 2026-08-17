package com.example.petclinic.service.dto;

import java.time.LocalDate;

public class PetDTO {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String typeName;
    private Long ownerId;
    private Long visitCount; // New field

    public PetDTO() {
    }

    public PetDTO(Long id, String name, LocalDate birthDate, String typeName, Long ownerId, Long visitCount) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.typeName = typeName;
        this.ownerId = ownerId;
        this.visitCount = visitCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Long visitCount) {
        this.visitCount = visitCount;
    }
}