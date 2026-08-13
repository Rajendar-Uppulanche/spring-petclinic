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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

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
	private final VisitStatusService visitStatusService;

	public VisitController(OwnerRepository owners, VisitRepository visits, VisitStatusService visitStatusService) {
		this.owners = owners;
		this.visits = visits;
		this.visitStatusService = visitStatusService;
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
		// FR-045: Set default status for new visits
		visit.setStatus(VisitStatus.SCHEDULED); // Set default status
		pet.addVisit(visit);
		return visit;
	}

	@ModelAttribute("minVisitDate")
	public LocalDate minVisitDate() {
		return LocalDate.now().plusDays(1);
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner, @PathVariable int petId, @Valid Visit visit,
			BindingResult result, RedirectAttributes redirectAttributes) {
		if (visit.getDate() != null && !visit.getDate().isAfter(LocalDate.now())) {
			result.rejectValue("date", "typeMismatch.visitDate");
		}

		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}

		owner.addVisit(petId, visit);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * FR-043: API endpoint to update a visit's status.
	 * Allows authorized staff to update a visit's status, enforcing business rules.
	 * @param visitId The ID of the visit to update.
	 * @param newStatus The new status to set for the visit.
	 * @return ResponseEntity with updated Visit or error.
	 */
	@PutMapping("/visits/{visitId}/status")
	public ResponseEntity<Visit> updateVisitStatus(@PathVariable("visitId") int visitId,
											   @RequestBody VisitStatus newStatus) {
		Optional<Visit> optionalVisit = visits.findById(visitId);
		if (optionalVisit.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Visit not found with ID: " + visitId);
		}

		Visit visit = optionalVisit.get();
		VisitStatus currentStatus = visit.getStatus();

		if (!visitStatusService.isValidTransition(currentStatus, newStatus)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Invalid status transition from " + currentStatus + " to " + newStatus);
		}

		visit.setStatus(newStatus);
		visits.save(visit);
		return ResponseEntity.ok(visit);
	}
}