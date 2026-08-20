package org.springframework.samples.petclinic.owner;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.NamedEntity;
import org.springframework.samples.petclinic.vaccination.Vaccination; // Import new class

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "pets")
public class Pet extends NamedEntity {

    @Column(name = "birth_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private PetType type;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pet", fetch = FetchType.EAGER)
    private Set<Visit> visits = new LinkedHashSet<>();

    // New field for vaccinations
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pet", fetch = FetchType.EAGER)
    private Set<Vaccination> vaccinations = new LinkedHashSet<>();

    // Transient field to indicate if pet has overdue vaccinations
    private transient boolean overdueForVaccination;

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

    public Set<Visit> getVisitsInternal() {
        return this.visits;
    }

    public void setVisitsInternal(Set<Visit> visits) {
        this.visits = visits;
    }

    public List<Visit> getVisits() {
        List<Visit> sortedVisits = new ArrayList<>(getVisitsInternal());
        Comparator<Visit> descSort = Comparator.comparing(Visit::getDate);
        Collections.sort(sortedVisits, descSort.reversed());
        return Collections.unmodifiableList(sortedVisits);
    }

    public void addVisit(Visit visit) {
        if (visit.isNew()) {
            getVisitsInternal().add(visit);
        }
        visit.setPet(this);
    }

    // New getters/setters for vaccinations
    public Set<Vaccination> getVaccinationsInternal() {
        return this.vaccinations;
    }

    public void setVaccinationsInternal(Set<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }

    public List<Vaccination> getVaccinations() {
        List<Vaccination> sortedVaccinations = new ArrayList<>(getVaccinationsInternal());
        Comparator<Vaccination> descSort = Comparator.comparing(Vaccination::getDateAdministered);
        Collections.sort(sortedVaccinations, descSort.reversed());
        return Collections.unmodifiableList(sortedVaccinations);
    }

    public void addVaccination(Vaccination vaccination) {
        if (vaccination.isNew()) {
            getVaccinationsInternal().add(vaccination);
        }
        vaccination.setPet(this);
    }

    public boolean isOverdueForVaccination() {
        return overdueForVaccination;
    }

    public void setOverdueForVaccination(boolean overdueForVaccination) {
        this.overdueForVaccination = overdueForVaccination;
    }
}
