package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// This file is inferred and created based on usage in provided controllers and tests.
// It includes the new method for NFR-062.
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    @Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
    Page<Owner> findByLastName(@Param("lastName") String lastName, Pageable pageable);

    @Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id = :id")
    @Transactional(readOnly = true)
    Optional<Owner> findById(@Param("id") Integer id);

    // New method to fetch owner with pets and their visits eagerly to avoid N+1 for visit counts (NFR-062)
    @Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.pets pet LEFT JOIN FETCH pet.visits WHERE owner.id = :id")
    @Transactional(readOnly = true)
    Optional<Owner> findOwnerWithPetsAndVisits(@Param("id") Integer id);

    Page<Owner> findByLastNameStartingWith(String lastName, Pageable pageable);

}
