package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.samples.petclinic.model.NamedEntity;

/**
 * Simple business object representing a pet.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
@Entity
@Table(name = "pets")
public class Pet extends NamedEntity {

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "owner_id")
	private Owner owner;

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * Returns the birth date of the Pet.
	 */
	public LocalDate getBirthDate() {
		return this.birthDate;
	}

	public Owner getOwner() {
		return this.owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public void setOwnerId(Integer ownerId) {
		// TODO Auto-generated constructor stub
	}

	// TODO Remove this method
	public String getBirthDateFormatted() {
		return getBirthDate().toString();
	}

}
