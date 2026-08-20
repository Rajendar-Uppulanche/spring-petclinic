package com.example.petclinic.web;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.Visit;
import com.example.petclinic.service.OwnerService;
import com.example.petclinic.service.PetService;
import com.example.petclinic.service.VisitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/owners/{ownerId}/pets/{petId}/visits")
public class VisitController {

    private final VisitService visitService;
    private final PetService petService;
    private final OwnerService ownerService;

    public VisitController(VisitService visitService, PetService petService, OwnerService ownerService) {
        this.visitService = visitService;
        this.petService = petService;
        this.ownerService = ownerService;
    }

    @GetMapping
    public ResponseEntity<Collection<Visit>> getVisits(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId) {
        Optional<Pet> pet = petService.findPetById(petId);
        if (!pet.isPresent() || !pet.get().getOwner().getId().equals(ownerId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Access owner's userId if needed, e.g., for logging or specific business logic
        String ownerUserId = pet.get().getOwner().getUserId();
        System.out.println("Owner's User ID for this visit: " + ownerUserId);

        Collection<Visit> visits = visitService.findVisitsByPetId(petId);
        if (visits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Visit> createVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, @Valid @RequestBody Visit visit, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Pet> pet = petService.findPetById(petId);
        if (!pet.isPresent() || !pet.get().getOwner().getId().equals(ownerId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visit.setPet(pet.get());
        Visit savedVisit = visitService.saveVisit(visit);
        return new ResponseEntity<>(savedVisit, HttpStatus.CREATED);
    }

    @PutMapping("/{visitId}")
    public ResponseEntity<Visit> updateVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, @PathVariable("visitId") int visitId, @Valid @RequestBody Visit visit, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || !visitService.findVisitById(visitId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Pet> pet = petService.findPetById(petId);
        if (!pet.isPresent() || !pet.get().getOwner().getId().equals(ownerId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visit.setId(visitId);
        visit.setPet(pet.get());
        Visit updatedVisit = visitService.saveVisit(visit);
        return new ResponseEntity<>(updatedVisit, HttpStatus.OK);
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> deleteVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, @PathVariable("visitId") int visitId) {
        Optional<Visit> visit = visitService.findVisitById(visitId);
        if (!visit.isPresent() || !visit.get().getPet().getId().equals(petId) || !visit.get().getPet().getOwner().getId().equals(ownerId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visitService.deleteVisit(visit.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}