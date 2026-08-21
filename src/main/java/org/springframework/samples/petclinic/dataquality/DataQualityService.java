package org.springframework.samples.petclinic.dataquality;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service responsible for calculating and aggregating data quality metrics
 * for owner contact details and pet records.
 */
@Service
public class DataQualityService {

	private static final Pattern TELEPHONE_PATTERN = Pattern.compile("^\\d{10}$|^\\d{3}-\\d{3}-\\d{4}$");

	private final OwnerRepository ownerRepository;

	public DataQualityService(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	/**
	 * Generates a comprehensive data quality report by analyzing owner and pet data.
	 * This method fetches all owners with their associated pets and visits to perform
	 * various completeness and validity checks.
	 * @return A {@link DataQualityReport} containing aggregated and detailed metrics.
	 */
	@Transactional(readOnly = true)
	public DataQualityReport generateDataQualityReport() {
		DataQualityReport report = new DataQualityReport();
		report.setGenerationTimestamp(LocalDateTime.now());

		List<Owner> owners = ownerRepository.findAllWithPetsAndVisits();
		report.setTotalOwners(owners.size());

		long totalPets = 0;
		for (Owner owner : owners) {
			OwnerQualityMetrics ownerMetrics = calculateOwnerQualityMetrics(owner);
			report.getDetailedOwnerMetrics().add(ownerMetrics);

			if (!ownerMetrics.isAddressComplete()) {
				report.setOwnersWithIncompleteAddress(report.getOwnersWithIncompleteAddress() + 1);
			}
			if (!ownerMetrics.isCityComplete()) {
				report.setOwnersWithIncompleteCity(report.getOwnersWithIncompleteCity() + 1);
			}
			if (!ownerMetrics.isTelephoneComplete()) {
				report.setOwnersWithIncompleteTelephone(report.getOwnersWithIncompleteTelephone() + 1);
			}
			if (!ownerMetrics.isTelephoneValid()) {
				report.setOwnersWithInvalidTelephone(report.getOwnersWithInvalidTelephone() + 1);
			}

			totalPets += owner.getPets().size();
			for (Pet pet : owner.getPets()) {
				PetQualityMetrics petMetrics = calculatePetQualityMetrics(pet);
				ownerMetrics.getPetMetrics().add(petMetrics);

				if (!petMetrics.isBirthDateComplete()) {
					report.setPetsWithIncompleteBirthDate(report.getPetsWithIncompleteBirthDate() + 1);
				}
				if (!petMetrics.isTypeComplete()) {
					report.setPetsWithIncompleteType(report.getPetsWithIncompleteType() + 1);
				}
				if (!petMetrics.isHasVisits()) {
					report.setPetsWithoutVisits(report.getPetsWithoutVisits() + 1);
				}
			}
		}
		report.setTotalPets(totalPets);

		return report;
	}

	/**
	 * Calculates data quality metrics for a single owner.
	 * @param owner The owner to analyze.
	 * @return An {@link OwnerQualityMetrics} object with the calculated metrics.
	 */
	private OwnerQualityMetrics calculateOwnerQualityMetrics(Owner owner) {
		OwnerQualityMetrics metrics = new OwnerQualityMetrics();
		metrics.setOwnerId(owner.getId());
		metrics.setOwnerName(owner.getFirstName() + " " + owner.getLastName());

		// Completeness checks
		metrics.setAddressComplete(StringUtils.hasText(owner.getAddress()));
		metrics.setCityComplete(StringUtils.hasText(owner.getCity()));
		metrics.setTelephoneComplete(StringUtils.hasText(owner.getTelephone()));

		// Validity checks (BR-027)
		metrics.setTelephoneValid(metrics.isTelephoneComplete() && TELEPHONE_PATTERN.matcher(owner.getTelephone()).matches());

		return metrics;
	}

	/**
	 * Calculates data quality metrics for a single pet.
	 * @param pet The pet to analyze.
	 * @return A {@link PetQualityMetrics} object with the calculated metrics.
	 */
	private PetQualityMetrics calculatePetQualityMetrics(Pet pet) {
		PetQualityMetrics metrics = new PetQualityMetrics();
		metrics.setPetId(pet.getId());
		metrics.setPetName(pet.getName());

		// Completeness checks
		metrics.setBirthDateComplete(pet.getBirthDate() != null);
		metrics.setTypeComplete(pet.getType() != null);

		// Other checks
		metrics.setHasVisits(!pet.getVisits().isEmpty()); // Using the new helper method

		return metrics;
	}

}
