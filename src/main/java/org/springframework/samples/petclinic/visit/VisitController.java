package org.springframework.samples.petclinic.visit;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing `Visit`s.
 */
@RestController
@RequestMapping("/api/visits")
public class VisitController {

	private final VisitService visitService;
	private final OwnerRepository ownerRepository;
	private final VetRepository vetRepository;

	public VisitController(VisitService visitService, OwnerRepository ownerRepository, VetRepository vetRepository) {
		this.visitService = visitService;
		this.ownerRepository = ownerRepository;
		this.vetRepository = vetRepository;
	}

	@GetMapping
	public ResponseEntity<List<Visit>> getFilteredVisits(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false) Integer petId,
			@RequestParam(required = false) Integer ownerId,
			@RequestParam(required = false) Integer vetId) {
		List<Visit> visits = visitService.findVisitsByCriteria(startDate, endDate, petId, ownerId, vetId);
		return ResponseEntity.ok(visits);
	}

	@PostMapping("/owners/{ownerId}/pets/{petId}")
	public ResponseEntity<Visit> createVisit(@PathVariable Integer ownerId, @PathVariable Integer petId, @RequestBody Visit visit) {
		Owner owner = ownerRepository.findById(ownerId).orElseThrow(() -> new RuntimeException("Owner not found"));
		Pet pet = owner.getPet(petId);
		if (pet == null) {
			throw new RuntimeException("Pet not found for owner");
		}
		visit.setPet(pet);
		if (visit.getVet() != null && visit.getVet().getId() != null) {
			Vet vet = vetRepository.findById(visit.getVet().getId()).orElseThrow(() -> new RuntimeException("Vet not found"));
			visit.setVet(vet);
		}
		visitService.saveVisit(visit);
		return ResponseEntity.ok(visit);
	}

	@GetMapping("/{visitId}")
	public ResponseEntity<Visit> getVisitById(@PathVariable Integer visitId) {
		Visit visit = visitService.findVisitById(visitId);
		if (visit == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(visit);
	}
}
