package org.springframework.samples.petclinic.vaccination;

import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final VaccineTypeRepository vaccineTypeRepository;
    private final OwnerRepository ownerRepository; // To save pet changes

    public VaccinationService(VaccinationRepository vaccinationRepository,
                              VaccineTypeRepository vaccineTypeRepository,
                              OwnerRepository ownerRepository) {
        this.vaccinationRepository = vaccinationRepository;
        this.vaccineTypeRepository = vaccineTypeRepository;
        this.ownerRepository = ownerRepository;
    }

    @Transactional(readOnly = true)
    public Collection<VaccineType> findAllVaccineTypes() {
        return vaccineTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<VaccineType> findVaccineTypeById(Integer id) {
        return vaccineTypeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Vaccination> findVaccinationById(Integer id) {
        return vaccinationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Collection<Vaccination> findVaccinationsByPet(Pet pet) {
        return vaccinationRepository.findByPet(pet);
    }

    @Transactional
    public void saveVaccination(Vaccination vaccination, Pet pet, BindingResult result) {
        // BR-017: dateAdministered not in the future
        if (vaccination.getDateAdministered() != null && vaccination.getDateAdministered().isAfter(LocalDate.now())) {
            result.rejectValue("dateAdministered", "futureDate", "Date administered cannot be in the future");
        }

        // BR-018: vaccineType selected from predefined list
        if (vaccination.getVaccineType() == null || vaccination.getVaccineType().getId() == null) {
            result.rejectValue("vaccineType", "required", "Vaccine type is required");
        } else {
            Optional<VaccineType> existingType = vaccineTypeRepository.findById(vaccination.getVaccineType().getId());
            if (existingType.isEmpty()) {
                result.rejectValue("vaccineType", "notFound", "Selected vaccine type does not exist");
            } else {
                vaccination.setVaccineType(existingType.get()); // Ensure managed entity is used
            }
        }

        // BR-020: nextDueDate on or after dateAdministered
        if (vaccination.getDateAdministered() != null && vaccination.getNextDueDate() != null &&
            vaccination.getNextDueDate().isBefore(vaccination.getDateAdministered())) {
            result.rejectValue("nextDueDate", "beforeAdministered", "Next due date cannot be before date administered");
        }

        if (result.hasErrors()) {
            return;
        }

        // If nextDueDate is not provided, calculate it based on default interval
        if (vaccination.getNextDueDate() == null && vaccination.getVaccineType() != null &&
            vaccination.getVaccineType().getDefaultNextDueIntervalDays() != null &&
            vaccination.getDateAdministered() != null) {
            vaccination.setNextDueDate(vaccination.getDateAdministered().plusDays(vaccination.getVaccineType().getDefaultNextDueIntervalDays()));
        }

        pet.addVaccination(vaccination);
        vaccination.setPet(pet);
        this.vaccinationRepository.save(vaccination);
        this.ownerRepository.save(pet.getOwner()); // Ensure pet and owner are updated
    }

    @Transactional
    public void deleteVaccination(Vaccination vaccination) {
        vaccination.getPet().getVaccinationsInternal().remove(vaccination);
        this.vaccinationRepository.delete(vaccination);
        this.ownerRepository.save(vaccination.getPet().getOwner()); // Ensure owner is updated
    }

    // Step 5: Implement Pet Overdue Vaccination Status Logic
    @Transactional(readOnly = true)
    public boolean isPetOverdueForVaccination(Pet pet) {
        LocalDate today = LocalDate.now();
        return pet.getVaccinations().stream()
            .anyMatch(v -> v.getNextDueDate() != null && v.getNextDueDate().isBefore(today));
    }
}
