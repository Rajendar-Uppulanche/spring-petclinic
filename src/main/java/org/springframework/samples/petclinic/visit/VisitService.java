package org.springframework.samples.petclinic.visit;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for `Visit` management.
 */
@Service
public class VisitService {

	private final VisitRepository visitRepository;

	public VisitService(VisitRepository visitRepository) {
		this.visitRepository = visitRepository;
	}

	@Transactional(readOnly = true)
	public List<Visit> findAllVisits() {
		return visitRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<Visit> findVisitsByCriteria(LocalDate startDate, LocalDate endDate, Integer petId, Integer ownerId, Integer vetId) {
		return visitRepository.findVisitsByCriteria(startDate, endDate, petId, ownerId, vetId);
	}

	@Transactional
	public void saveVisit(Visit visit) {
		visitRepository.save(visit);
	}

	@Transactional(readOnly = true)
	public Visit findVisitById(Integer visitId) {
		return visitRepository.findById(visitId).orElse(null);
	}
}
