package org.springframework.samples.petclinic.owner;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.model.NamedEntity;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple JavaBean domain object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
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

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "pet", fetch = FetchType.LAZY)
	@OrderBy("visit_date DESC")
	private Set<Visit> visits = new LinkedHashSet<>();

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
			this.visits = new LinkedHashSet<>();
		}
		return this.visits;
	}

	protected void setVisitsInternal(Set<Visit> visits) {
		this.visits = visits;
	}

	public Set<Visit> getVisits() {
		return getVisitsInternal();
	}

	public void addVisit(Visit visit) {
		getVisitsInternal().add(visit);
		visit.setPet(this);
	}

	public int getVisitsCount() {
		return getVisitsInternal().size();
	}

}
