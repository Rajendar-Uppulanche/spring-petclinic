package com.example.petclinic.web;

import com.example.petclinic.model.Owner;
import com.example.petclinic.service.OwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public ResponseEntity<Collection<Owner>> getAllOwners() {
        Collection<Owner> owners = ownerService.findOwnersByLastName(""); // Find all owners
        if (owners.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(owners, HttpStatus.OK);
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<Owner> getOwner(@PathVariable("ownerId") int ownerId) {
        Optional<Owner> owner = ownerService.findOwnerById(ownerId);
        return owner.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-user-id/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')") // NFR-070: Authorized staff roles only
    public ResponseEntity<Owner> getOwnerByUserId(@PathVariable("userId") String userId) {
        Optional<Owner> owner = ownerService.findOwnerByUserId(userId);
        return owner.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Owner> createOwner(@Valid @RequestBody Owner owner, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            Owner savedOwner = ownerService.saveOwner(owner);
            return new ResponseEntity<>(savedOwner, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // For userId uniqueness violation
        }
    }

    @PutMapping("/{ownerId}")
    public ResponseEntity<Owner> updateOwner(@PathVariable("ownerId") int ownerId, @Valid @RequestBody Owner owner, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || !ownerService.findOwnerById(ownerId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        owner.setId(ownerId); // Ensure the ID from path is used
        try {
            Owner savedOwner = ownerService.saveOwner(owner);
            return new ResponseEntity<>(savedOwner, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // For userId immutability/uniqueness violation
        }
    }

    @DeleteMapping("/{ownerId}")
    public ResponseEntity<Void> deleteOwner(@PathVariable("ownerId") int ownerId) {
        Optional<Owner> owner = ownerService.findOwnerById(ownerId);
        if (owner.isPresent()) {
            ownerService.deleteOwner(owner.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}