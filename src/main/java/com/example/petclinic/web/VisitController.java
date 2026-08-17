package com.example.petclinic.web;

import com.example.petclinic.model.Visit;
import com.example.petclinic.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;

    @Autowired
    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping
    public ResponseEntity<Collection<Visit>> getAllVisits() {
        Collection<Visit> visits = visitService.findAllVisits();
        if (visits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }

    @GetMapping("/{visitId}")
    public ResponseEntity<Visit> getVisitById(@PathVariable("visitId") int visitId) {
        Optional<Visit> visit = visitService.findVisitById(visitId);
        return visit.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Visit> addVisit(@Valid @RequestBody Visit visit) {
        visitService.saveVisit(visit);
        return new ResponseEntity<>(visit, HttpStatus.CREATED);
    }

    @PutMapping("/{visitId}")
    public ResponseEntity<Visit> updateVisit(@PathVariable("visitId") int visitId, @Valid @RequestBody Visit visit) {
        Optional<Visit> currentVisit = visitService.findVisitById(visitId);
        if (currentVisit.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visit.setId(visitId);
        visitService.saveVisit(visit);
        return new ResponseEntity<>(visit, HttpStatus.OK);
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> deleteVisit(@PathVariable("visitId") int visitId) {
        Optional<Visit> visit = visitService.findVisitById(visitId);
        if (visit.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visitService.deleteVisit(visit.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/filtered")
    public ResponseEntity<Collection<Visit>> getFilteredVisits(
            @RequestParam(required = false) Integer petId,
            @RequestParam(required = false) Integer ownerId,
            @RequestParam(required = false) Integer veterinarianId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String descriptionKeyword) {

        Collection<Visit> visits = visitService.findVisits(
                petId, ownerId, veterinarianId, startDate, endDate, descriptionKeyword);

        if (visits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }
}