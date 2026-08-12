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
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 * @author Wick Dynex
 */
@Controller
class VisitController {

	private final OwnerRepository owners;
	private final VisitRepository visits;

	public VisitController(OwnerRepository owners, VisitRepository visits) {
		this.owners = owners;
		this.visits = visits;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id", "*.id");
		dataBinder.setAllowedFields("date", "description", "status"); // Allow status for updates
	}

	// This @ModelAttribute is for NEW visits only
	@ModelAttribute("visit")
	public Visit initNewVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			Map<String, Object> model) {
		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));

		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException(
					"Pet with id " + petId + " not found for owner with id " + ownerId + ".");
		}
		model.put("pet", pet);
		model.put("owner", owner);

		Visit visit = new Visit();
		visit.setPet(pet); // Set the pet for the new visit
		pet.addVisit(visit); // Add to pet's collection (for cascade save)
		return visit;
	}

	// This @ModelAttribute is for EXISTING visits (for edit/update)
	@ModelAttribute("existingVisit")
	public Visit initExistingVisit(@PathVariable("visitId") int visitId,
									@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
									Map<String, Object> model) {
		Visit existingVisit = visits.findById(visitId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found with id: " + visitId));

		// Verify owner and pet
		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));

		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException(
					"Pet with id " + petId + " not found for owner with id " + ownerId + ".");
		}

		if (existingVisit.getPet() == null || existingVisit.getPet().getId() != petId) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visit does not belong to the specified pet.");
		}

		model.put("pet", pet);
		model.put("owner", owner);

		return existingVisit;
	}


	@ModelAttribute("minVisitDate")
	public LocalDate minVisitDate() {
		return LocalDate.now().plusDays(1);
	}

	// Spring MVC calls method initNewVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method initNewVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute("visit") @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (visit.getDate() != null && !visit.getDate().isAfter(LocalDate.now())) {
			result.rejectValue("date", "typeMismatch.visitDate");
		}

		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}

		// The visit object already has its pet set by initNewVisit
		// and is part of the pet's visits collection, which is part of the owner.
		this.owners.save(visit.getPet().getOwner()); // Saving owner will cascade save the new visit
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}

	// New GET mapping for editing an existing visit
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit")
	public String initUpdateVisitForm(@ModelAttribute("existingVisit") Visit existingVisit, Map<String, Object> model) {
		model.put("visit", existingVisit); // Make the existing visit available as "visit" for the form
		return "pets/createOrUpdateVisitForm"; // Re-use the form
	}

	// New POST mapping for processing updates to an existing visit
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit")
	public String processUpdateVisitForm(@ModelAttribute("existingVisit") Visit existingVisit, // The original visit
										 @Valid @ModelAttribute("visit") Visit visitForm, // Form data for update, named "visit"
										 BindingResult result,
										 RedirectAttributes redirectAttributes) {

		// The pet and owner are already in the model from initExistingVisit
		// No need to re-add them here unless there's a specific reason for error rendering.

		if (visitForm.getDate() != null && !visitForm.getDate().isAfter(LocalDate.now())) {
			result.rejectValue("date", "typeMismatch.visitDate");
		}

		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}

		try {
			// Apply updates from form to existing visit
			existingVisit.setDate(visitForm.getDate());
			existingVisit.setDescription(visitForm.getDescription());

			// Validate and apply status transition if status has changed
			if (visitForm.getStatus() != null && existingVisit.getStatus() != visitForm.getStatus()) {
				existingVisit.transitionTo(visitForm.getStatus());
			}

			this.visits.save(existingVisit); // Save the updated visit
			redirectAttributes.addFlashAttribute("message", "Visit updated successfully!");
			return "redirect:/owners/{ownerId}";
		} catch (IllegalArgumentException e) {
			result.rejectValue("status", "invalidStatusTransition", e.getMessage());
			return "pets/createOrUpdateVisitForm";
		}
	}

}
