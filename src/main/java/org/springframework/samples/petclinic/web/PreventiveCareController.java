package org.springframework.samples.petclinic.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PreventiveCare;
import org.springframework.samples.petclinic.service.PetService;
import org.springframework.samples.petclinic.service.PreventiveCareService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping("/owners/{ownerId}/pets/{petId}/preventivecares")
public class PreventiveCareController {

    private static final String VIEWS_PREVENTIVE_CARE_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePreventiveCareForm";

    private final PetService petService;
    private final PreventiveCareService preventiveCareService;

    @Autowired
    public PreventiveCareController(PetService petService, PreventiveCareService preventiveCareService) {
        this.petService = petService;
        this.preventiveCareService = preventiveCareService;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @ModelAttribute("pet")
    public Pet findPet(@PathVariable("petId") int petId) {
        return this.petService.findPetById(petId);
    }

    @GetMapping("/new")
    public String initNewPreventiveCareForm(@PathVariable("petId") int petId, ModelMap model) {
        Pet pet = findPet(petId);
        PreventiveCare preventiveCare = new PreventiveCare();
        pet.addPreventiveCare(preventiveCare);
        preventiveCare.setCareDate(LocalDate.now()); // Default to today
        model.put("preventiveCare", preventiveCare);
        return VIEWS_PREVENTIVE_CARE_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/new")
    public String processNewPreventiveCareForm(@PathVariable("petId") int petId, @Valid PreventiveCare preventiveCare, BindingResult result, ModelMap model) {
        Pet pet = findPet(petId);
        if (result.hasErrors()) {
            model.put("preventiveCare", preventiveCare);
            return VIEWS_PREVENTIVE_CARE_CREATE_OR_UPDATE_FORM;
        } else {
            pet.addPreventiveCare(preventiveCare);
            this.preventiveCareService.savePreventiveCare(preventiveCare);
            return "redirect:/owners/{ownerId}";
        }
    }

    @GetMapping("/{preventiveCareId}/edit")
    public String initUpdatePreventiveCareForm(@PathVariable("preventiveCareId") int preventiveCareId, ModelMap model) {
        PreventiveCare preventiveCare = this.preventiveCareService.findPreventiveCareById(preventiveCareId);
        model.put("preventiveCare", preventiveCare);
        return VIEWS_PREVENTIVE_CARE_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/{preventiveCareId}/edit")
    public String processUpdatePreventiveCareForm(@Valid PreventiveCare preventiveCare, BindingResult result, @PathVariable("petId") int petId, @PathVariable("preventiveCareId") int preventiveCareId, ModelMap model) {
        if (result.hasErrors()) {
            model.put("preventiveCare", preventiveCare);
            return VIEWS_PREVENTIVE_CARE_CREATE_OR_UPDATE_FORM;
        } else {
            preventiveCare.setId(preventiveCareId); // Ensure ID is set for update
            Pet pet = findPet(petId);
            preventiveCare.setPet(pet); // Associate with the correct pet
            this.preventiveCareService.savePreventiveCare(preventiveCare);
            return "redirect:/owners/{ownerId}";
        }
    }

    @GetMapping("/{preventiveCareId}/delete")
    public String deletePreventiveCare(@PathVariable("preventiveCareId") int preventiveCareId, @PathVariable("ownerId") int ownerId) {
        this.preventiveCareService.deletePreventiveCare(preventiveCareId);
        return "redirect:/owners/{ownerId}";
    }
}
