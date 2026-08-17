package com.example.petclinic.web;

import com.example.petclinic.model.Visit;
import com.example.petclinic.service.VisitService;
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

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping
    public ResponseEntity<List<Visit>> getVisits(
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(value = "petId", required = false) Long petId,
        @RequestParam(value = "ownerId", required = false) Long ownerId,
        @RequestParam(value = "veterinarianId", required = false) Long veterinarianId,
        @RequestParam(value = "descriptionKeyword", required = false) String descriptionKeyword
    ) {
        List<Visit> visits = visitService.findVisitsByCriteria(
            startDate, endDate, petId, ownerId, veterinarianId, descriptionKeyword
        );
        return new ResponseEntity<>(visits, HttpStatus.OK);
    }

    @GetMapping("/{visitId}")
    public ResponseEntity<Visit> getVisitById(@PathVariable("visitId") Long visitId) {
        Optional<Visit> visit = visitService.findById(visitId);
        return visit.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Visit> createVisit(@RequestBody Visit visit) {
        return new ResponseEntity<>(visitService.save(visit), HttpStatus.CREATED);
    }

    @PutMapping("/{visitId}")
    public ResponseEntity<Visit> updateVisit(@PathVariable("visitId") Long visitId, @RequestBody Visit visit) {
        if (!visitService.findById(visitId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visit.setId(visitId);
        return new ResponseEntity<>(visitService.save(visit), HttpStatus.OK);
    }

    @DeleteMapping("/{visitId}")
    public ResponseEntity<Void> deleteVisit(@PathVariable("visitId") Long visitId) {
        if (!visitService.findById(visitId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        visitService.deleteById(visitId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
