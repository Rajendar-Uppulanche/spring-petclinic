package org.springframework.samples.petclinic.visit;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {

    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    public List<Visit> findVisitsByCriteria(VisitFilterCriteria criteria) {
        return visitRepository.findAll(VisitSpecifications.withCriteria(criteria));
    }

    public void saveVisit(Visit visit) {
        visitRepository.save(visit);
    }

    public List<Visit> findAllVisits() {
        return visitRepository.findAll();
    }
}