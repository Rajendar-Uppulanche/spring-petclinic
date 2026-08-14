package com.example.petclinic.controller;

import com.example.petclinic.dto.OwnerContactInfoDTO;
import com.example.petclinic.dto.PreventiveCareDetailsDTO;
import com.example.petclinic.dto.VisitRequestDTO;
import com.example.petclinic.dto.VisitResponseDTO;
import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PreventiveCareDetails;
import com.example.petclinic.model.Visit;
import com.example.petclinic.model.VisitType;
import com.example.petclinic.service.PetService;
import com.example.petclinic.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;
    private final PetService petService;

    @Autowired
    public VisitController(VisitService visitService, PetService petService) {
        this.visitService = visitService;
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<VisitResponseDTO> createVisit(@RequestBody VisitRequestDTO visitRequestDTO) {
        try {
            Visit visit = convertToEntity(visitRequestDTO);
            Visit savedVisit = visitService.saveVisit(visit);
            return new ResponseEntity<>(convertToResponseDTO(savedVisit), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/{visitId}")
    public ResponseEntity<VisitResponseDTO> updateVisit(@PathVariable Integer visitId, @RequestBody VisitRequestDTO visitRequestDTO) {
        if (!visitId.equals(visitRequestDTO.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Visit ID in path and body do not match.");
        }
        try {
            Visit existingVisit = visitService.findById(visitId)
                .orElseThrow(() -> new NoSuchElementException("Visit with ID " + visitId + " not found."));

            existingVisit.setVisitDate(visitRequestDTO.getVisitDate());
            existingVisit.setDescription(visitRequestDTO.getDescription());
            existingVisit.setVisitType(visitRequestDTO.getVisitType());
            existingVisit.setPreventiveCareDetails(convertToPreventiveCareDetailsEntity(visitRequestDTO.getPreventiveCareDetails()));

            Visit updatedVisit = visitService.saveVisit(existingVisit);
            return ResponseEntity.ok(convertToResponseDTO(updatedVisit));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{visitId}")
    public ResponseEntity<VisitResponseDTO> getVisitDetails(@PathVariable Integer visitId) {
        try {
            VisitService.VisitDetailsWithOwnerInfo details = visitService.getVisitDetailsWithOwnerInfo(visitId);
            return ResponseEntity.ok(convertToResponseDTOWithOwnerInfo(details.getVisit(), details.getOwner()));
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Visit convertToEntity(VisitRequestDTO dto) {
        Visit visit = new Visit();
        if (dto.getId() != null) {
            visit.setId(dto.getId());
        }
        visit.setVisitDate(dto.getVisitDate());
        visit.setDescription(dto.getDescription());
        visit.setVisitType(dto.getVisitType());
        visit.setPreventiveCareDetails(convertToPreventiveCareDetailsEntity(dto.getPreventiveCareDetails()));

        Pet pet = petService.findById(dto.getPetId())
            .orElseThrow(() -> new NoSuchElementException("Pet with ID " + dto.getPetId() + " not found."));
        visit.setPet(pet);
        return visit;
    }

    private PreventiveCareDetails convertToPreventiveCareDetailsEntity(PreventiveCareDetailsDTO dto) {
        if (dto == null) {
            return null;
        }
        PreventiveCareDetails details = new PreventiveCareDetails();
        details.setVaccineName(dto.getVaccineName());
        details.setDosage(dto.getDosage());
        details.setNextDueDate(dto.getNextDueDate());
        return details;
    }

    private VisitResponseDTO convertToResponseDTO(Visit visit) {
        VisitResponseDTO dto = new VisitResponseDTO();
        dto.setId(visit.getId());
        dto.setPetId(visit.getPet().getId());
        dto.setPetName(visit.getPet().getName());
        dto.setVisitDate(visit.getVisitDate());
        dto.setDescription(visit.getDescription());
        dto.setVisitType(visit.getVisitType());
        dto.setPreventiveCareDetails(convertToPreventiveCareDetailsDTO(visit.getPreventiveCareDetails()));
        return dto;
    }

    private VisitResponseDTO convertToResponseDTOWithOwnerInfo(Visit visit, Owner owner) {
        VisitResponseDTO dto = convertToResponseDTO(visit);
        OwnerContactInfoDTO ownerInfoDTO = new OwnerContactInfoDTO();
        ownerInfoDTO.setOwnerId(owner.getId());
        ownerInfoDTO.setFirstName(owner.getFirstName());
        ownerInfoDTO.setLastName(owner.getLastName());
        ownerInfoDTO.setTelephone(owner.getTelephone());
        ownerInfoDTO.setEmail(owner.getEmail());
        dto.setOwnerContactInfo(ownerInfoDTO);
        return dto;
    }

    private PreventiveCareDetailsDTO convertToPreventiveCareDetailsDTO(PreventiveCareDetails entity) {
        if (entity == null) {
            return null;
        }
        PreventiveCareDetailsDTO dto = new PreventiveCareDetailsDTO();
        dto.setVaccineName(entity.getVaccineName());
        dto.setDosage(entity.getDosage());
        dto.setNextDueDate(entity.getNextDueDate());
        return dto;
    }
}