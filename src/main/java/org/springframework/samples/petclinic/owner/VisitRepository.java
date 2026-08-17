package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Integer>, JpaSpecificationExecutor<Visit> {

    @Query("SELECT v FROM Visit v JOIN v.pet p JOIN p.owner o WHERE o.id = :ownerId")
    List<Visit> findByPetOwnerId(@Param("ownerId") Integer ownerId);

    @Query("SELECT v FROM Visit v JOIN v.pet p WHERE p.id = :petId")
    List<Visit> findByPetId(@Param("petId") Integer petId);

    @Query("SELECT v FROM Visit v WHERE v.veterinarian.id = :veterinarianId")
    List<Visit> findByVeterinarianId(@Param("veterinarianId") Integer veterinarianId);

    List<Visit> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Visit> findByStatus(VisitStatus status);

    @Query("SELECT v FROM Visit v JOIN v.pet p JOIN p.owner o WHERE o.id = :ownerId AND p.id = :petId")
    List<Visit> findByPetOwnerIdAndPetId(@Param("ownerId") Integer ownerId, @Param("petId") Integer petId);

    @Query("SELECT v FROM Visit v JOIN v.pet p JOIN p.owner o WHERE o.id = :ownerId AND v.veterinarian.id = :veterinarianId")
    List<Visit> findByPetOwnerIdAndVeterinarianId(@Param("ownerId") Integer ownerId, @Param("veterinarianId") Integer veterinarianId);

    @Query("SELECT v FROM Visit v JOIN v.pet p WHERE p.id = :petId AND v.veterinarian.id = :veterinarianId")
    List<Visit> findByPetIdAndVeterinarianId(@Param("petId") Integer petId, @Param("veterinarianId") Integer veterinarianId);

    @Query("SELECT v FROM Visit v JOIN v.pet p JOIN p.owner o WHERE o.id = :ownerId AND p.id = :petId AND v.veterinarian.id = :veterinarianId")
    List<Visit> findByPetOwnerIdAndPetIdAndVeterinarianId(@Param("ownerId") Integer ownerId, @Param("petId") Integer petId, @Param("veterinarianId") Integer veterinarianId);
}
