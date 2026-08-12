/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.springframework.beans.support.MutablePropertyValues;
import org.springframework.core.style.StylerUtils;
import org.springframework.util.ClassUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	private Owner owner;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "type_id")
	private PetType type;

	@OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Visit> visits = new HashSet<>();

	// Transient field to hold the calculated age
	@Transient
	private String age;

	public LocalDate getBirthDate() {
		return this.birthDate;
	}

	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}

	public boolean isNew() {
		return this.id == null;
	}

	public Owner getOwner() {
		return this.owner;
	}

	protected void setOwner(Owner owner) {
		this.owner = owner;
	}

	public void addVisit(Visit visit) {
		getVisitsInternal().add(visit);
		visit.setPet(this);
	}

	public List<Visit> getVisits() {
		List<Visit> sortedVisits = new ArrayList<>(getVisitsInternal());
		// Collections.sort(sortedVisits, new Visit.VisitComparator()); // Assuming Visit.VisitComparator exists
		return sortedVisits;
	}

	protected Set<Visit> getVisitsInternal() {
		return this.visits;
	}

	public PetType getType() {
		return this.type;
	}

	public void setType(PetType type) {
		this.type = type;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return StylerUtils.style(this);
	}

	@Override
	public MutablePropertyValues getPropertyValues() {
		MutablePropertyValues propertyValues = super.getPropertyValues();
		propertyValues.addPropertyValue("birthDate", this.birthDate);
		propertyValues.addPropertyValue("type", this.type);
		return propertyValues;
	}

	@Override
	public String toStringOf() {
		return "birthDate=" + StylerUtils.shortStyle(this.birthDate) + ", type=" + this.type;
	}

	@Override
	public String getObjectClassName() {
		return ClassUtils.getShortName(getClass());
	}

}
