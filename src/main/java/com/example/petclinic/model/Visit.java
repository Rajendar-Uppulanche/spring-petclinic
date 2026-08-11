package org.springframework.samples.petclinic.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Simple business object representing a visit.
 *
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Entity
@Table(name = "visits")
public class Visit extends BaseEntity {

	@Column(name = "visit_date")
	private LocalDate date;

	@Column(name = "description")
	private String description;

	@ManyToOne
	@JoinColumn(name = "pet_id")
	private Pet pet;

	@ManyToOne
	@JoinColumn(name = "visit_type_id")
	private VisitType visitType;

	@ManyToOne
	@JoinColumn(name = "veterinarian_id")
	private Veterinarian veterinarian;

	/**
	 * Creates a new instance of Visit for the current year.
	 */
	public Visit() {
		this.date = LocalDate.now();
	}

	/**
	 * Sets the visit date.
	 * @param date the visit date
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * Returns the visit date.
	 * @return the visit date
	 */
	public LocalDate getDate() {
		return this.date;
	}

	/**
	 * Sets the description of the visit.
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the description of the visit.
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the pet for this visit.
	 * @param pet the pet
	 */
	protected void setPet(Pet pet) {
		this.pet = pet;
	}

	/**
	 * Returns the pet for this visit.
	 * @return the pet
	 */
	public Pet getPet() {
		return this.pet;
	}

	public VisitType getVisitType() {
		return visitType;
	}

	public void setVisitType(VisitType visitType) {
		this.visitType = visitType;
	}

	public Veterinarian getVeterinarian() {
		return veterinarian;
	}

	public void setVeterinarian(Veterinarian veterinarian) {
		this.veterinarian = veterinarian;
	}

}
