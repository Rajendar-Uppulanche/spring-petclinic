package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.owner.Owner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VisitRepositoryTests {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Pet pet1;
    private Pet pet2;
    private Vet vet1;
    private Vet vet2;
    private Owner owner1;
    private Owner owner2;

    @BeforeEach
    void setUp() {
        owner1 = new Owner();
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");
        entityManager.persist(owner1);

        owner2 = new Owner();
        owner2.setFirstName("Betty");
        owner2.setLastName("Davis");
        entityManager.persist(owner2);

        pet1 = new Pet();
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2010, 1, 1));
        pet1.setOwner(owner1);
        entityManager.persist(pet1);

        pet2 = new Pet();
        pet2.setName("Max");
        pet2.setBirthDate(LocalDate.of(2015, 5, 10));
        pet2.setOwner(owner2);
        entityManager.persist(pet2);

        vet1 = new Vet();
        vet1.setFirstName("James");
        vet1.setLastName("Carter");
        entityManager.persist(vet1);

        vet2 = new Vet();
        vet2.setFirstName("Helen");
        vet2.setLastName("Leary");
        entityManager.persist(vet2);

        Visit visit1 = new Visit();
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup for Leo");
        visit1.setPet(pet1);
        visit1.setVeterinarian(vet1);
        visit1.setStatus(VisitStatus.COMPLETED);
        entityManager.persist(visit1);

        Visit visit2 = new Visit();
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination for Max");
        visit2.setPet(pet2);
        visit2.setVeterinarian(vet2);
        visit2.setStatus(VisitStatus.SCHEDULED);
        entityManager.persist(visit2);

        Visit visit3 = new Visit();
        visit3.setDate(LocalDate.of(2023, 3, 10));
        visit3.setDescription("Follow-up for Leo");
        visit3.setPet(pet1);
        visit3.setVeterinarian(vet1);
        visit3.setStatus(VisitStatus.SCHEDULED);
        entityManager.persist(visit3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindAllWithDateRangeSpecification() {
        VisitSearchCriteriaDTO criteria = new VisitSearchCriteriaDTO();
        criteria.setStartDate(LocalDate.of(2023, 1, 1));
        criteria.setEndDate(LocalDate.of(2023, 1, 31));

        Specification<Visit> spec = createVisitSpecification(criteria);
        List<Visit> visits = visitRepository.findAll(spec);

        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getDescription()).isEqualTo("Routine checkup for Leo");
    }

    @Test
    void testFindAllWithPetNameSpecification() {
        VisitSearchCriteriaDTO criteria = new VisitSearchCriteriaDTO();
        criteria.setPetName("Leo");

        Specification<Visit> spec = createVisitSpecification(criteria);
        List<Visit> visits = visitRepository.findAll(spec);

        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDescription).containsExactlyInAnyOrder("Routine checkup for Leo", "Follow-up for Leo");
    }

    @Test
    void testFindAllWithOwnerLastNameSpecification() {
        VisitSearchCriteriaDTO criteria = new VisitSearchCriteriaDTO();
        criteria.setOwnerLastName("Franklin");

        Specification<Visit> spec = createVisitSpecification(criteria);
        List<Visit> visits = visitRepository.findAll(spec);

        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDescription).containsExactlyInAnyOrder("Routine checkup for Leo", "Follow-up for Leo");
    }

    @Test
    void testFindAllWithVeterinarianFirstNameSpecification() {
        VisitSearchCriteriaDTO criteria = new VisitSearchCriteriaDTO();
        criteria.setVeterinarianFirstName("James");

        Specification<Visit> spec = createVisitSpecification(criteria);
        List<Visit> visits = visitRepository.findAll(spec);

        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDescription).containsExactlyInAnyOrder("Routine checkup for Leo", "Follow-up for Leo");
    }

    @Test
    void testFindAllWithStatusSpecification() {
        VisitSearchCriteriaDTO criteria = new VisitSearchCriteriaDTO();
        criteria.setStatus(VisitStatus.SCHEDULED);

        Specification<Visit> spec = createVisitSpecification(criteria);
        List<Visit> visits = visitRepository.findAll(spec);

        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDescription).containsExactlyInAnyOrder("Vaccination for Max", "Follow-up for Leo");
    }

    @Test
    void testFindAllWithCombinedSpecification() {
        VisitSearchCriteriaDTO criteria = new VisitSearchCriteriaDTO();
        criteria.setStartDate(LocalDate.of(2023, 3, 1));
        criteria.setEndDate(LocalDate.of(2023, 3, 31));
        criteria.setPetName("Leo");
        criteria.setVeterinarianLastName("Carter");
        criteria.setStatus(VisitStatus.SCHEDULED);

        Specification<Visit> spec = createVisitSpecification(criteria);
        List<Visit> visits = visitRepository.findAll(spec);

        assertThat(visits).hasSize(1);
        assertThat(visits.get(0).getDescription()).isEqualTo("Follow-up for Leo");
    }

    private Specification<Visit> createVisitSpecification(VisitSearchCriteriaDTO criteria) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (criteria.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), criteria.getEndDate()));
            }
            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            if (org.springframework.util.StringUtils.hasText(criteria.getPetName())) {
                Join<Visit, Pet> petJoin = root.join("pet");
                predicates.add(cb.like(cb.lower(petJoin.get("name")), "%" + criteria.getPetName().toLowerCase() + "%"));
            }

            if (org.springframework.util.StringUtils.hasText(criteria.getOwnerLastName())) {
                Join<Visit, Pet> petJoin = root.join("pet");
                Join<Pet, Owner> ownerJoin = petJoin.join("owner");
                predicates.add(cb.like(cb.lower(ownerJoin.get("lastName")), "%" + criteria.getOwnerLastName().toLowerCase() + "%"));
            }

            if (org.springframework.util.StringUtils.hasText(criteria.getVeterinarianFirstName()) || org.springframework.util.StringUtils.hasText(criteria.getVeterinarianLastName())) {
                Join<Visit, Vet> vetJoin = root.join("veterinarian");
                if (org.springframework.util.StringUtils.hasText(criteria.getVeterinarianFirstName())) {
                    predicates.add(cb.like(cb.lower(vetJoin.get("firstName")), "%" + criteria.getVeterinarianFirstName().toLowerCase() + "%"));
                }
                if (org.springframework.util.StringUtils.hasText(criteria.getVeterinarianLastName())) {
                    predicates.add(cb.like(cb.lower(vetJoin.get("lastName")), "%" + criteria.getVeterinarianLastName().toLowerCase() + "%"));
                }
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
