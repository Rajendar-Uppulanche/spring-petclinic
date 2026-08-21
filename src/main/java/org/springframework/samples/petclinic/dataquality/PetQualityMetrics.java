package org.springframework.samples.petclinic.dataquality;

/**
 * DTO for reporting data quality metrics related to an individual pet.
 */
public class PetQualityMetrics {

	private Integer petId;

	private String petName;

	private boolean birthDateComplete;

	private boolean typeComplete;

	private boolean hasVisits;

	// Getters and Setters

	public Integer getPetId() {
		return petId;
	}

	public void setPetId(Integer petId) {
		this.petId = petId;
	}

	public String getPetName() {
		return petName;
	}

	public void setPetName(String petName) {
		this.petName = petName;
	}

	public boolean isBirthDateComplete() {
		return birthDateComplete;
	}

	public void setBirthDateComplete(boolean birthDateComplete) {
		this.birthDateComplete = birthDateComplete;
	}

	public boolean isTypeComplete() {
		return typeComplete;
	}

	public void setTypeComplete(boolean typeComplete) {
		this.typeComplete = typeComplete;
	}

	public boolean isHasVisits() {
		return hasVisits;
	}

	public void setHasVisits(boolean hasVisits) {
		this.hasVisits = hasVisits;
	}

}
