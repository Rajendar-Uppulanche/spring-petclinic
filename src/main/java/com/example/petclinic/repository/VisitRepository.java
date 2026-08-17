package com.example.petclinic.repository;

import com.example.petclinic.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

    List<Visit> findByPetId(Integer petId);

    @Query("SELECT v FROM Visit v WHERE " +
           "(:petId IS NULL OR v.pet.id = :petId) AND " +
           "(:ownerId IS NULL OR v.pet.owner.id = :ownerId) AND " +
           "(:veterinarianId IS NULL OR v.veterinarian.id = :veterinarianId) AND " +
           "(:startDate IS NULL OR v.date >= :startDate) AND " +
           "(:endDate IS NULL OR v.date <= :endDate) AND " +
           "(:descriptionLike IS NULL OR LOWER(v.description) LIKE LOWER(CONCAT('%', :descriptionLike, '%')))")
    List<Visit> findFilteredVisits(
            @Param("petId") Integer petId,
            @Param("ownerId") Integer ownerId,
            @Param("veterinarianId") Integer veterinarianId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("descriptionLike") String descriptionLike
    );
}