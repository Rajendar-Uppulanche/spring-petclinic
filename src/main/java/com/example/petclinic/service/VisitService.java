package com.example.petclinic.service;

import com.example.petclinic.model.Visit;
import com.example.petclinic.repository.VisitRepository;
import com.example.petclinic.service.dto.PetVisitCountDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VisitService {

    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    public List<Visit> findAll() {
        return visitRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Visit> findById(Long id) {
        return visitRepository.findById(id);
    }

    @Transactional
    public Visit save(Visit visit) {
        return visitRepository.save(visit);
    }

    @Transactional
    public void deleteById(Long id) {
        visitRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PetVisitCountDTO> countVisitsByPetForOwner(Long ownerId) {
        return visitRepository.countVisitsByPetForOwner(ownerId);
    }
}