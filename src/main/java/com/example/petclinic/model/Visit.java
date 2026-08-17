package com.example.petclinic.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "visits")
@Data
@EqualsAndHashCode(callSuper = true)
public class Visit extends BaseEntity {

    @Column(name = "visit_date")
    private LocalDate visitDate;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "veterinarian_id")
    private Veterinarian veterinarian;
}
