package com.example.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pets")
public class Pet extends BaseEntity {

    @Column(name = "name")
    @NotEmpty
    private String name;

    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy/MM/dd")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private PetType type;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pet", fetch = javax.persistence.FetchType.EAGER)
    private Set<Visit> visits;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pet", orphanRemoval = true)
    private List<Vaccination> vaccinations;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
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
        sortedVisits.sort((visit1, visit2) -> visit1.getDate().compareTo(visit2.getDate()));
        return Collections.unmodifiableList(sortedVisits);
    }

    public void addVisit(Visit visit) {
        getVisitsInternal().add(visit);
        visit.setPet(this);
    }

    protected List<Vaccination> getVaccinationsInternal() {
        if (this.vaccinations == null) {
            this.vaccinations = new ArrayList<>();
        }
        return this.vaccinations;
    }

    protected void setVaccinationsInternal(List<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }

    public List<Vaccination> getVaccinations() {
        return Collections.unmodifiableList(getVaccinationsInternal());
    }

    public void addVaccination(Vaccination vaccination) {
        getVaccinationsInternal().add(vaccination);
        vaccination.setPet(this);
    }

    public boolean isNew() {
        return this.id == null;
    }

}
