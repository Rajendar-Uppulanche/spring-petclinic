package org.springframework.samples.petclinic.vaccination;

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
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaccinationServiceTests {

    @Mock
    private VaccinationRepository vaccinationRepository;

    @Mock
    private VaccineTypeRepository vaccineTypeRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private VaccinationService vaccinationService;

    private Pet testPet;
    private VaccineType rabiesType;
    private Owner testOwner;

    @BeforeEach
    void setUp() {
        testOwner = new Owner();
        testOwner.setId(1);
        testOwner.setFirstName("George");
        testOwner.setLastName("Bush");

        PetType petType = new PetType();
        petType.setName("dog");

        testPet = new Pet();
        testPet.setId(1);
        testPet.setName("Leo");
        testPet.setBirthDate(LocalDate.of(2020, 1, 1));
        testPet.setType(petType);
        testPet.setOwner(testOwner);
        testOwner.addPet(testPet);

        rabiesType = new VaccineType();
        rabiesType.setId(1);
        rabiesType.setName("Rabies");
        rabiesType.setDefaultNextDueIntervalDays(365);

        when(vaccineTypeRepository.findById(1)).thenReturn(Optional.of(rabiesType));
        when(vaccineTypeRepository.findByName("Rabies")).thenReturn(Optional.of(rabiesType));
    }

    @Test
    void shouldFindAllVaccineTypes() {
        when(vaccineTypeRepository.findAll()).thenReturn(Arrays.asList(rabiesType));
        Collection<VaccineType> vaccineTypes = vaccinationService.findAllVaccineTypes();
        assertThat(vaccineTypes).hasSize(1);
        assertThat(vaccineTypes.iterator().next().getName()).isEqualTo("Rabies");
    }

    @Test
    void shouldFindVaccineTypeById() {
        Optional<VaccineType> foundType = vaccinationService.findVaccineTypeById(1);
        assertThat(foundType).isPresent();
        assertThat(foundType.get().getName()).isEqualTo("Rabies");
    }

    @Test
    void shouldFindVaccinationById() {
        Vaccination vaccination = new Vaccination();
        vaccination.setId(1);
        when(vaccinationRepository.findById(1)).thenReturn(Optional.of(vaccination));
        Optional<Vaccination> foundVaccination = vaccinationService.findVaccinationById(1);
        assertThat(foundVaccination).isPresent();
    }

    @Test
    void shouldFindVaccinationsByPet() {
        Vaccination vaccination = new Vaccination();
        vaccination.setPet(testPet);
        when(vaccinationRepository.findByPet(testPet)).thenReturn(Collections.singletonList(vaccination));
        Collection<Vaccination> vaccinations = vaccinationService.findVaccinationsByPet(testPet);
        assertThat(vaccinations).hasSize(1);
        assertThat(vaccinations.iterator().next().getPet()).isEqualTo(testPet);
    }

    @Test
    void shouldSaveNewVaccinationWithCalculatedNextDueDate() {
        Vaccination vaccination = new Vaccination();
        vaccination.setVaccineType(rabiesType);
        vaccination.setDateAdministered(LocalDate.of(2023, 1, 1));
        BindingResult result = new BeanPropertyBindingResult(vaccination, "vaccination");

        vaccinationService.saveVaccination(vaccination, testPet, result);

        assertThat(result.hasErrors()).isFalse();
        assertThat(vaccination.getNextDueDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        verify(vaccinationRepository, times(1)).save(vaccination);
        verify(ownerRepository, times(1)).save(testOwner);
        assertThat(testPet.getVaccinations()).contains(vaccination);
    }

    @Test
    void shouldSaveNewVaccinationWithProvidedNextDueDate() {
        Vaccination vaccination = new Vaccination();
        vaccination.setVaccineType(rabiesType);
        vaccination.setDateAdministered(LocalDate.of(2023, 1, 1));
        vaccination.setNextDueDate(LocalDate.of(2025, 1, 1)); // Provided next due date
        BindingResult result = new BeanPropertyBindingResult(vaccination, "vaccination");

        vaccinationService.saveVaccination(vaccination, testPet, result);

        assertThat(result.hasErrors()).isFalse();
        assertThat(vaccination.getNextDueDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        verify(vaccinationRepository, times(1)).save(vaccination);
        verify(ownerRepository, times(1)).save(testOwner);
        assertThat(testPet.getVaccinations()).contains(vaccination);
    }

    @Test
    void shouldRejectVaccinationWithFutureDateAdministered() {
        Vaccination vaccination = new Vaccination();
        vaccination.setVaccineType(rabiesType);
        vaccination.setDateAdministered(LocalDate.now().plusDays(1));
        BindingResult result = new BeanPropertyBindingResult(vaccination, "vaccination");

        vaccinationService.saveVaccination(vaccination, testPet, result);

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getFieldError("dateAdministered").getCode()).isEqualTo("futureDate");
        verify(vaccinationRepository, never()).save(any(Vaccination.class));
    }

    @Test
    void shouldRejectVaccinationWithNextDueDateBeforeAdministeredDate() {
        Vaccination vaccination = new Vaccination();
        vaccination.setVaccineType(rabiesType);
        vaccination.setDateAdministered(LocalDate.of(2023, 1, 1));
        vaccination.setNextDueDate(LocalDate.of(2022, 12, 31));
        BindingResult result = new BeanPropertyBindingResult(vaccination, "vaccination");

        vaccinationService.saveVaccination(vaccination, testPet, result);

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getFieldError("nextDueDate").getCode()).isEqualTo("beforeAdministered");
        verify(vaccinationRepository, never()).save(any(Vaccination.class));
    }

    @Test
    void shouldRejectVaccinationWithInvalidVaccineType() {
        VaccineType invalidType = new VaccineType();
        invalidType.setId(99); // Non-existent ID
        Vaccination vaccination = new Vaccination();
        vaccination.setVaccineType(invalidType);
        vaccination.setDateAdministered(LocalDate.now());
        BindingResult result = new BeanPropertyBindingResult(vaccination, "vaccination");

        when(vaccineTypeRepository.findById(99)).thenReturn(Optional.empty());

        vaccinationService.saveVaccination(vaccination, testPet, result);

        assertThat(result.hasErrors()).isTrue();
        assertThat(result.getFieldError("vaccineType").getCode()).isEqualTo("notFound");
        verify(vaccinationRepository, never()).save(any(Vaccination.class));
    }

    @Test
    void shouldDeleteVaccination() {
        Vaccination vaccination = new Vaccination();
        vaccination.setId(1);
        vaccination.setPet(testPet);
        testPet.setVaccinationsInternal(new HashSet<>(Collections.singletonList(vaccination)));

        vaccinationService.deleteVaccination(vaccination);

        verify(vaccinationRepository, times(1)).delete(vaccination);
        verify(ownerRepository, times(1)).save(testOwner);
        assertThat(testPet.getVaccinations()).doesNotContain(vaccination);
    }

    @Test
    void shouldReturnTrueIfPetIsOverdueForVaccination() {
        Vaccination overdueVaccination = new Vaccination();
        overdueVaccination.setNextDueDate(LocalDate.now().minusDays(1));
        testPet.addVaccination(overdueVaccination);

        assertThat(vaccinationService.isPetOverdueForVaccination(testPet)).isTrue();
    }

    @Test
    void shouldReturnFalseIfPetIsNotOverdueForVaccination() {
        Vaccination upToDateVaccination = new Vaccination();
        upToDateVaccination.setNextDueDate(LocalDate.now().plusDays(1));
        testPet.addVaccination(upToDateVaccination);

        Vaccination noDueDateVaccination = new Vaccination();
        noDueDateVaccination.setNextDueDate(null);
        testPet.addVaccination(noDueDateVaccination);

        assertThat(vaccinationService.isPetOverdueForVaccination(testPet)).isFalse();
    }

    @Test
    void shouldReturnFalseIfPetHasNoVaccinations() {
        testPet.setVaccinationsInternal(new HashSet<>()); // Ensure no vaccinations
        assertThat(vaccinationService.isPetOverdueForVaccination(testPet)).isFalse();
    }
}
