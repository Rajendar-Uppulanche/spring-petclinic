package com.example.petclinic.service;

import com.example.petclinic.model.Visit;
import com.example.petclinic.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
public class VisitService {

    private final VisitRepository visitRepository;

    @Autowired
    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    public Collection<Visit> findAllVisits() {
        return visitRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Visit> findVisitById(int visitId) {
        return visitRepository.findById(visitId);
    }

    @Transactional
    public void saveVisit(Visit visit) {
        visitRepository.save(visit);
    }

    @Transactional
    public void deleteVisit(Visit visit) {
        visitRepository.delete(visit);
    }

    @Transactional(readOnly = true)
    public Collection<Visit> findVisitsByPetId(Integer petId) {
        return visitRepository.findByPetId(petId);
    }

    @Transactional(readOnly = true)
    public Collection<Visit> findVisits(
            Integer petId,
            Integer ownerId,
            Integer veterinarianId,
            LocalDate startDate,
            LocalDate endDate,
            String descriptionKeyword) {
        return visitRepository.findFilteredVisits(petId, ownerId, veterinarianId, startDate, endDate, descriptionKeyword);
    }
}