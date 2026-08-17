package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Simple JavaBean domain object representing a veterinarian for appointment management.
 * This entity is distinct from the existing 'Vet' entity, as per the implementation plan.
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

    /**
     * Provides a combined name for the veterinarian, using first and last names.
     * @return The full name of the veterinarian.
     */
    public String getName() {
        return getFirstName() + " " + getLastName();
    }
}
