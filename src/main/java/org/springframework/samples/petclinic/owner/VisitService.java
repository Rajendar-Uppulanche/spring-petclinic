package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class VisitService {

    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    public List<Visit> findFilteredVisits(Integer petId, Integer ownerId, Integer veterinarianId,
                                          LocalDate startDate, LocalDate endDate, String description) {
        return visitRepository.findFilteredVisits(petId, ownerId, veterinarianId, startDate, endDate, description);
    }

    @Transactional(readOnly = true)
    public Visit findVisitById(Integer id) {
        return visitRepository.findById(id).orElse(null);
    }

    @Transactional
    public void saveVisit(Visit visit) {
        visitRepository.save(visit);
    }

    @Transactional(readOnly = true)
    public List<Visit> findAllVisits() {
        return visitRepository.findAll();
    }
}