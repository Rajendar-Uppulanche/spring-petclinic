package org.springframework.samples.petclinic.vet;

import jakarta.persistence.*;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.samples.petclinic.user.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vets")
public class Vet extends Person {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "vet_specialties", joinColumns = @JoinColumn(name = "vet_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id"))
    private Set<Specialty> specialties = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Set<Specialty> getSpecialties() {
        return this.specialties;
    }

    public void addSpecialty(Specialty specialty) {
        this.specialties.add(specialty);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}