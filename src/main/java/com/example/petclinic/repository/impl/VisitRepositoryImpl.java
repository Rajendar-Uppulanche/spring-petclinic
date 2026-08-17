package com.example.petclinic.repository.impl;

import com.example.petclinic.model.Visit;
import com.example.petclinic.repository.VisitRepositoryCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VisitRepositoryImpl implements VisitRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Visit> findVisitsByCriteria(
        LocalDate startDate,
        LocalDate endDate,
        Long petId,
        Long ownerId,
        Long veterinarianId,
        String descriptionKeyword
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Visit> query = cb.createQuery(Visit.class);
        Root<Visit> visit = query.from(Visit.class);

        List<Predicate> predicates = new ArrayList<>();

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(visit.get("visitDate"), startDate));
        }
        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(visit.get("visitDate"), endDate));
        }
        if (petId != null) {
            predicates.add(cb.equal(visit.get("pet").get("id"), petId));
        }
        if (ownerId != null) {
            predicates.add(cb.equal(visit.get("pet").get("owner").get("id"), ownerId));
        }
        if (veterinarianId != null) {
            predicates.add(cb.equal(visit.get("veterinarian").get("id"), veterinarianId));
        }
        if (descriptionKeyword != null && !descriptionKeyword.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(visit.get("description")), "%" + descriptionKeyword.toLowerCase() + "%"));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.asc(visit.get("visitDate")));

        return entityManager.createQuery(query).getResultList();
    }
}
