package com.example.petclinic.web;

import com.example.petclinic.model.Owner;
import com.example.petclinic.service.OwnerService;
import com.example.petclinic.service.dto.OwnerDetailsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public List<Owner> getAllOwners() {
        return ownerService.findAll();
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<OwnerDetailsDTO> getOwnerDetails(@PathVariable Long ownerId) {
        Optional<OwnerDetailsDTO> ownerDetails = ownerService.findOwnerDetailsById(ownerId);
        return ownerDetails.map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Owner createOwner(@RequestBody Owner owner) {
        return ownerService.save(owner);
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<Owner> updateOwner(@PathVariable Long ownerId, @RequestBody Owner owner) {
        return ownerService.findById(ownerId)
                .map(existingOwner -> {
                    existingOwner.setFirstName(owner.getFirstName());
                    existingOwner.setLastName(owner.getLastName());
                    existingOwner.setAddress(owner.getAddress());
                    existingOwner.setCity(owner.getCity());
                    existingOwner.setTelephone(owner.getTelephone());
                    return ResponseEntity.ok(ownerService.save(existingOwner));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long ownerId) {
        if (ownerService.findById(ownerId).isPresent()) {
            ownerService.deleteById(ownerId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}