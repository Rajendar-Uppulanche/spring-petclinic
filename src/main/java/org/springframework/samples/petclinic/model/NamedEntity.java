package org.springframework.samples.petclinic.model;

import jakarta.persistence.MappedSuperclass;

/**
 * Simple JavaBean domain object adds a name property to a {@link BaseEntity}. Used by Pet and PetType. 
 * @author Ken Krebs
 * @author Juergen Hoeller
 */
@MappedSuperclass
public class NamedEntity extends BaseEntity {
	
	private String name;

	public String getName() {
		return this.name != null ? this.name : "";
	}

	public void setName(String name) {
		this.name = name;
	}

}
