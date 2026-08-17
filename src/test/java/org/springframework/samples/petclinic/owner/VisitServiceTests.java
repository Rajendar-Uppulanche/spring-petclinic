package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.vet.Vet;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceTests {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private VisitService visitService;

    private Owner owner;
    private Pet pet;
    private Vet vet;
    private Visit visit1;
    private Visit visit2;

    @BeforeEach
    void setUp() {
        owner = new Owner();
        owner.setId(1);
        owner.setFirstName("George");
        owner.setLastName("Franklin");

        pet = new Pet();
        pet.setId(10);
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.of(2000, 1, 1));
        pet.setType(new PetType());
        owner.addPet(pet);

        vet = new Vet();
        vet.setId(100);
        vet.setFirstName("James");
        vet.setLastName("Carter");

        visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet);
        visit1.setVet(vet);

        visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet);
        visit2.setVet(vet);
    }

    @Test
    void testFindVisits_noFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        when(visitRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(Arrays.asList(visit1, visit2), pageable, 2));

        Page<Visit> result = visitService.findVisits(null, null, null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactlyInAnyOrder(visit1, visit2);
        verify(visitRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testFindVisits_byOwnerId() {
        Pageable pageable = PageRequest.of(0, 10);
        when(ownerRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(visitRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(Arrays.asList(visit1, visit2), pageable, 2));

        Page<Visit> result = visitService.findVisits(owner.getId(), null, null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
        verify(ownerRepository).findById(owner.getId());
        verify(visitRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testFindVisits_byPetId() {
        Pageable pageable = PageRequest.of(0, 10);
        when(visitRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(visit1), pageable, 1));

        Page<Visit> result = visitService.findVisits(null, pet.getId(), null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(visit1);
        verify(visitRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testFindVisits_byVetId() {
        Pageable pageable = PageRequest.of(0, 10);
        when(visitRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(Arrays.asList(visit1, visit2), pageable, 2));

        Page<Visit> result = visitService.findVisits(null, null, vet.getId(), null, null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
        verify(visitRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testFindVisits_byDateRange() {
        Pageable pageable = PageRequest.of(0, 10);
        when(visitRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(visit1), pageable, 1));

        Page<Visit> result = visitService.findVisits(null, null, null, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31), null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(visit1);
        verify(visitRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testFindVisits_byDescription() {
        Pageable pageable = PageRequest.of(0, 10);
        when(visitRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.singletonList(visit1), pageable, 1));

        Page<Visit> result = visitService.findVisits(null, null, null, null, null, "checkup", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(visit1);
        verify(visitRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testSaveVisit() {
        visitService.saveVisit(visit1);
        verify(visitRepository).save(visit1);
    }

    @Test
    void testFindVisitById() {
        when(visitRepository.findById(visit1.getId())).thenReturn(Optional.of(visit1));
        Visit foundVisit = visitService.findVisitById(visit1.getId());
        assertThat(foundVisit).isEqualTo(visit1);
        verify(visitRepository).findById(visit1.getId());
    }

    @Test
    void testFindVisitsByPetId() {
        when(visitRepository.findByPetId(pet.getId())).thenReturn(Arrays.asList(visit1, visit2));
        List<Visit> foundVisits = visitService.findVisitsByPetId(pet.getId());
        assertThat(foundVisits).hasSize(2);
        verify(visitRepository).findByPetId(pet.getId());
    }
}