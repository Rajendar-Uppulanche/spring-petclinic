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

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
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

	public VisitController(OwnerRepository owners) {
		this.owners = owners;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id", "*.id");
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: - Make sure
	 * we always have fresh data - Since we do not use the session scope, make sure that
	 * Pet object always has an id (Even though id is not part of the form fields)
	 * @param petId
	 * @return Pet
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
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
		pet.addVisit(visit);
		return visit;
	}

	@ModelAttribute("minVisitDate")
	public LocalDate minVisitDate() {
		return LocalDate.now();
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm(@ModelAttribute Pet pet, Map<String, Object> model) {
		model.put("visit", new Visit());
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes) {
		
		// BR-004, FR-011: Prevent future visit dates
		if (visit.getDate() != null && visit.getDate().isAfter(LocalDate.now())) {
			result.rejectValue("date", "typeMismatch.visitDate", "Visit date cannot be in the future.");
		}

		// FR-012, BR-005: Visit type classification
		if (visit.getVisitType() == null) {
			result.rejectValue("visitType", "typeMismatch.visitType", "Visit type is required.");
		}

		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}

		owner.addVisit(petId, visit);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit")
	public String initUpdateVisitForm(@PathVariable int visitId, @PathVariable int petId, @PathVariable int ownerId,
			Map<String, Object> model) {
		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException(
					"Pet with id " + petId + " not found for owner with id " + ownerId + ".");
		}
		Visit visit = pet.getVisit(visitId);
		if (visit == null) {
			throw new IllegalArgumentException(
					"Visit with id " + visitId + " not found for pet with id " + petId + ".");
		}
		model.put("pet", pet);
		model.put("owner", owner);
		model.put("visit", visit);
		return "pets/createOrUpdateVisitForm";
	}

	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/{visitId}/edit")
	public String processUpdateVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @PathVariable int visitId,
			@Valid Visit visit, BindingResult result, RedirectAttributes redirectAttributes) {

		// FR-013, BR-006: Ensure visit date and type are immutable during edit
		// The visit object is retrieved and populated before this method is called,
		// so we need to re-fetch the original visit to compare.
		Visit originalVisit = owner.getPet(petId).getVisit(visitId);
		if (originalVisit == null) {
			throw new IllegalArgumentException(
					"Visit with id " + visitId + " not found for pet with id " + petId + ".");
		}

		// Check if visit date has been changed
		if (!visit.getDate().equals(originalVisit.getDate())) {
			result.rejectValue("date", "immutable.field", "Visit date cannot be changed.");
		}

		// Check if visit type has been changed
		if (originalVisit.getVisitType() != null && !visit.getVisitType().equals(originalVisit.getVisitType())) {
			result.rejectValue("visitType", "immutable.field", "Visit type cannot be changed.");
		}

		// BR-004, FR-011: Prevent future visit dates
		if (visit.getDate() != null && visit.getDate().isAfter(LocalDate.now())) {
			result.rejectValue("date", "typeMismatch.visitDate", "Visit date cannot be in the future.");
		}

		// FR-012, BR-005: Visit type classification
		if (visit.getVisitType() == null) {
			result.rejectValue("visitType", "typeMismatch.visitType", "Visit type is required.");
		}

		// FR-013, BR-006: Editable visit description
		// The description is editable, so we only need to ensure it's not empty if required.
		// Assuming description is optional for now.

		// FR-014: Veterinarian assignment (optional)
		// This is handled by the Visit entity and its setters.

		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}

		// Update only the description and veterinarian if they have changed
		originalVisit.setDescription(visit.getDescription());
		originalVisit.setVeterinarian(visit.getVeterinarian());

		owner.addVisit(petId, originalVisit);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Your visit has been updated");
		return "redirect:/owners/{ownerId}";
	}

}