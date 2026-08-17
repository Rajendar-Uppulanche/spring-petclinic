package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.samples.petclinic.vet.Veterinarian;
import org.springframework.samples.petclinic.vet.VeterinarianRepository;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 * @author Wick Dynex
 * @author Synapse Builder
 */
@Controller
class VisitController {

	private final OwnerRepository owners;
	private final VisitService visitService;
	private final VeterinarianRepository veterinarianRepository;

	public VisitController(OwnerRepository owners, VisitService visitService, VeterinarianRepository veterinarianRepository) {
		this.owners = owners;
		this.visitService = visitService;
		this.veterinarianRepository = veterinarianRepository;
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
		visit.setPet(pet);
		return visit;
	}

	@ModelAttribute("minVisitDate")
	public LocalDate minVisitDate() {
		return LocalDate.now().plusDays(1);
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm(Map<String, Object> model) {
		List<Veterinarian> veterinarians = veterinarianRepository.findAll();
		model.put("veterinarians", veterinarians);
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
		this.visitService.saveVisit(visit);
		redirectAttributes.addFlashAttribute("message", "Your visit has been booked");
		return "redirect:/owners/{ownerId}";
	}

	/**
	 * API endpoint for retrieving filtered visits.
	 * This endpoint is designed to be consumed by a frontend application.
	 * @param petId Optional pet ID for filtering
	 * @param ownerId Optional owner ID for filtering
	 * @param veterinarianId Optional veterinarian ID for filtering
	 * @param startDate Optional start date for filtering
	 * @param endDate Optional end date for filtering
	 * @param description Optional description keyword for filtering
	 * @return A list of visits matching the criteria
	 */
	@GetMapping("/api/visits")
	@ResponseBody
	public ResponseEntity<List<Visit>> getFilteredVisits(
			@RequestParam(required = false) Integer petId,
			@RequestParam(required = false) Integer ownerId,
			@RequestParam(required = false) Integer veterinarianId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			@RequestParam(required = false) String description) {

		List<Visit> visits = visitService.findFilteredVisits(petId, ownerId, veterinarianId, startDate, endDate, description);
		return ResponseEntity.ok(visits);
	}

	/**
	 * API endpoint to get all veterinarians for dropdowns in frontend.
	 * @return A list of all veterinarians.
	 */
	@GetMapping("/api/veterinarians")
	@ResponseBody
	public ResponseEntity<List<Veterinarian>> getAllVeterinarians() {
		List<Veterinarian> veterinarians = veterinarianRepository.findAll();
		return ResponseEntity.ok(veterinarians);
	}
}