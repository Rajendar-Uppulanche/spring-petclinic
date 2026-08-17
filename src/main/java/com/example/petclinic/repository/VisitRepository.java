package com.example.petclinic.repository;

import com.example.petclinic.model.Visit;
import com.example.petclinic.service.dto.PetVisitCountDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    // Custom query to get visit counts for all pets belonging to a specific owner
    @Query("SELECT new com.example.petclinic.service.dto.PetVisitCountDTO(v.pet.id, COUNT(v.id)) " +
           "FROM Visit v WHERE v.pet.owner.id = :ownerId GROUP BY v.pet.id")
    List<PetVisitCountDTO> countVisitsByPetForOwner(@Param("ownerId") Long ownerId);
}