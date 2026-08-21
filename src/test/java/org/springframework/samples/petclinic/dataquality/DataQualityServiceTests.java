package org.springframework.samples.petclinic.dataquality;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.pet.PetRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataQualityServiceTests {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private PetRepository petRepository;

    private DataQualityService dataQualityService;

    @BeforeEach
    void setUp() {
        dataQualityService = new DataQualityService(ownerRepository, petRepository);
        // Set default thresholds for testing
        ReflectionTestUtils.setField(dataQualityService, "ownerEmailCompletenessThreshold", 0.95);
        ReflectionTestUtils.setField(dataQualityService, "petNameCompletenessThreshold", 0.98);
    }

    @Test
    void shouldPassOwnerEmailCompletenessCheckWhenAllEmailsPresent() {
        Owner owner1 = new Owner(); owner1.setEmail("test1@example.com");
        Owner owner2 = new Owner(); owner2.setEmail("test2@example.com");
        when(ownerRepository.findAll()).thenReturn(Arrays.asList(owner1, owner2));

        DataQualityService.DataQualityReport report = dataQualityService.checkOwnerContactDetailsQuality();

        assertThat(report.isPassed()).isTrue();
        assertThat(report.getMetricValue()).isEqualTo(1.0);
        assertThat(report.getSummary()).contains("PASSED");
    }

    @Test
    void shouldFailOwnerEmailCompletenessCheckWhenEmailsMissing() {
        Owner owner1 = new Owner(); owner1.setEmail("test1@example.com");
        Owner owner2 = new Owner(); // No email
        Owner owner3 = new Owner(); owner3.setEmail(""); // Empty email
        when(ownerRepository.findAll()).thenReturn(Arrays.asList(owner1, owner2, owner3));

        DataQualityService.DataQualityReport report = dataQualityService.checkOwnerContactDetailsQuality();

        assertThat(report.isPassed()).isFalse();
        assertThat(report.getMetricValue()).isEqualTo(1.0/3.0); // 1 out of 3 has email
        assertThat(report.getSummary()).contains("FAILED");
        assertThat(report.getIssues()).contains("Low email completeness for owners. Current: 33.33%");
    }

    @Test
    void shouldPassPetNameCompletenessCheckWhenAllNamesPresent() {
        Pet pet1 = new Pet(); pet1.setName("Buddy");
        Pet pet2 = new Pet(); pet2.setName("Max");
        when(petRepository.findAll()).thenReturn(Arrays.asList(pet1, pet2));

        DataQualityService.DataQualityReport report = dataQualityService.checkPetRecordsQuality();

        assertThat(report.isPassed()).isTrue();
        assertThat(report.getMetricValue()).isEqualTo(1.0);
        assertThat(report.getSummary()).contains("PASSED");
    }

    @Test
    void shouldFailPetNameCompletenessCheckWhenNamesMissing() {
        Pet pet1 = new Pet(); pet1.setName("Buddy");
        Pet pet2 = new Pet(); // No name
        Pet pet3 = new Pet(); pet3.setName(""); // Empty name
        when(petRepository.findAll()).thenReturn(Arrays.asList(pet1, pet2, pet3));

        DataQualityService.DataQualityReport report = dataQualityService.checkPetRecordsQuality();

        assertThat(report.isPassed()).isFalse();
        assertThat(report.getMetricValue()).isEqualTo(1.0/3.0); // 1 out of 3 has name
        assertThat(report.getSummary()).contains("FAILED");
        assertThat(report.getIssues()).contains("Low name completeness for pets. Current: 33.33%");
    }

    @Test
    void shouldHandleEmptyOwnerListGracefully() {
        when(ownerRepository.findAll()).thenReturn(Collections.emptyList());
        DataQualityService.DataQualityReport report = dataQualityService.checkOwnerContactDetailsQuality();
        assertThat(report.isPassed()).isTrue(); // 1.0 completeness for empty set
        assertThat(report.getMetricValue()).isEqualTo(1.0);
    }

    @Test
    void shouldHandleEmptyPetListGracefully() {
        when(petRepository.findAll()).thenReturn(Collections.emptyList());
        DataQualityService.DataQualityReport report = dataQualityService.checkPetRecordsQuality();
        assertThat(report.isPassed()).isTrue(); // 1.0 completeness for empty set
        assertThat(report.getMetricValue()).isEqualTo(1.0);
    }
}
