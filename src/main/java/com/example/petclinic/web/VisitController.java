package com.example.petclinic.web;

import com.example.petclinic.model.Visit;
import com.example.petclinic.service.VisitService;
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

    @Autowired
    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping
    public ResponseEntity<List<Visit>> getAllVisits(
            @RequestParam(required = false) Integer petId,
            @RequestParam(required = false) Integer ownerId,
            @RequestParam(required = false) Integer veterinarianId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String descriptionKeyword
    ) {
        List<Visit> visits = visitService.findFilteredVisits(
                petId, ownerId, veterinarianId, startDate, endDate, descriptionKeyword
        );
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }

    @GetMapping("/{visitId}")
    public ResponseEntity<Visit> getVisitById(@PathVariable("visitId") Integer visitId) {
        Optional<Visit> visit = visitService.findVisitById(visitId);
        return visit.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Visit> createVisit(@RequestBody Visit visit) {
        Visit savedVisit = visitService.saveVisit(visit);
        return new ResponseEntity<>(savedVisit, HttpStatus.CREATED);
    }

    @PutMapping("/{visitId}")
    public ResponseEntity<Visit> updateVisit(@PathVariable("visitId") Integer visitId, @RequestBody Visit visit) {
        if (!visitService.findVisitById(visitId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visit.setId(visitId);
        Visit updatedVisit = visitService.saveVisit(visit);
        return new ResponseEntity<>(updatedVisit, HttpStatus.OK);
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> deleteVisit(@PathVariable("visitId") Integer visitId) {
        if (!visitService.findVisitById(visitId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visitService.deleteVisit(visitId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}