package com.example.petclinic.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "veterinarians")
public class Veterinarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact_information")
    private String contactInformation;

    public Veterinarian() {
    }

    public Veterinarian(String name, String contactInformation) {
        this.name = name;
        this.contactInformation = contactInformation;
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

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veterinarian that = (Veterinarian) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Veterinarian{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", contactInformation='" + contactInformation + '\'' +
               '}';
    }
}