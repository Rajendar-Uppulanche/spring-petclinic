package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Simple JavaBean domain object representing a veterinarian.
 */
@Entity
@Table(name = "veterinarians")
public class Veterinarian extends Person {

    @Column(name = "contact_information")
    private String contactInformation;

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }
}
