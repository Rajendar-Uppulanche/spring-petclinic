package org.springframework.samples.petclinic.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

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

	// Default FetchType.LAZY is preferred for performance.
	// The visits will be eagerly fetched via JOIN FETCH in the repository query.
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pet")
	private Set<Visit> visits;

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

	public Set<Visit> getVisits() {
		return Collections.unmodifiableSet(getVisitsInternal());
	}

	public void addVisit(Visit visit) {
		getVisitsInternal().add(visit);
		visit.setPet(this);
	}

	/**
	 * Returns the total number of visits for this pet.
	 * This method assumes the 'visits' collection is already loaded
	 * by an eager fetch strategy (e.g., JOIN FETCH in the repository).
	 * @return the number of visits
	 */
	public int getVisitCount() {
		return getVisitsInternal().size();
	}

	@Override
	public String toString() {
		return "Pet [id=" + getId() + ", name=" + getName() + ", birthDate=" + birthDate + ", type=" + type + ", owner=" + owner + "]";
	}
}
