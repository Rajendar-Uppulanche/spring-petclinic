package com.example.petclinic.service;

import com.example.petclinic.model.Owner;
import com.example.petclinic.repository.OwnerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Transactional(readOnly = true)
    public Collection<Owner> findOwnersByLastName(String lastName) {
        return ownerRepository.findByLastName(lastName);
    }

    @Transactional(readOnly = true)
    public Optional<Owner> findOwnerById(int id) {
        return ownerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Owner> findOwnerByUserId(String userId) {
        return ownerRepository.findByUserId(userId);
    }

    @Transactional
    public Owner saveOwner(Owner owner) {
        // Enforce uniqueness of userId if provided
        if (owner.getUserId() != null) {
            Optional<Owner> existingOwnerWithUserId = ownerRepository.findByUserId(owner.getUserId());
            if (existingOwnerWithUserId.isPresent() && !existingOwnerWithUserId.get().getId().equals(owner.getId())) {
                throw new IllegalArgumentException("An owner with this user ID already exists.");
            }
        }
        return ownerRepository.save(owner);
    }

    @Transactional
    public void deleteOwner(Owner owner) {
        ownerRepository.delete(owner);
    }
}