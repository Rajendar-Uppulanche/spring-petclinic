package com.example.petclinic.repository;

import com.example.petclinic.model.Visit;
import java.time.LocalDate;
import java.util.List;

public interface VisitRepositoryCustom {
    List<Visit> findVisitsByCriteria(
        LocalDate startDate,
        LocalDate endDate,
        Long petId,
        Long ownerId,
        Long veterinarianId,
        String descriptionKeyword
    );
}
