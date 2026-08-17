package com.example.petclinic.service;

import com.example.petclinic.model.Visit;
import com.example.petclinic.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VisitService {

    private final VisitRepository visitRepository;

    @Autowired
    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    public List<Visit> findAllVisits() {
        return visitRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Visit> findVisitById(Integer id) {
        return visitRepository.findById(id);
    }

    @Transactional
    public Visit saveVisit(Visit visit) {
        return visitRepository.save(visit);
    }

    @Transactional
    public void deleteVisit(Integer id) {
        visitRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Visit> findVisitsByPetId(Integer petId) {
        return visitRepository.findByPetId(petId);
    }

    @Transactional(readOnly = true)
    public List<Visit> findFilteredVisits(
            Integer petId,
            Integer ownerId,
            Integer veterinarianId,
            LocalDate startDate,
            LocalDate endDate,
            String descriptionKeyword
    ) {
        return visitRepository.findFilteredVisits(
                petId, ownerId, veterinarianId, startDate, endDate, descriptionKeyword
        );
    }
}