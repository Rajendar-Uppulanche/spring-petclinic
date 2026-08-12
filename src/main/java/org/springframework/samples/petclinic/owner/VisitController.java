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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.util.DateTimeUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Ramesh Pardeshi
 * @author Chris Richard
 * @author Maciej Szefler
 * @author John J. Blum
 */
@Controller
@SessionAttributes("owner")
public class VisitController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private static final String VIEWS_PET_CREATE_OR_UPDATE_FORM = "pets/createOrUpdateVisitForm";

	private final OwnerRepository ownersRepository;

	@Autowired
	public VisitController(OwnerRepository ownersRepository) {
		this.ownersRepository = ownersRepository;
	}

	@InitBinder("owner")
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/**
	 * Called before each handler method. 
	 * @param model
	 */
	@ModelAttribute("owner")
	public String refOwner(ModelMap model) {
		return "owner"; // This is a placeholder, actual owner retrieval logic should be here
	}

	/**
	 * Called before each handler method. 
	 * @param model
	 */
	@ModelAttribute("pet")
	public String refPet(ModelMap model) {
		return "pet"; // This is a placeholder, actual pet retrieval logic should be here
	}

	@GetMapping(value = "/owners/*/pets/{petId}/visits/new")
	public String initNewVisitForm(@PathVariable("petId") int petId, ModelMap model) {
		// This method should retrieve the pet and owner based on petId and add them to the model.
		// For now, we'll assume they are already in the model or can be retrieved.
		model.addAttribute("visit", new Visit());
		return VIEWS_PET_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping(value = "/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@PathVariable int ownerId, @PathVariable int petId, Visit visit, BindingResult result, SessionStatus status) {
		if (result.hasErrors()) {
			return VIEWS_PET_CREATE_OR_UPDATE_FORM;
		}
		else {
			// Retrieve owner and pet to associate the visit
			Owner owner = this.ownersRepository.findById(ownerId).orElse(null);
			if (owner == null) {
				// Handle error: owner not found
				return VIEWS_PET_CREATE_OR_UPDATE_FORM; // Or redirect to an error page
			}
			Pet pet = owner.getPet(petId);
			if (pet == null) {
				// Handle error: pet not found
				return VIEWS_PET_CREATE_OR_UPDATE_FORM; // Or redirect to an error page
			}
			visit.setPet(pet);
			// Save the visit
			// This part needs to be implemented: visitRepository.save(visit);
			status.setComplete();
			return "redirect:/owners/{ownerId}";
		}
	}

	@PostMapping(value = "/visits/{visitId}/checkin")
	public String checkInVisit(@PathVariable int visitId) {
		// Retrieve the visit
		// Visit visit = visitRepository.findById(visitId).orElse(null);
		Visit visit = new Visit(); // Placeholder
		visit.setId(visitId);

		// Check BR-014: check-in not more than 24 hours in the future
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime visitDate = visit.getDate().atStartOfDay(); // Assuming visit date is stored as LocalDate
		if (now.isAfter(visitDate.plusHours(24))) {
			// Handle validation error: check-in too far in the future
			// For now, we'll just log or return an error status
			System.err.println("BR-014 Violation: Check-in cannot be more than 24 hours in the future.");
			return "redirect:/owners/{ownerId}"; // Redirect back or to an error page
		}

		visit.setCheckInTime(now);
		// Save the updated visit
		// visitRepository.save(visit);

		return "redirect:/owners/{ownerId}"; // Redirect to owner's details page
	}

	@PostMapping(value = "/visits/{visitId}/checkout")
	public String checkOutVisit(@PathVariable int visitId) {
		// Retrieve the visit
		// Visit visit = visitRepository.findById(visitId).orElse(null);
		Visit visit = new Visit(); // Placeholder
		visit.setId(visitId);

		LocalDateTime now = LocalDateTime.now();

		// Check BR-013: check-out not before check-in
		if (visit.getCheckInTime() != null && now.isBefore(visit.getCheckInTime())) {
			// Handle validation error: check-out before check-in
			System.err.println("BR-013 Violation: Check-out cannot be before check-in.");
			return "redirect:/owners/{ownerId}"; // Redirect back or to an error page
		}

		visit.setCheckOutTime(now);
		// Save the updated visit
		// visitRepository.save(visit);

		return "redirect:/owners/{ownerId}"; // Redirect to owner's details page
	}

}
