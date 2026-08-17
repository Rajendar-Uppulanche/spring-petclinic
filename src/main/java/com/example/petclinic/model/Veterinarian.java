package com.example.petclinic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;

@Entity
@Table(name = "veterinarians")
@Data
@EqualsAndHashCode(callSuper = true)
public class Veterinarian extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "contact_information")
    private String contactInformation;
}
