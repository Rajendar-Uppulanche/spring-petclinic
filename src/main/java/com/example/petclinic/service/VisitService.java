package com.example.petclinic.service;

import com.example.petclinic.model.Visit;
import com.example.petclinic.repository.VisitRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VisitService {

    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public List<Visit> findAll() {
        return visitRepository.findAll();
    }

    public Optional<Visit> findById(Long id) {
        return visitRepository.findById(id);
    }

    public Visit save(Visit visit) {
        return visitRepository.save(visit);
    }

    public void deleteById(Long id) {
        visitRepository.deleteById(id);
    }

    public List<Visit> findVisitsByCriteria(
        LocalDate startDate,
        LocalDate endDate,
        Long petId,
        Long ownerId,
        Long veterinarianId,
        String descriptionKeyword
    ) {
        return visitRepository.findVisitsByCriteria(startDate, endDate, petId, ownerId, veterinarianId, descriptionKeyword);
    }
}
