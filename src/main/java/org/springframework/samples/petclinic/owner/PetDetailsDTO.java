package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

/**
 * A Data Transfer Object (DTO) for Pet details, including a formatted visit count string.
 */
public class PetDetailsDTO {
    private Integer id;
    private String name;
    private LocalDate birthDate;
    private PetType type;
    private String visitCountString; // New field for formatted visit count

    public PetDetailsDTO(Integer id, String name, LocalDate birthDate, PetType type, String visitCountString) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.type = type;
        this.visitCountString = visitCountString;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public PetType getType() { return type; }
    public void setType(PetType type) { this.type = type; }
    public String getVisitCountString() { return visitCountString; }
    public void setVisitCountString(String visitCountString) { this.visitCountString = visitCountString; }
}
