package org.springframework.samples.petclinic.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Maciej Walkowiak
 */
@Entity
@Table(name = "pets")
public class Pet extends NamedEntity {

    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private PetType type;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pet", fetch = FetchType.EAGER)
    private Set<Visit> visits;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pet", fetch = FetchType.EAGER)
    private Set<PreventiveCare> preventiveCares;

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public PetType getType() {
        return this.type;
    }

    public void setType(PetType type) {
        this.type = type;
    }

    public Owner getOwner() {
        return this.owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    protected Set<Visit> getVisitsInternal() {
        if (this.visits == null) {
            this.visits = new HashSet<>();
        }
        return this.visits;
    }

    protected void setVisitsInternal(Set<Visit> visits) {
        this.visits = visits;
    }

    public List<Visit> getVisits() {
        List<Visit> sortedVisits = new ArrayList<>(getVisitsInternal());
        sortedVisits.sort((visit1, visit2) -> visit1.getVisitDate().compareTo(visit2.getVisitDate()));
        return Collections.unmodifiableList(sortedVisits);
    }

    public void addVisit(Visit visit) {
        getVisitsInternal().add(visit);
        visit.setPet(this);
    }

    protected Set<PreventiveCare> getPreventiveCaresInternal() {
        if (this.preventiveCares == null) {
            this.preventiveCares = new HashSet<>();
        }
        return this.preventiveCares;
    }

    protected void setPreventiveCaresInternal(Set<PreventiveCare> preventiveCares) {
        this.preventiveCares = preventiveCares;
    }

    public Set<PreventiveCare> getPreventiveCares() {
        return getPreventiveCaresInternal();
    }

    public void addPreventiveCare(PreventiveCare preventiveCare) {
        getPreventiveCaresInternal().add(preventiveCare);
        preventiveCare.setPet(this);
    }

    public int getAge() {
        if (getBirthDate() == null) {
            return 0;
        }
        return Period.between(getBirthDate(), LocalDate.now()).getYears();
    }

}