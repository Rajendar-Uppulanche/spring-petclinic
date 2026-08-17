package org.springframework.samples.petclinic.web;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.owner.VisitService;
import org.springframework.samples.petclinic.owner.VisitStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitRestController {

    private final VisitService visitService;

    public VisitRestController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping
    public ResponseEntity<List<Visit>> getFilteredVisits(
            @RequestParam(required = false) Integer ownerId,
            @RequestParam(required = false) Integer petId,
            @RequestParam(required = false) Integer veterinarianId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) VisitStatus status) {

        List<Visit> visits = visitService.findVisits(ownerId, petId, veterinarianId, startDate, endDate, status);
        return ResponseEntity.ok(visits);
    }
}
