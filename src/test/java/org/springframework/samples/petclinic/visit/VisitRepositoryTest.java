package org.springframework.samples.petclinic.visit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class VisitRepositoryTest {

    @Autowired
    private VisitRepository visitRepository;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private PetTypeRepository petTypeRepository;

    private Owner owner1;
    private Pet pet1;
    private Visit visit1;
    private Visit visit2;

    @BeforeEach
    void setUp() {
        owner1 = new Owner();
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");
        owner1.setAddress("110 W. Liberty St.");
        owner1.setCity("Madison");
        owner1.setTelephone("6085551023");
        ownerRepository.save(owner1);

        PetType catType = new PetType();
        catType.setName("cat");
        petTypeRepository.save(catType);

        pet1 = new Pet();
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2010, 9, 7));
        pet1.setType(catType);
        owner1.addPet(pet1);
        ownerRepository.save(owner1);

        visit1 = new Visit();
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);
        visitRepository.save(visit1);

        visit2 = new Visit();
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);
        visitRepository.save(visit2);

        Owner owner2 = new Owner();
        owner2.setFirstName("Betty");
        owner2.setLastName("Davis");
        owner2.setAddress("638 Cardinal Ave.");
        owner2.setCity("Sun Prairie");
        owner2.setTelephone("6085551749");
        ownerRepository.save(owner2);

        PetType dogType = new PetType();
        dogType.setName("dog");
        petTypeRepository.save(dogType);

        Pet pet2 = new Pet();
        pet2.setName("Max");
        pet2.setBirthDate(LocalDate.of(2018, 1, 1));
        pet2.setType(dogType);
        owner2.addPet(pet2);
        ownerRepository.save(owner2);

        Visit visit3 = new Visit();
        visit3.setDate(LocalDate.of(2023, 3, 10));
        visit3.setDescription("Annual checkup");
        visit3.setPet(pet2);
        visitRepository.save(visit3);
    }

    @Test
    void shouldFindAllVisits() {
        List<Visit> visits = visitRepository.findAll();
        assertThat(visits).hasSize(3);
    }

    @Test
    void shouldFilterByStartDate() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setStartDate(LocalDate.of(2023, 2, 1));
        Specification<Visit> spec = VisitSpecifications.withCriteria(criteria);
        List<Visit> visits = visitRepository.findAll(spec);
        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDescription).containsExactlyInAnyOrder("Vaccination", "Annual checkup");
    }

    @Test
    void shouldFilterByEndDate() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setEndDate(LocalDate.of(2023, 1, 31));
        Specification<Visit> spec = VisitSpecifications.withCriteria(criteria);
        List<Visit> visits = visitRepository.findAll(spec);
        assertThat(visits).hasSize(1);
        assertThat(visits).extracting(Visit::getDescription).containsExactly("Routine checkup");
    }

    @Test
    void shouldFilterByDateRange() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setStartDate(LocalDate.of(2023, 1, 1));
        criteria.setEndDate(LocalDate.of(2023, 1, 31));
        Specification<Visit> spec = VisitSpecifications.withCriteria(criteria);
        List<Visit> visits = visitRepository.findAll(spec);
        assertThat(visits).hasSize(1);
        assertThat(visits).extracting(Visit::getDescription).containsExactly("Routine checkup");
    }

    @Test
    void shouldFilterByPetName() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setPetName("leo");
        Specification<Visit> spec = VisitSpecifications.withCriteria(criteria);
        List<Visit> visits = visitRepository.findAll(spec);
        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDescription).containsExactlyInAnyOrder("Routine checkup", "Vaccination");
    }

    @Test
    void shouldFilterByOwnerLastName() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setOwnerLastName("franklin");
        Specification<Visit> spec = VisitSpecifications.withCriteria(criteria);
        List<Visit> visits = visitRepository.findAll(spec);
        assertThat(visits).hasSize(2);
        assertThat(visits).extracting(Visit::getDescription).containsExactlyInAnyOrder("Routine checkup", "Vaccination");
    }

    @Test
    void shouldFilterByMultipleCriteria() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setStartDate(LocalDate.of(2023, 2, 1));
        criteria.setEndDate(LocalDate.of(2023, 2, 28));
        criteria.setPetName("leo");
        criteria.setOwnerLastName("franklin");
        Specification<Visit> spec = VisitSpecifications.withCriteria(criteria);
        List<Visit> visits = visitRepository.findAll(spec);
        assertThat(visits).hasSize(1);
        assertThat(visits).extracting(Visit::getDescription).containsExactly("Vaccination");
    }

    @Test
    void shouldReturnEmptyListIfNoMatch() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setPetName("nonexistent");
        Specification<Visit> spec = VisitSpecifications.withCriteria(criteria);
        List<Visit> visits = visitRepository.findAll(spec);
        assertThat(visits).isEmpty();
    }
}