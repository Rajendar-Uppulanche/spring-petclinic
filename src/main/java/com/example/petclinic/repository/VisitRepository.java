package com.example.petclinic.repository;

import com.example.petclinic.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long>, VisitRepositoryCustom {
    List<Visit> findByPetId(Long petId);
}
