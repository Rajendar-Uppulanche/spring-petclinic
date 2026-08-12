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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

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
	private final VisitService visitService;
	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
	private static final String VIEWS_PET_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";
	private static final String VIEWS_PET_OWNER_FORM = "owners/findOwners";
	private static final String VIEWS_OWNER_DETAILS = "owners/ownerDetails";
	private static final String VIEWS_PET_DETAILS = "pets/petDetails";
	private static final String VIEWS_VISIT_CREATE_OR_UPDATE_FORM = "pets/createOrUpdateVisitForm";

	// Default page size for visit history
	private static final int DEFAULT_PAGE_SIZE = 10;
	// Default sort column for visit history
	private static final String DEFAULT_SORT_COLUMN = "date";
	// Default sort direction for visit history
	private static final String DEFAULT_SORT_DIRECTION = "desc";

	public VisitController(OwnerRepository owners, VisitService visitService) {
		this.owners = owners;
		this.visitService = visitService;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id", "*.id");
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals:
	 * - Make sure we always have fresh data
	 * - Since we do not use the session scope, make sure that Pet object always has an
	 * id (Even though id is not part of the form fields)
	 * @param petId
	 * @return Pet
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("ownerId") int ownerId,
								@PathVariable("petId") int petId,
								Map<String, Object> model) {
		Optional<Owner> optionalOwner = owners.findById(ownerId);
		Owner owner = optionalOwner
				.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));

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
		return LocalDate.now().plusDays(1);
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm() {
		return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@ModelAttribute Owner owner,
										@PathVariable int petId,
										@Valid Visit visit,
										BindingResult result,
										RedirectAttributes redirectAttributes) {
		if (visit.getDate() != null && !visit.getDate().isAfter(LocalDate.now())) {
			result.rejectValue("date", "typeMismatch.visitDate");
		}

		if (result.hasErrors()) {
			return VIEWS_VISIT_CREATE_OR_UPDATE_FORM;
		}

		owner.addVisit(petId, visit);
		this.owners.save(owner);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Handles the display of visit history for a specific pet, with pagination, sorting, and filtering.
	 *
	 * @param petId The ID of the pet.
	 * @param page The page number to display (0-based).
	 * @param pageSize The number of visits per page.
	 * @param sortColumn The column to sort by.
	 * @param sortDirection The direction of sorting (asc/desc).
	 * @param treatmentTags Filter by treatment tags.
	 * @param model The model to add attributes to.
	 * @return The name of the view to render.
	 */
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits")
	public String showVisitHistory(@PathVariable int ownerId,
										@PathVariable int petId,
										@RequestParam(value = "page", defaultValue = "0") int page,
										@RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
										@RequestParam(value = "sortColumn", defaultValue = "date") String sortColumn,
										@RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
										@RequestParam(value = "treatmentTags", required = false) List<String> treatmentTags,
										ModelMap model) {

		Owner owner = owners.findById(ownerId)
				.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException("Pet not found with id: " + petId);
		}

		// Fetch paginated, sorted, and filtered visits
		VisitHistoryPage visitHistoryPage = visitService.getVisitHistory(petId, page, pageSize, sortColumn, sortDirection, treatmentTags);

		// Convert UTC timestamps to local timezone for display
		visitHistoryPage.getContent().forEach(visit -> {
			if (visit.getCheckInTime() != null) {
				visit.setCheckInTime(visit.getCheckInTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
			}
			if (visit.getCheckOutTime() != null) {
				visit.setCheckOutTime(visit.getCheckOutTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
			}
		});

		model.addAttribute("owner", owner);
		model.addAttribute("pet", pet);
		model.addAttribute("visits", visitHistoryPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("totalPages", visitHistoryPage.getTotalPages());
		model.addAttribute("totalVisits", visitHistoryPage.getTotalElements());
		model.addAttribute("sortColumn", sortColumn);
		model.addAttribute("sortDirection", sortDirection);
		model.addAttribute("treatmentTags", treatmentTags);
		model.addAttribute("availablePageSizes", List.of(10, 25, 50));

		return "pets/visitHistory";
	}

	/**
	 * Endpoint for exporting visit history to CSV.
	 *
	 * @param petId The ID of the pet.
	 * @param model The model to add attributes to.
	 * @return The name of the view to render.
	 */
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/export/csv")
	public String exportVisitsToCsv(@PathVariable int ownerId,
										@PathVariable int petId,
										ModelMap model) {

		Owner owner = owners.findById(ownerId)
				.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new IllegalArgumentException("Pet not found with id: " + petId);
		}

		List<Visit> visits = visitService.getAllVisitsForPet(petId);

		// Convert UTC timestamps to local timezone for display in CSV
		visits.forEach(visit -> {
			if (visit.getCheckInTime() != null) {
				visit.setCheckInTime(visit.getCheckInTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
			}
			if (visit.getCheckOutTime() != null) {
				visit.setCheckOutTime(visit.getCheckOutTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
			}
		});

		// Prepare CSV data
		List<String[]> csvData = visits.stream().map(visit -> new String[] {
				String.valueOf(visit.getId()),
				visit.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
				// Assuming Vet Name can be retrieved from Visit or related entities if available
				"", // Placeholder for Vet Name
				"", // Placeholder for Status
				String.valueOf(visitService.calculateDurationInMinutes(visit)), // Duration in minutes
				"", // Placeholder for Diagnosis Code
				visit.getDescription() // Using description as a placeholder for Treatment Tags for now
		}).collect(Collectors.toList());

		// Add headers
		String[] headers = {"Visit ID", "Appointment Date", "Vet Name", "Status", "Duration (minutes)", "Diagnosis Code", "Treatment Tags", "Description"};
		model.addAttribute("headers", headers);
		model.addAttribute("csvData", csvData);
		model.addAttribute("filename", String.format("petclinic_visits_%s_%s.csv", pet.getName(), LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));

		return "csvView"; // This will be handled by a custom ViewResolver
	}

}
