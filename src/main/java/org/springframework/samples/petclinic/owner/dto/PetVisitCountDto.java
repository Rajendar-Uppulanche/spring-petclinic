package org.springframework.samples.petclinic.owner.dto;

import java.time.LocalDate;

public class PetVisitCountDto {
    private Integer id;
    private String name;
    private LocalDate birthDate;
    private String typeName;
    private long visitCount;

    // Constructor for JPA projection
    public PetVisitCountDto(Integer id, String name, LocalDate birthDate, String typeName, long visitCount) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.typeName = typeName;
        this.visitCount = visitCount;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getTypeName() {
        return typeName;
    }

    public long getVisitCount() {
        return visitCount;
    }

    // Setters (optional, but good for DTOs if they are mutable or used in other contexts)
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setVisitCount(long visitCount) {
        this.visitCount = visitCount;
    }
}