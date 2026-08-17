package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.samples.petclinic.vet.Veterinarian;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

    @Query("SELECT visit FROM Visit visit WHERE visit.pet.id = :petId")
    List<Visit> findByPetId(@Param("petId") Integer petId);

    @Query("SELECT v FROM Visit v " +
           "LEFT JOIN v.pet p " +
           "LEFT JOIN p.owner o " +
           "LEFT JOIN v.veterinarian vet " +
           "WHERE (:petId IS NULL OR p.id = :petId) " +
           "AND (:ownerId IS NULL OR o.id = :ownerId) " +
           "AND (:veterinarianId IS NULL OR vet.id = :veterinarianId) " +
           "AND (:startDate IS NULL OR v.date >= :startDate) " +
           "AND (:endDate IS NULL OR v.date <= :endDate) " +
           "AND (:description IS NULL OR LOWER(v.description) LIKE LOWER(CONCAT('%', :description, '%')))")
    List<Visit> findFilteredVisits(
            @Param("petId") Integer petId,
            @Param("ownerId") Integer ownerId,
            @Param("veterinarianId") Integer veterinarianId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("description") String description);
}