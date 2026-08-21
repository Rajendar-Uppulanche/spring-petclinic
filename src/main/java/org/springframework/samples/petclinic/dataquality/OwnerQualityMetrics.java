package org.springframework.samples.petclinic.dataquality;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for reporting data quality metrics related to an individual owner.
 */
public class OwnerQualityMetrics {

	private Integer ownerId;

	private String ownerName;

	private boolean addressComplete;

	private boolean cityComplete;

	private boolean telephoneComplete;

	private boolean telephoneValid;

	private List<PetQualityMetrics> petMetrics = new ArrayList<>();

	// Getters and Setters

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public boolean isAddressComplete() {
		return addressComplete;
	}

	public void setAddressComplete(boolean addressComplete) {
		this.addressComplete = addressComplete;
	}

	public boolean isCityComplete() {
		return cityComplete;
	}

	public void setCityComplete(boolean cityComplete) {
		this.cityComplete = cityComplete;
	}

	public boolean isTelephoneComplete() {
		return telephoneComplete;
	}

	public void setTelephoneComplete(boolean telephoneComplete) {
		this.telephoneComplete = telephoneComplete;
	}

	public boolean isTelephoneValid() {
		return telephoneValid;
	}

	public void setTelephoneValid(boolean telephoneValid) {
		this.telephoneValid = telephoneValid;
	}

	public List<PetQualityMetrics> getPetMetrics() {
		return petMetrics;
	}

	public void setPetMetrics(List<PetQualityMetrics> petMetrics) {
		this.petMetrics = petMetrics;
	}

}
