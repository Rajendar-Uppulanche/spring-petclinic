package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.util.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@Controller
@RequestMapping("/owners/{ownerId}/pets")
public class PetController {

    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdatePetForm";

    private final PetRepository pets;
    private final OwnerRepository owners;

    @Autowired
    public PetController(PetRepository pets, OwnerRepository owners) {
        this.pets = pets;
        this.owners = owners;
    }

    @ModelAttribute("petTypes")
    public List<String> getPetTypes() {
        final List<String> petTypes = new ArrayList<>();
        petTypes.add("cat");
        petTypes.add("dog");
        petTypes.add("lizard");
        petTypes.add("snake");
        return petTypes;
    }

    @GetMapping("/new")
    public String initCreationForm(@PathVariable("ownerId") int ownerId, ModelMap model) {
        Owner owner = this.owners.findById(ownerId);
        if (owner == null) {
            throw new ResourceNotFoundException("Owner not found with id: " + ownerId);
        }
        Pet pet = new Pet();
        owner.addPet(pet);
        model.addAttribute("pet", pet);
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/new")
    public String processCreationForm(@Valid Pet pet, BindingResult result, @PathVariable("ownerId") int ownerId) {
        Owner owner = this.owners.findById(ownerId);
        if (owner == null) {
            throw new ResourceNotFoundException("Owner not found with id: " + ownerId);
        }
        if (result.hasErrors()) {
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        } else {
            owner.addPet(pet);
            this.pets.save(pet);
            return "redirect:/owners/" + ownerId;
        }
    }

    @GetMapping("/{petId}/edit")
    public String initUpdateForm(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, ModelMap model) {
        Owner owner = this.owners.findById(ownerId);
        if (owner == null) {
            throw new ResourceNotFoundException("Owner not found with id: " + ownerId);
        }
        Pet pet = owner.getPet(petId);
        if (pet == null) {
            throw new ResourceNotFoundException("Pet not found with id: " + petId + " for owner: " + ownerId);
        }
        model.addAttribute("pet", pet);
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    @PostMapping("/{petId}/edit")
    public String processUpdateForm(@Valid Pet pet, BindingResult result, @PathVariable("ownerId") int ownerId) {
        if (result.hasErrors()) {
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        } else {
            this.pets.save(pet);
            return "redirect:/owners/" + ownerId;
        }
    }

    // This method is called from VisitController to load pet data by owner and pet IDs.
    // It is now modified to include error handling for invalid ownerId or petId.
    private Pet loadPetWithVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, ModelMap model) {
        Owner owner = this.owners.findById(ownerId);
        if (owner == null) {
            throw new ResourceNotFoundException("Owner not found with id: " + ownerId);
        }
        Pet pet = owner.getPet(petId);
        if (pet == null) {
            throw new ResourceNotFoundException("Pet not found with id: " + petId + " for owner: " + ownerId);
        }
        model.addAttribute("pet", pet);
        return pet;
    }

}
