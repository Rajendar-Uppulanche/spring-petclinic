package org.springframework.samples.petclinic.owner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.samples.petclinic.model.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.xml.bind.annotation.XmlElement;

/**
 * Simple JavaBean domain object representing an owner.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
@Entity
@Table(name = "owners", indexes = { @Index(name = "idx_owner_last_name", columnList = "last_name") }) // Added index here
public class Owner extends Person {

	@Column(name = "address")
	@NotEmpty
	private String address;

	@Column(name = "city")
	@NotEmpty
	private String city;

	@Column(name = "telephone")
	@NotEmpty
	@Digits(fraction = 0, integer = 10)
	private String telephone;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
	private Set<Pet> pets;

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@XmlElement
	public List<Pet> getPets() {
		List<Pet> sortedPets = new ArrayList<>(getPetsInternal());
		sortedPets.sort((pet1, pet2) -> pet1.getName().compareToIgnoreCase(pet2.getName()));
		return Collections.unmodifiableList(sortedPets);
	}

	protected Set<Pet> getPetsInternal() {
		if (this.pets == null) {
			this.pets = new HashSet<>();
		}
		return this.pets;
	}

	protected void setPetsInternal(Set<Pet> pets) {
		this.pets = pets;
	}

	public void addPet(Pet pet) {
		if (pet.isNew()) {
			getPetsInternal().add(pet);
		}
		pet.setOwner(this);
	}

	/**
	 * Return the Pet with the given name, or null if none found for this Owner.
	 * @param name to test
	 * @return true if pet name is already in use
	 */
	public Pet getPet(String name) {
		return getPet(name, false);
	}

	/**
	 * Return the Pet with the given name, or null if none found for this Owner.
	 * @param name to test
	 * @return true if pet name is already in use
	 */
	public Pet getPet(String name, boolean ignoreNew) {
		name = name.toLowerCase();
		for (Pet pet : getPetsInternal()) {
			if (!ignoreNew || !pet.isNew()) {
				String compName = pet.getName();
				compName = compName.toLowerCase();
				if (compName.equals(name)) {
					return pet;
				}
			}
		}
		return null;
	}

	public Pet getPet(Integer id) {
		for (Pet pet : getPetsInternal()) {
			if (pet.getId().equals(id)) {
				return pet;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Owner [id=" + getId() + ", new=" + isNew() + ", lastName=" + getLastName() + ", firstName="
				+ getFirstName() + ", address=" + address + ", city=" + city + ", telephone=" + telephone + "]";
	}

}
