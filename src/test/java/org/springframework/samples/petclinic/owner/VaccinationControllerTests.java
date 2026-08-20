package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.samples.petclinic.vaccination.Vaccination;
import org.springframework.samples.petclinic.vaccination.VaccinationService;
import org.springframework.samples.petclinic.vaccination.VaccineType;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(value = VaccinationController.class,
    includeFilters = @ComponentScan.Filter(value = PetTypeFormatter.class, type = FilterType.ASSIGNABLE_TYPE))
@DisabledInNativeImage
@DisabledInAotMode
class VaccinationControllerTests {

    private static final int TEST_OWNER_ID = 1;
    private static final int TEST_PET_ID = 1;
    private static final int TEST_VACCINATION_ID = 1;
    private static final int TEST_VACCINE_TYPE_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VaccinationService vaccinationService;

    @MockBean
    private OwnerRepository ownerRepository;

    private Owner testOwner;
    private Pet testPet;
    private VaccineType rabiesType;

    @BeforeEach
    void setUp() {
        testOwner = new Owner();
        testOwner.setId(TEST_OWNER_ID);
        testOwner.setFirstName("George");
        testOwner.setLastName("Bush");

        PetType petType = new PetType();
        petType.setName("dog");

        testPet = new Pet();
        testPet.setId(TEST_PET_ID);
        testPet.setName("Leo");
        testPet.setBirthDate(LocalDate.of(2020, 1, 1));
        testPet.setType(petType);
        testPet.setOwner(testOwner);
        testOwner.addPet(testPet);

        rabiesType = new VaccineType();
        rabiesType.setId(TEST_VACCINE_TYPE_ID);
        rabiesType.setName("Rabies");
        rabiesType.setDefaultNextDueIntervalDays(365);

        given(ownerRepository.findById(TEST_OWNER_ID)).willReturn(Optional.of(testOwner));
        given(vaccinationService.findAllVaccineTypes()).willReturn(Collections.singletonList(rabiesType));
        given(vaccinationService.findVaccineTypeById(TEST_VACCINE_TYPE_ID)).willReturn(Optional.of(rabiesType));

        // Mock saveVaccination to not add errors by default
        doAnswer(invocation -> {
            BindingResult result = invocation.getArgument(2);
            if (result.hasErrors()) {
                return null; // Simulate early exit if errors are present
            }
            Vaccination vaccination = invocation.getArgument(0);
            if (vaccination.getVaccineType() == null || vaccination.getVaccineType().getId() == null) {
                result.rejectValue("vaccineType", "required", "Vaccine type is required");
            } else if (vaccination.getVaccineType().getId().equals(TEST_VACCINE_TYPE_ID)) {
                vaccination.setVaccineType(rabiesType); // Ensure managed entity is used
            } else {
                result.rejectValue("vaccineType", "notFound", "Selected vaccine type does not exist");
            }
            if (vaccination.getDateAdministered() != null && vaccination.getDateAdministered().isAfter(LocalDate.now())) {
                result.rejectValue("dateAdministered", "futureDate", "Date administered cannot be in the future");
            }
            if (vaccination.getDateAdministered() != null && vaccination.getNextDueDate() != null &&
                vaccination.getNextDueDate().isBefore(vaccination.getDateAdministered())) {
                result.rejectValue("nextDueDate", "beforeAdministered", "Next due date cannot be before date administered");
            }
            if (!result.hasErrors()) {
                if (vaccination.getNextDueDate() == null && vaccination.getDateAdministered() != null && vaccination.getVaccineType() != null && vaccination.getVaccineType().getDefaultNextDueIntervalDays() != null) {
                    vaccination.setNextDueDate(vaccination.getDateAdministered().plusDays(vaccination.getVaccineType().getDefaultNextDueIntervalDays()));
                }
                testPet.addVaccination(vaccination);
                vaccination.setPet(testPet);
            }
            return null;
        }).when(vaccinationService).saveVaccination(any(Vaccination.class), any(Pet.class), any(BindingResult.class));
    }

    @Test
    void initNewVaccinationForm() throws Exception {
        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/vaccinations/new", TEST_OWNER_ID, TEST_PET_ID))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdateVaccinationForm"))
            .andExpect(model().attributeExists("vaccination"))
            .andExpect(model().attributeExists("pet"))
            .andExpect(model().attributeExists("vaccineTypes"));
    }

    @Test
    void processNewVaccinationFormSuccess() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/vaccinations/new", TEST_OWNER_ID, TEST_PET_ID)
            .param("vaccineType.id", String.valueOf(TEST_VACCINE_TYPE_ID))
            .param("dateAdministered", "2023-01-01")
            .param("administeringVet", "Dr. Who")
            .param("batchNumber", "BATCH-001"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"))
            .andExpect(model().attributeExists("message"));

        verify(vaccinationService).saveVaccination(any(Vaccination.class), eq(testPet), any(BindingResult.class));
    }

    @Test
    void processNewVaccinationFormHasErrors() throws Exception {
        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/vaccinations/new", TEST_OWNER_ID, TEST_PET_ID)
            .param("vaccineType.id", String.valueOf(TEST_VACCINE_TYPE_ID))
            .param("dateAdministered", LocalDate.now().plusDays(1).toString()) // Future date
            .param("administeringVet", "Dr. Who")
            .param("batchNumber", "BATCH-001"))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdateVaccinationForm"))
            .andExpect(model().attributeHasFieldErrors("vaccination", "dateAdministered"));

        verify(vaccinationService).saveVaccination(any(Vaccination.class), eq(testPet), any(BindingResult.class));
    }

    @Test
    void initUpdateVaccinationForm() throws Exception {
        Vaccination existingVaccination = new Vaccination();
        existingVaccination.setId(TEST_VACCINATION_ID);
        existingVaccination.setPet(testPet);
        existingVaccination.setVaccineType(rabiesType);
        existingVaccination.setDateAdministered(LocalDate.of(2022, 1, 1));
        given(vaccinationService.findVaccinationById(TEST_VACCINATION_ID)).willReturn(Optional.of(existingVaccination));

        mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/vaccinations/{vaccinationId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VACCINATION_ID))
            .andExpect(status().isOk())
            .andExpect(view().name("pets/createOrUpdateVaccinationForm"))
            .andExpect(model().attributeExists("vaccination"))
            .andExpect(model().attribute("vaccination", existingVaccination));
    }

    @Test
    void processUpdateVaccinationFormSuccess() throws Exception {
        Vaccination existingVaccination = new Vaccination();
        existingVaccination.setId(TEST_VACCINATION_ID);
        existingVaccination.setPet(testPet);
        existingVaccination.setVaccineType(rabiesType);
        existingVaccination.setDateAdministered(LocalDate.of(2022, 1, 1));
        given(vaccinationService.findVaccinationById(TEST_VACCINATION_ID)).willReturn(Optional.of(existingVaccination));

        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/vaccinations/{vaccinationId}/edit", TEST_OWNER_ID, TEST_PET_ID, TEST_VACCINATION_ID)
            .param("vaccineType.id", String.valueOf(TEST_VACCINE_TYPE_ID))
            .param("dateAdministered", "2023-02-01")
            .param("administeringVet", "Dr. Strange")
            .param("batchNumber", "BATCH-002")
            .param("nextDueDate", "2024-02-01"))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"))
            .andExpect(model().attributeExists("message"));

        verify(vaccinationService).saveVaccination(any(Vaccination.class), eq(testPet), any(BindingResult.class));
    }

    @Test
    void deleteVaccination() throws Exception {
        Vaccination existingVaccination = new Vaccination();
        existingVaccination.setId(TEST_VACCINATION_ID);
        existingVaccination.setPet(testPet);
        existingVaccination.setVaccineType(rabiesType);
        existingVaccination.setDateAdministered(LocalDate.of(2022, 1, 1));
        given(vaccinationService.findVaccinationById(TEST_VACCINATION_ID)).willReturn(Optional.of(existingVaccination));

        mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/vaccinations/{vaccinationId}/delete", TEST_OWNER_ID, TEST_PET_ID, TEST_VACCINATION_ID))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/owners/{ownerId}"))
            .andExpect(model().attributeExists("message"));

        verify(vaccinationService).deleteVaccination(existingVaccination);
    }
}
