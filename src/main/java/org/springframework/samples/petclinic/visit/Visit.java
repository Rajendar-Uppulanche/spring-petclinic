package org.springframework.samples.petclinic.visit;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.springframework.samples.petclinic.model.BaseEntity;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.vet.Vet;

/**
 * Simple JavaBean domain object representing a visit.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

	@Column(name = "visit_date")
	private LocalDate date;

	@NotEmpty
	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	private Pet pet;

	@ManyToOne
	@JoinColumn(name = "vet_id")
	private Vet vet;

	/**
	 * Creates a new instance of Visit for the current date
	 */
	public Visit() {
		this.date = LocalDate.now();
	}

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Pet getPet() {
		return this.pet;
	}

	public void setPet(Pet pet) {
		this.pet = pet;
	}

	public Vet getVet() {
		return vet;
	}

	public void setVet(Vet vet) {
		this.vet = vet;
	}

}
