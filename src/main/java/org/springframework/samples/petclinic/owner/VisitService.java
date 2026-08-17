package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.samples.petclinic.vet.Vet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final OwnerRepository ownerRepository;

    public VisitService(VisitRepository visitRepository, OwnerRepository ownerRepository) {
        this.visitRepository = visitRepository;
        this.ownerRepository = ownerRepository;
    }

    @Transactional(readOnly = true)
    public Page<Visit> findVisits(Integer ownerId, Integer petId, Integer vetId, LocalDate startDate, LocalDate endDate, String description, Pageable pageable) {
        return visitRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (ownerId != null) {
                // Find pets belonging to this owner
                Owner owner = ownerRepository.findById(ownerId).orElse(null);
                if (owner != null && !owner.getPets().isEmpty()) {
                    List<Integer> petIds = owner.getPets().stream().map(Pet::getId).toList();
                    predicates.add(root.get("pet").get("id").in(petIds));
                } else {
                    // If owner has no pets or owner not found, no visits match
                    predicates.add(cb.disjunction()); // Ensures no results
                }
            }

            if (petId != null) {
                predicates.add(cb.equal(root.get("pet").get("id"), petId));
            }

            if (vetId != null) {
                predicates.add(cb.equal(root.get("vet").get("id"), vetId));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            if (StringUtils.hasText(description)) {
                predicates.add(cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Transactional
    public void saveVisit(Visit visit) {
        visitRepository.save(visit);
    }

    @Transactional(readOnly = true)
    public Visit findVisitById(Integer visitId) {
        return visitRepository.findById(visitId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Visit> findVisitsByPetId(Integer petId) {
        return visitRepository.findByPetId(petId);
    }
}