package com.example.petclinic.web;

import com.example.petclinic.model.Visit;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Veterinarian;
import com.example.petclinic.service.VisitService;
import com.example.petclinic.service.PetService;
import com.example.petclinic.service.OwnerService;
import com.example.petclinic.service.VeterinarianService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;
    private final PetService petService;
    private final OwnerService ownerService;
    private final VeterinarianService veterinarianService;

    @Autowired
    public VisitController(VisitService visitService, PetService petService, OwnerService ownerService, VeterinarianService veterinarianService) {
        this.visitService = visitService;
        this.petService = petService;
        this.ownerService = ownerService;
        this.veterinarianService = veterinarianService;
    }

    @GetMapping
    public ResponseEntity<List<Visit>> getAllVisits(
            @RequestParam(required = false) Integer petId,
            @RequestParam(required = false) Integer ownerId,
            @RequestParam(required = false) Integer veterinarianId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String description
    ) {
        List<Visit> visits = visitService.findFilteredVisits(petId, ownerId, veterinarianId, startDate, endDate, description);
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Visit> getVisitById(@PathVariable Integer id) {
        Optional<Visit> visit = visitService.findVisitById(id);
        return visit.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Visit> createVisit(@RequestBody Visit visit) {
        Visit savedVisit = visitService.saveVisit(visit);
        return new ResponseEntity<>(savedVisit, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Visit> updateVisit(@PathVariable Integer id, @RequestBody Visit visit) {
        if (!visitService.findVisitById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visit.setId(id);
        Visit updatedVisit = visitService.saveVisit(visit);
        return new ResponseEntity<>(updatedVisit, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable Integer id) {
        if (!visitService.findVisitById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visitService.deleteVisit(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/pets")
    public ResponseEntity<List<Pet>> getAllPets() {
        return new ResponseEntity<>(petService.findAllPets(), HttpStatus.OK);
    }

    @GetMapping("/owners")
    public ResponseEntity<List<Owner>> getAllOwners() {
        return new ResponseEntity<>(ownerService.findAllOwners(), HttpStatus.OK);
    }

    @GetMapping("/veterinarians")
    public ResponseEntity<List<Veterinarian>> getAllVeterinarians() {
        return new ResponseEntity<>(veterinarianService.findAllVeterinarians(), HttpStatus.OK);
    }
}