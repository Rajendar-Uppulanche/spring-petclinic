package org.springframework.samples.petclinic.visit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitService visitService;

    private Visit visit1;
    private Visit visit2;
    private Visit visit3;
    private Pet pet1;
    private Pet pet2;
    private Owner owner1;
    private Owner owner2;

    @BeforeEach
    void setUp() {
        owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");

        owner2 = new Owner();
        owner2.setId(2);
        owner2.setFirstName("Betty");
        owner2.setLastName("Davis");

        PetType catType = new PetType();
        catType.setName("cat");
        PetType dogType = new PetType();
        dogType.setName("dog");

        pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setBirthDate(LocalDate.of(2010, 9, 7));
        pet1.setType(catType);
        pet1.setOwner(owner1);

        pet2 = new Pet();
        pet2.setId(2);
        pet2.setName("Max");
        pet2.setBirthDate(LocalDate.of(2018, 1, 1));
        pet2.setType(dogType);
        pet2.setOwner(owner2);

        visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);

        visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);

        visit3 = new Visit();
        visit3.setId(3);
        visit3.setDate(LocalDate.of(2023, 3, 10));
        visit3.setDescription("Annual checkup");
        visit3.setPet(pet2);
    }

    @Test
    void shouldFindAllVisits() {
        when(visitRepository.findAll()).thenReturn(Arrays.asList(visit1, visit2, visit3));
        List<Visit> visits = visitService.findAllVisits();
        assertThat(visits).hasSize(3);
    }

    @Test
    void shouldFilterByStartDate() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setStartDate(LocalDate.of(2023, 2, 1));
        when(visitRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(visit2, visit3));
        List<Visit> visits = visitService.findVisitsByCriteria(criteria);
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit2, visit3);
    }

    @Test
    void shouldFilterByPetName() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setPetName("leo");
        when(visitRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(visit1, visit2));
        List<Visit> visits = visitService.findVisitsByCriteria(criteria);
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    void shouldFilterByOwnerLastName() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setOwnerLastName("franklin");
        when(visitRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(visit1, visit2));
        List<Visit> visits = visitService.findVisitsByCriteria(criteria);
        assertThat(visits).hasSize(2);
        assertThat(visits).containsExactlyInAnyOrder(visit1, visit2);
    }

    @Test
    void shouldFilterByMultipleCriteria() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setStartDate(LocalDate.of(2023, 2, 1));
        criteria.setEndDate(LocalDate.of(2023, 2, 28));
        criteria.setPetName("leo");
        criteria.setOwnerLastName("franklin");
        when(visitRepository.findAll(any(Specification.class))).thenReturn(Collections.singletonList(visit2));
        List<Visit> visits = visitService.findVisitsByCriteria(criteria);
        assertThat(visits).hasSize(1);
        assertThat(visits).containsExactly(visit2);
    }

    @Test
    void shouldReturnEmptyListIfNoMatch() {
        VisitFilterCriteria criteria = new VisitFilterCriteria();
        criteria.setPetName("nonexistent");
        when(visitRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());
        List<Visit> visits = visitService.findVisitsByCriteria(criteria);
        assertThat(visits).isEmpty();
    }
}