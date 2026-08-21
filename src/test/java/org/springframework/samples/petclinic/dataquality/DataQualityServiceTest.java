package org.springframework.samples.petclinic.dataquality;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.Visit;

@ExtendWith(MockitoExtension.class)
class DataQualityServiceTest {

	@Mock
	private OwnerRepository ownerRepository;

	@InjectMocks
	private DataQualityService dataQualityService;

	private Owner owner1;

	private Owner owner2;

	private Pet pet1;

	private Pet pet2;

	private PetType cat;

	private PetType dog;

	@BeforeEach
	void setUp() {
		cat = new PetType();
		cat.setId(1);
		cat.setName("cat");

		dog = new PetType();
		dog.setId(2);
		dog.setName("dog");

		owner1 = new Owner();
		owner1.setId(1);
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");
		owner1.setAddress("110 W. Liberty St.");
		owner1.setCity("Madison");
		owner1.setTelephone("6085551023");

		pet1 = new Pet();
		pet1.setId(1);
		pet1.setName("Leo");
		pet1.setBirthDate(LocalDate.of(2000, 9, 7));
		pet1.setType(cat);
		owner1.addPet(pet1);

		Visit visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.now());
		visit1.setDescription("Routine checkup");
		pet1.addVisit(visit1);

		owner2 = new Owner();
		owner2.setId(2);
		owner2.setFirstName("Betty");
		owner2.setLastName("Davis");
		owner2.setAddress(null); // Incomplete address
		owner2.setCity("Wisconsin");
		owner2.setTelephone("invalid-phone"); // Invalid telephone

		pet2 = new Pet();
		pet2.setId(2);
		pet2.setName("Max");
		pet2.setBirthDate(null); // Incomplete birth date
		pet2.setType(null); // Incomplete type
		owner2.addPet(pet2);
		// No visits for pet2
	}

	@Test
	void shouldGenerateReportWithCorrectAggregates() {
		// Given
		when(ownerRepository.findAllWithPetsAndVisits()).thenReturn(Arrays.asList(owner1, owner2));

		// When
		DataQualityReport report = dataQualityService.generateDataQualityReport();

		// Then
		assertThat(report).isNotNull();
		assertThat(report.getGenerationTimestamp()).isNotNull();
		assertThat(report.getTotalOwners()).isEqualTo(2);
		assertThat(report.getTotalPets()).isEqualTo(2);

		// Owner metrics
		assertThat(report.getOwnersWithIncompleteAddress()).isEqualTo(1);
		assertThat(report.getOwnersWithIncompleteCity()).isEqualTo(0);
		assertThat(report.getOwnersWithIncompleteTelephone()).isEqualTo(0); // telephone is not null, but invalid
		assertThat(report.getOwnersWithInvalidTelephone()).isEqualTo(1);

		// Pet metrics
		assertThat(report.getPetsWithIncompleteBirthDate()).isEqualTo(1);
		assertThat(report.getPetsWithIncompleteType()).isEqualTo(1);
		assertThat(report.getPetsWithoutVisits()).isEqualTo(1);

		// Detailed owner metrics
		assertThat(report.getDetailedOwnerMetrics()).hasSize(2);

		OwnerQualityMetrics o1Metrics = report.getDetailedOwnerMetrics().get(0);
		assertThat(o1Metrics.getOwnerId()).isEqualTo(owner1.getId());
		assertThat(o1Metrics.isAddressComplete()).isTrue();
		assertThat(o1Metrics.isCityComplete()).isTrue();
		assertThat(o1Metrics.isTelephoneComplete()).isTrue();
		assertThat(o1Metrics.isTelephoneValid()).isTrue();
		assertThat(o1Metrics.getPetMetrics()).hasSize(1);

		PetQualityMetrics p1Metrics = o1Metrics.getPetMetrics().get(0);
		assertThat(p1Metrics.getPetId()).isEqualTo(pet1.getId());
		assertThat(p1Metrics.isBirthDateComplete()).isTrue();
		assertThat(p1Metrics.isTypeComplete()).isTrue();
		assertThat(p1Metrics.isHasVisits()).isTrue();

		OwnerQualityMetrics o2Metrics = report.getDetailedOwnerMetrics().get(1);
		assertThat(o2Metrics.getOwnerId()).isEqualTo(owner2.getId());
		assertThat(o2Metrics.isAddressComplete()).isFalse();
		assertThat(o2Metrics.isCityComplete()).isTrue();
		assertThat(o2Metrics.isTelephoneComplete()).isTrue(); // Has text, but invalid format
		assertThat(o2Metrics.isTelephoneValid()).isFalse();
		assertThat(o2Metrics.getPetMetrics()).hasSize(1);

		PetQualityMetrics p2Metrics = o2Metrics.getPetMetrics().get(0);
		assertThat(p2Metrics.getPetId()).isEqualTo(pet2.getId());
		assertThat(p2Metrics.isBirthDateComplete()).isFalse();
		assertThat(p2Metrics.isTypeComplete()).isFalse();
		assertThat(p2Metrics.isHasVisits()).isFalse();
	}

	@Test
	void shouldHandleNoOwners() {
		// Given
		when(ownerRepository.findAllWithPetsAndVisits()).thenReturn(Collections.emptyList());

		// When
		DataQualityReport report = dataQualityService.generateDataQualityReport();

		// Then
		assertThat(report).isNotNull();
		assertThat(report.getTotalOwners()).isEqualTo(0);
		assertThat(report.getTotalPets()).isEqualTo(0);
		assertThat(report.getDetailedOwnerMetrics()).isEmpty();
		assertThat(report.getOwnersWithIncompleteAddress()).isEqualTo(0);
		assertThat(report.getPetsWithIncompleteBirthDate()).isEqualTo(0);
	}

	@Test
	void shouldHandleOwnerWithNoPets() {
		// Given
		Owner ownerWithNoPets = new Owner();
		ownerWithNoPets.setId(3);
		ownerWithNoPets.setFirstName("John");
		ownerWithNoPets.setLastName("Doe");
		ownerWithNoPets.setAddress("123 Main St");
		ownerWithNoPets.setCity("Anytown");
		ownerWithNoPets.setTelephone("1234567890");

		when(ownerRepository.findAllWithPetsAndVisits()).thenReturn(Collections.singletonList(ownerWithNoPets));

		// When
		DataQualityReport report = dataQualityService.generateDataQualityReport();

		// Then
		assertThat(report.getTotalOwners()).isEqualTo(1);
		assertThat(report.getTotalPets()).isEqualTo(0);
		assertThat(report.getDetailedOwnerMetrics()).hasSize(1);
		assertThat(report.getDetailedOwnerMetrics().get(0).getPetMetrics()).isEmpty();
	}

}
