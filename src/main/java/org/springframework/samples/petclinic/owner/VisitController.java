package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.util.ValidationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Map;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
public class VisitController {

    private static final String VIEWS_VISIT_CREATE_OR_UPDATE_FORM = "owner/createOrUpdateVisitForm";
    private final OwnerService ownerService;

    @Autowired
    public VisitController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    /**
     * Called when POST processing a form for adding a visit
     */
    @PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    public String processNewVisitForm(@Validated Visit visit, BindingResult result, @PathVariable("petId") int petId, @PathVariable("ownerId") int ownerId) {
        // FR-010: Implement Error Handling for Invalid IDs
        if (ValidationUtil.isInvalidId(ownerId) || ValidationUtil.isInvalidId(petId)) {
            ObjectError error = new ObjectError("globalError", "Invalid ownerId or petId provided.");
            result.addError(error);
            return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
        }

        Pet pet = this.ownerService.findPetById(petId);
        if (pet == null) {
            ObjectError error = new ObjectError("globalError", "Pet with ID " + petId + " not found.");
            result.addError(error);
            return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
        }

        Owner owner = this.ownerService.findById(ownerId);
        if (owner == null) {
            ObjectError error = new ObjectError("globalError", "Owner with ID " + ownerId + " not found.");
            result.addError(error);
            return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
        }

        // Check if the pet belongs to the owner
        if (!owner.getPets().contains(pet)) {
            ObjectError error = new ObjectError("globalError", "Pet with ID " + petId + " does not belong to owner with ID " + ownerId + ".");
            result.addError(error);
            return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
        }

        visit.setPet(pet);
        if (result.hasErrors()) {
            return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
        }
        this.ownerService.saveVisit(visit);
        return "redirect:/owners/{ownerId}";
    }

    /**
     * Called when GET processing a form for adding a visit
     */
    @GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
    public String initNewVisitForm(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, Map<String, Object> model) {
        // FR-010: Implement Error Handling for Invalid IDs
        if (ValidationUtil.isInvalidId(ownerId) || ValidationUtil.isInvalidId(petId)) {
            model.put("message", "Invalid ownerId or petId provided.");
            return "error"; // Assuming an error view exists
        }

        Visit visit = new Visit();
        Owner owner = this.ownerService.findById(ownerId);
        if (owner == null) {
            model.put("message", "Owner with ID " + ownerId + " not found.");
            return "error";
        }

        Pet pet = this.ownerService.findPetById(petId);
        if (pet == null) {
            model.put("message", "Pet with ID " + petId + " not found.");
            return "error";
        }

        // Check if the pet belongs to the owner
        if (!owner.getPets().contains(pet)) {
            model.put("message", "Pet with ID " + petId + " does not belong to owner with ID " + ownerId + ".");
            return "error";
        }

        visit.setPet(pet);
        model.put("visit", visit);
        model.put("ownerId", ownerId);
        model.put("petId", petId);
        return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
    }
}
