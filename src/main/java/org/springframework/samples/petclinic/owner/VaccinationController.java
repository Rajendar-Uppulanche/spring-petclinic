package org.springframework.samples.petclinic.owner;

import org.springframework.samples.petclinic.vaccination.Vaccination;
import org.springframework.samples.petclinic.vaccination.VaccinationService;
import org.springframework.samples.petclinic.vaccination.VaccineType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@Controller
@RequestMapping("/owners/{ownerId}/pets/{petId}")
class VaccinationController {

    private static final String VIEWS_VACCINATION_CREATE_OR_UPDATE_FORM = "pets/createOrUpdateVaccinationForm";

    private final VaccinationService vaccinationService;
    private final OwnerRepository ownerRepository; // To find owner and pet

    public VaccinationController(VaccinationService vaccinationService, OwnerRepository ownerRepository) {
        this.vaccinationService = vaccinationService;
        this.ownerRepository = ownerRepository;
    }

    @ModelAttribute("vaccineTypes")
    public Collection<VaccineType> populateVaccineTypes() {
        return this.vaccinationService.findAllVaccineTypes();
    }

    @ModelAttribute("pet")
    public Pet findPet(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId) {
        Optional<Owner> optionalOwner = this.ownerRepository.findById(ownerId);
        Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
            "Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
        return owner.getPet(petId);
    }

    @ModelAttribute("vaccination")
    public Vaccination findVaccination(@PathVariable(name = "vaccinationId", required = false) Integer vaccinationId) {
        if (vaccinationId == null) {
            return new Vaccination();
        }
        return this.vaccinationService.findVaccinationById(vaccinationId)
            .orElseThrow(() -> new IllegalArgumentException("Vaccination not found with id: " + vaccinationId));
    }

    @GetMapping("/vaccinations/new")
    public String initNewVaccinationForm(Pet pet, ModelMap model) {
        Vaccination vaccination = new Vaccination();
        pet.addVaccination(vaccination);
        model.put("vaccination", vaccination);
        return VIEWS_VACCINATION_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/vaccinations/new")
    public String processNewVaccinationForm(Pet pet, @Valid Vaccination vaccination, BindingResult result,
                                            RedirectAttributes redirectAttributes) {
        this.vaccinationService.saveVaccination(vaccination, pet, result);

        if (result.hasErrors()) {
            return VIEWS_VACCINATION_CREATE_OR_UPDATE_FORM;
        }

        redirectAttributes.addFlashAttribute("message", "New vaccination record added.");
        return "redirect:/owners/{ownerId}";
    }

    @GetMapping("/vaccinations/{vaccinationId}/edit")
    public String initUpdateVaccinationForm(Vaccination vaccination, ModelMap model) {
        model.put("vaccination", vaccination);
        return VIEWS_VACCINATION_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/vaccinations/{vaccinationId}/edit")
    public String processUpdateVaccinationForm(Pet pet, @Valid Vaccination vaccination, BindingResult result,
                                               RedirectAttributes redirectAttributes) {
        this.vaccinationService.saveVaccination(vaccination, pet, result);

        if (result.hasErrors()) {
            return VIEWS_VACCINATION_CREATE_OR_UPDATE_FORM;
        }

        redirectAttributes.addFlashAttribute("message", "Vaccination record updated.");
        return "redirect:/owners/{ownerId}";
    }

    @PostMapping("/vaccinations/{vaccinationId}/delete")
    public String deleteVaccination(@PathVariable("vaccinationId") int vaccinationId,
                                    @ModelAttribute("vaccination") Vaccination vaccination,
                                    RedirectAttributes redirectAttributes) {
        this.vaccinationService.deleteVaccination(vaccination);
        redirectAttributes.addFlashAttribute("message", "Vaccination record deleted.");
        return "redirect:/owners/{ownerId}";
    }
}
