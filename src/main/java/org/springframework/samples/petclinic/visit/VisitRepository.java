package org.springframework.samples.petclinic.visit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VisitRepository extends Repository<Visit, Integer> {

    void save(Visit visit);

    @Query("SELECT visit FROM Visit visit WHERE visit.pet.id = :petId")
    List<Visit> findByPetId(@Param("petId") Integer petId);

    @Query("SELECT visit FROM Visit visit WHERE visit.pet.id = :petId")
    Page<Visit> findByPetId(@Param("petId") Integer petId, Pageable pageable);

    @Query("SELECT visit FROM Visit visit WHERE visit.id = :visitId")
    @Transactional(readOnly = true)
    Visit findById(@Param("visitId") Integer visitId);
}
