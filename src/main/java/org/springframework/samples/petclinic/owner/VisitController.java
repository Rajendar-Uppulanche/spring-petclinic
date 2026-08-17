package org.springframework.samples.petclinic.owner;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Dave Syer
 */
@Controller
class VisitController {

	private final VisitService visitService;
	private final OwnerRepository owners;
	private final VetRepository vets; // Inject VetRepository to get vets for dropdowns/filtering

	public VisitController(VisitService visitService, OwnerRepository owners, VetRepository vets) {
		this.visitService = visitService;
		this.owners = owners;
		this.vets = vets;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/**
	 * Called before every request to {@link VisitController}
	 * @param petId
	 * @param model
	 * @return
	 */
	@ModelAttribute("visit")
	public Visit loadPetWithVisit(@PathVariable("petId") int petId, Model model) {
		Pet pet = this.owners.findById(petId).orElseThrow(() -> new IllegalArgumentException("Pet not found"));
		model.addAttribute("pet", pet);
		Visit visit = new Visit();
		pet.addVisit(visit);
		return visit;
	}

	// -------------------------- NEW VISIT FORM --------------------------

	@GetMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String initNewVisitForm(@PathVariable("petId") int petId, Map<String, Object> model) {
		// loadPetWithVisit already adds the visit to the model
		return "pets/createOrUpdateVisitForm";
	}

	@PostMapping("/owners/{ownerId}/pets/{petId}/visits/new")
	public String processNewVisitForm(@Valid Visit visit, BindingResult result) {
		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}
		else {
			this.visitService.saveVisit(visit);
			return "redirect:/owners/{ownerId}";
		}
	}

	// -------------------------- FILTERED VISITS API --------------------------

	@GetMapping("/api/visits")
	@ResponseBody
	public ResponseEntity<Page<Visit>> getFilteredVisits(
			@RequestParam(required = false) Integer ownerId,
			@RequestParam(required = false) Integer petId,
			@RequestParam(required = false) Integer vetId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
			@RequestParam(required = false) String description,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		Page<Visit> visits = visitService.findVisits(ownerId, petId, vetId, startDate, endDate, description, pageable);
		return ResponseEntity.ok(visits);
	}

	// Endpoint to get all vets for dropdowns
	@GetMapping("/api/vets")
	@ResponseBody
	public ResponseEntity<List<Vet>> getAllVets() {
		return ResponseEntity.ok(vets.findAll().stream().toList());
	}
}