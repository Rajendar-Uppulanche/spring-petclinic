package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VisitService {

    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    public List<Visit> findVisits(Integer ownerId, Integer petId, Integer veterinarianId, LocalDate startDate, LocalDate endDate, VisitStatus status) {
        return visitRepository.findAll((Specification<Visit>) (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (ownerId != null) {
                Join<Visit, Pet> petJoin = root.join("pet");
                Join<Pet, Owner> ownerJoin = petJoin.join("owner");
                predicates.add(cb.equal(ownerJoin.get("id"), ownerId));
            }
            if (petId != null) {
                Join<Visit, Pet> petJoin = root.join("pet");
                predicates.add(cb.equal(petJoin.get("id"), petId));
            }
            if (veterinarianId != null) {
                predicates.add(cb.equal(root.get("veterinarian").get("id"), veterinarianId));
            }
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("date"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
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
}
