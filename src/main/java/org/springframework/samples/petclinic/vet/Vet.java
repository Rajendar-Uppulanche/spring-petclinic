package org.springframework.samples.petclinic.vet;

import org.springframework.samples.petclinic.model.Person;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "vets")
public class Vet extends Person {
    // No specific fields for Vet beyond Person's first and last name for this context
}
