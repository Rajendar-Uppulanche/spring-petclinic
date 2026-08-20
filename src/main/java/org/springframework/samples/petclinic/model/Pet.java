package org.springframework.samples.petclinic.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import org.springframework.format.annotation.DateTimeFormat;

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
	@NotNull
	@PastOrPresent(message = "{pet.birthDate.future}")
	private LocalDate birthDate;
	
	@ManyToOne
	@JoinColumn(name = "type_id")
	@NotNull
	private PetType type;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Owner owner;

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public LocalDate getBirthDate() {
		return this.birthDate;
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

	protected void setOwner(Owner owner) {
		this.owner = owner;
	}

	public boolean isNew() {
		return this.id == null;
	}

}
