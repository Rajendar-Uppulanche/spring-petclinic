package org.springframework.samples.petclinic.visit;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class VisitSpecifications {

    public static Specification<Visit> withCriteria(VisitFilterCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), criteria.getEndDate()));
            }
            if (StringUtils.hasText(criteria.getPetName())) {
                Join<Visit, Pet> petJoin = root.join("pet", JoinType.INNER);
                predicates.add(cb.like(cb.lower(petJoin.get("name")), "%" + criteria.getPetName().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(criteria.getOwnerLastName())) {
                Join<Visit, Pet> petJoin = root.join("pet", JoinType.INNER);
                Join<Pet, Owner> ownerJoin = petJoin.join("owner", JoinType.INNER);
                predicates.add(cb.like(cb.lower(ownerJoin.get("lastName")), "%" + criteria.getOwnerLastName().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}