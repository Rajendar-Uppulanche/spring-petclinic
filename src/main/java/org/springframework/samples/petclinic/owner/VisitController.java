/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetRepository;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Luigi R. Viggiano
 * @author Waseem Akram
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Colin But
 */
@Controller
class VisitController {

	private static final String VIEWS_VISIT_FORM = "pets/createOrUpdateVisitForm";
	private final PetRepository pets;

	@Autowired
	public VisitController(PetRepository pets) {
		this.pets = pets;
	}

	@InitBinder("visit")
	public void initVisitBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new VisitValidator());
	}

	/**
	 * Called before each test method (or controller method) to set up a new Visit object
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@RequestParam(name = "petId", required = false) Integer petId,
										@PathVariable(name = "petId", required = false) Integer petIdPathVariable) {
		final Integer id = (petId != null) ? petId : petIdPathVariable;
		//
		// Find the pet corresponding to the passed in petId. If the pet is not found,
		// throw an exception. This is a common pattern used to simplify the code:
		// if the pet is not found, then the controller method will not be executed.
		//
		Pet pet = this.pets.findById(id.intValue());
		return new Visit(pet);
	}

	/**
	 * Called before each test method (or controller method) to set up the owner object
	 */
	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable("ownerId") int ownerId) {
		return this.pets.findOwnerByPetId(ownerId);
	}

	/**
	 * Called before each test method (or controller method) to set up the Pet object
	 */
	@ModelAttribute("pet")
	public Pet findPet(@PathVariable("petId") int petId) {
		return this.pets.findById(petId);
	}

	@GetMapping(value = "/owners/*/pets/{petId}/visits/new")
	public String initNewVisitForm(@PathVariable("petId") int petId, ModelMap model) {
		return VIEWS_VISIT_FORM;
	}

	/**
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@Valid Visit visit, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			// This is a workaround to ensure that the pet is available in the model
			// when the form is re-displayed after a validation error.
			// The pet is not available in the model because the @ModelAttribute("pet")
			// method is not called again when there are validation errors.
			Pet pet = this.pets.findById(visit.getPet().getId());
			model.addAttribute("pet", pet);
			return VIEWS_VISIT_FORM;
		}
		else {
			this.pets.saveVisit(visit);
			return "redirect:/owners/{ownerId}";
		}
	}

	@GetMapping(value = "/owners/{ownerId}/pets/{petId}/visits/{visitId}/checkin")
	public String checkInVisit(@PathVariable("visitId") int visitId, ModelMap model) {
		Visit visit = this.pets.findVisitById(visitId);
		if (visit == null) {
			return "error"; // Or redirect to an error page
		}

		// BR-014: Check-in not more than 24 hours in the future
		if (visit.getDate().isAfter(LocalDate.now().plusDays(1))) {
			model.addAttribute("error", "Check-in cannot be more than 24 hours in the future.");
			return VIEWS_VISIT_FORM;
		}

		visit.setCheckInTime(LocalDateTime.now());
		this.pets.saveVisit(visit);

		// Reload owner and pet to reflect changes in the UI
		Owner owner = this.pets.findOwnerByPetId(visit.getPet().getOwnerId());
		Pet pet = this.pets.findById(visit.getPet().getId());
		model.addAttribute("owner", owner);
		model.addAttribute("pet", pet);
		model.addAttribute("visit", visit); // Add the updated visit to the model

		return "redirect:/owners/" + visit.getPet().getOwnerId() + "/pets/" + visit.getPet().getId();
	}

	@GetMapping(value = "/owners/{ownerId}/pets/{petId}/visits/{visitId}/checkout")
	public String checkOutVisit(@PathVariable("visitId") int visitId, ModelMap model) {
		Visit visit = this.pets.findVisitById(visitId);
		if (visit == null) {
			return "error"; // Or redirect to an error page
		}

		// BR-013: Check-out not before check-in
		if (visit.getCheckInTime() != null && LocalDateTime.now().isBefore(visit.getCheckInTime())) {
			model.addAttribute("error", "Check-out cannot be before check-in.");
			// Reload owner and pet to reflect changes in the UI
			Owner owner = this.pets.findOwnerByPetId(visit.getPet().getOwnerId());
			Pet pet = this.pets.findById(visit.getPet().getId());
			model.addAttribute("owner", owner);
			model.addAttribute("pet", pet);
			return VIEWS_VISIT_FORM;
		}

		visit.setCheckOutTime(LocalDateTime.now());
		this.pets.saveVisit(visit);

		// Reload owner and pet to reflect changes in the UI
		Owner owner = this.pets.findOwnerByPetId(visit.getPet().getOwnerId());
		Pet pet = this.pets.findById(visit.getPet().getId());
		model.addAttribute("owner", owner);
		model.addAttribute("pet", pet);
		model.addAttribute("visit", visit); // Add the updated visit to the model

		return "redirect:/owners/" + visit.getPet().getOwnerId() + "/pets/" + visit.getPet().getId();
	}

}
