package org.springframework.samples.petclinic.vet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.samples.petclinic.model.Person;

/**
 * Simple JavaBean domain object representing a veterinarian.
 */
@Entity
@Table(name = "veterinarians")
public class Veterinarian extends Person {

    @Column(name = "contact_information")
    @NotEmpty
    private String contactInformation;

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }
}
