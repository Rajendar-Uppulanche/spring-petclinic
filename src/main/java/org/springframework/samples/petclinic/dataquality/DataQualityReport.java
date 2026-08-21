package org.springframework.samples.petclinic.dataquality;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO representing the overall data quality report.
 */
public class DataQualityReport {

	private LocalDateTime generationTimestamp;

	private long totalOwners;

	private long ownersWithIncompleteAddress;

	private long ownersWithIncompleteCity;

	private long ownersWithIncompleteTelephone;

	private long ownersWithInvalidTelephone;

	private long totalPets;

	private long petsWithIncompleteBirthDate;

	private long petsWithIncompleteType;

	private long petsWithoutVisits;

	private List<OwnerQualityMetrics> detailedOwnerMetrics = new ArrayList<>();

	// Getters and Setters

	public LocalDateTime getGenerationTimestamp() {
		return generationTimestamp;
	}

	public void setGenerationTimestamp(LocalDateTime generationTimestamp) {
		this.generationTimestamp = generationTimestamp;
	}

	public long getTotalOwners() {
		return totalOwners;
	}

	public void setTotalOwners(long totalOwners) {
		this.totalOwners = totalOwners;
	}

	public long getOwnersWithIncompleteAddress() {
		return ownersWithIncompleteAddress;
	}

	public void setOwnersWithIncompleteAddress(long ownersWithIncompleteAddress) {
		this.ownersWithIncompleteAddress = ownersWithIncompleteAddress;
	}

	public long getOwnersWithIncompleteCity() {
		return ownersWithIncompleteCity;
	}

	public void setOwnersWithIncompleteCity(long ownersWithIncompleteCity) {
		this.ownersWithIncompleteCity = ownersWithIncompleteCity;
	}

	public long getOwnersWithIncompleteTelephone() {
		return ownersWithIncompleteTelephone;
	}

	public void setOwnersWithIncompleteTelephone(long ownersWithIncompleteTelephone) {
		this.ownersWithIncompleteTelephone = ownersWithIncompleteTelephone;
	}

	public long getOwnersWithInvalidTelephone() {
		return ownersWithInvalidTelephone;
	}

	public void setOwnersWithInvalidTelephone(long ownersWithInvalidTelephone) {
		this.ownersWithInvalidTelephone = ownersWithInvalidTelephone;
	}

	public long getTotalPets() {
		return totalPets;
	}

	public void setTotalPets(long totalPets) {
		this.totalPets = totalPets;
	}

	public long getPetsWithIncompleteBirthDate() {
		return petsWithIncompleteBirthDate;
	}

	public void setPetsWithIncompleteBirthDate(long petsWithIncompleteBirthDate) {
		this.petsWithIncompleteBirthDate = petsWithIncompleteBirthDate;
	}

	public long getPetsWithIncompleteType() {
		return petsWithIncompleteType;
	}

	public void setPetsWithIncompleteType(long petsWithIncompleteType) {
		this.petsWithIncompleteType = petsWithIncompleteType;
	}

	public long getPetsWithoutVisits() {
		return petsWithoutVisits;
	}

	public void setPetsWithoutVisits(long petsWithoutVisits) {
		this.petsWithoutVisits = petsWithoutVisits;
	}

	public List<OwnerQualityMetrics> getDetailedOwnerMetrics() {
		return detailedOwnerMetrics;
	}

	public void setDetailedOwnerMetrics(List<OwnerQualityMetrics> detailedOwnerMetrics) {
		this.detailedOwnerMetrics = detailedOwnerMetrics;
	}

}
