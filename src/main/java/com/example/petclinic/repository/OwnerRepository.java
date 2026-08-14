package com.example.petclinic.repository;

import com.example.petclinic.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {
    // No specific changes needed for new fields, JPA handles them.
    // Potentially add custom queries if needed, but plan doesn't specify.
}