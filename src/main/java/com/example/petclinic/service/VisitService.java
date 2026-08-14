package com.example.petclinic.service;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PreventiveCareDetails;
import com.example.petclinic.model.Visit;
import com.example.petclinic.model.VisitType;
import com.example.petclinic.repository.OwnerRepository;
import com.example.petclinic.repository.PetRepository;
import com.example.petclinic.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final PetRepository petRepository;
    private final OwnerRepository ownerRepository;

    @Autowired
    public VisitService(VisitRepository visitRepository, PetRepository petRepository, OwnerRepository ownerRepository) {
        this.visitRepository = visitRepository;
        this.petRepository = petRepository;
        this.ownerRepository = ownerRepository;
    }

    @Transactional
    public Visit saveVisit(Visit visit) {
        // BR-004: Validate preventive care details if visit type is PREVENTIVE
        if (visit.getVisitType() == VisitType.PREVENTIVE) {
            PreventiveCareDetails details = visit.getPreventiveCareDetails();
            if (details == null || details.getVaccineName() == null || details.getVaccineName().isEmpty() ||
                details.getDosage() == null || details.getDosage().isEmpty() ||
                details.getNextDueDate() == null) {
                throw new IllegalArgumentException("Preventive care details (vaccine name, dosage, next due date) are required for PREVENTIVE visits.");
            }
        } else {
            // Ensure preventive care details are null for non-preventive visits
            visit.setPreventiveCareDetails(null);
        }
        return visitRepository.save(visit);
    }

    @Transactional(readOnly = true)
    public Optional<Visit> findById(Integer id) {
        return visitRepository.findById(id);
    }

    // New method to retrieve visit details with owner contact information
    @Transactional(readOnly = true)
    public VisitDetailsWithOwnerInfo getVisitDetailsWithOwnerInfo(Integer visitId) {
        Optional<Visit> visitOptional = visitRepository.findById(visitId);
        if (visitOptional.isEmpty()) {
            throw new NoSuchElementException("Visit with ID " + visitId + " not found.");
        }

        Visit visit = visitOptional.get();
        Pet pet = visit.getPet();
        if (pet == null) {
            throw new IllegalStateException("Visit with ID " + visitId + " has no associated pet.");
        }
        Owner owner = pet.getOwner();
        if (owner == null) {
            throw new IllegalStateException("Pet with ID " + pet.getId() + " has no associated owner.");
        }

        return new VisitDetailsWithOwnerInfo(visit, owner);
    }

    // Helper class to combine Visit and Owner info for DTO mapping
    public static class VisitDetailsWithOwnerInfo {
        private final Visit visit;
        private final Owner owner;

        public VisitDetailsWithOwnerInfo(Visit visit, Owner owner) {
            this.visit = visit;
            this.owner = owner;
        }

        public Visit getVisit() {
            return visit;
        }

        public Owner getOwner() {
            return owner;
        }
    }
}