package com.example.controller;

import com.example.model.Specialty;
import com.example.model.Vet;
import com.example.service.VetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class VetController {

    private final VetService vetService;

    @Autowired
    public VetController(VetService vetService) {
        this.vetService = vetService;
    }

    @GetMapping("/vets")
    public ResponseEntity<List<Vet>> getAllVets(@RequestParam(required = false) String specialty) {
        List<Vet> vets;
        if (specialty != null && !specialty.trim().isEmpty()) {
            vets = vetService.findVetsBySpecialty(specialty);
        } else {
            vets = vetService.findAllVets();
        }
        return ResponseEntity.ok(vets);
    }

    @GetMapping("/specialties")
    public ResponseEntity<Set<Specialty>> getAllDistinctSpecialties() {
        Set<Specialty> specialties = vetService.findAllDistinctSpecialties();
        return ResponseEntity.ok(specialties);
    }

    @GetMapping("/vets/{id}")
    public ResponseEntity<Vet> getVetById(@PathVariable Long id) {
        Vet vet = vetService.findVetById(id);
        if (vet != null) {
            return ResponseEntity.ok(vet);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/vets")
    public ResponseEntity<Vet> createVet(@RequestBody Vet vet) {
        Vet savedVet = vetService.saveVet(vet);
        return ResponseEntity.status(201).body(savedVet);
    }

    @PutMapping("/vets/{id}")
    public ResponseEntity<Vet> updateVet(@PathVariable Long id, @RequestBody Vet vet) {
        Vet existingVet = vetService.findVetById(id);
        if (existingVet != null) {
            vet.setId(id);
            Vet updatedVet = vetService.saveVet(vet);
            return ResponseEntity.ok(updatedVet);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/vets/{id}")
    public ResponseEntity<Void> deleteVet(@PathVariable Long id) {
        if (vetService.findVetById(id) != null) {
            vetService.deleteVet(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}