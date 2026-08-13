package com.example.repository;

import com.example.model.Specialty;
import com.example.model.Vet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VetRepository extends JpaRepository<Vet, Long> {

    @Query("SELECT DISTINCT v FROM Vet v JOIN v.specialties s WHERE s.name = :specialtyName")
    List<Vet> findBySpecialtyName(@Param("specialtyName") String specialtyName);

    @Query("SELECT DISTINCT s FROM Vet v JOIN v.specialties s")
    Set<Specialty> findAllDistinctSpecialties();
}