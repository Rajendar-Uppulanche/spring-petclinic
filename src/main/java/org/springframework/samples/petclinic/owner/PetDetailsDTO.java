package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.Objects;

public class PetDetailsDTO {
    private Integer id;
    private String name;
    private LocalDate birthDate;
    private String typeName;
    private Integer visitCount;

    public PetDetailsDTO() {
    }

    public PetDetailsDTO(Pet pet, Integer visitCount) {
        this.id = pet.getId();
        this.name = pet.getName();
        this.birthDate = pet.getBirthDate();
        this.typeName = pet.getType() != null ? pet.getType().getName() : null;
        this.visitCount = visitCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetDetailsDTO that = (PetDetailsDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(birthDate, that.birthDate) && Objects.equals(typeName, that.typeName) && Objects.equals(visitCount, that.visitCount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, birthDate, typeName, visitCount);
    }
}
