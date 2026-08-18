package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.vet.Vet;

/**
 * Service implementation for managing {@link Visit} instances.
 * Implements dynamic search and filter logic using JPA Specifications.
 *
 * @author Synapse Builder
 */
@Service
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;

    public VisitServiceImpl(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Visit> findVisits(VisitSearchCriteriaDTO criteria) {
        return visitRepository.findAll(createVisitSpecification(criteria));
    }

    @Override
    @Transactional(readOnly = true)
    public Visit findVisitById(int visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new IllegalArgumentException("Visit not found with id: " + visitId));
    }

    @Override
    @Transactional
    public void saveVisit(Visit visit) {
        visitRepository.save(visit);
    }

    @Override
    @Transactional
    public void deleteVisit(int visitId) {
        visitRepository.deleteById(visitId);
    }

    private Specification<Visit> createVisitSpecification(VisitSearchCriteriaDTO criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), criteria.getEndDate()));
            }
            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            if (StringUtils.hasText(criteria.getPetName())) {
                Join<Visit, Pet> petJoin = root.join("pet");
                predicates.add(cb.like(cb.lower(petJoin.get("name")), "%" + criteria.getPetName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getOwnerLastName())) {
                Join<Visit, Pet> petJoin = root.join("pet");
                Join<Pet, Owner> ownerJoin = petJoin.join("owner");
                predicates.add(cb.like(cb.lower(ownerJoin.get("lastName")), "%" + criteria.getOwnerLastName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(criteria.getVeterinarianFirstName()) || StringUtils.hasText(criteria.getVeterinarianLastName())) {
                Join<Visit, Vet> vetJoin = root.join("veterinarian");
                if (StringUtils.hasText(criteria.getVeterinarianFirstName())) {
                    predicates.add(cb.like(cb.lower(vetJoin.get("firstName")), "%" + criteria.getVeterinarianFirstName().toLowerCase() + "%"));
                }
                if (StringUtils.hasText(criteria.getVeterinarianLastName())) {
                    predicates.add(cb.like(cb.lower(vetJoin.get("lastName")), "%" + criteria.getVeterinarianLastName().toLowerCase() + "%"));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
