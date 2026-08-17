package com.example.petclinic.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "veterinarians")
public class Veterinarian extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "contact_information")
    private String contactInformation;

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
        if (!super.equals(o)) return false;
        Veterinarian that = (Veterinarian) o;
        return Objects.equals(name, that.name) &&
               Objects.equals(contactInformation, that.contactInformation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, contactInformation);
    }

    @Override
    public String toString() {
        return "Veterinarian{" +
               "id=" + getId() +
               ", name='" + name + '\'' +
               ", contactInformation='" + contactInformation + '\'' +
               '}';
    }
}