package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.owner.Owner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTests {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitServiceImpl visitService;

    private Visit visit1;
    private Visit visit2;
    private Pet pet1;
    private Owner owner1;
    private Vet vet1;

    @BeforeEach
    void setUp() {
        owner1 = new Owner();
        owner1.setId(1);
        owner1.setFirstName("George");
        owner1.setLastName("Franklin");

        pet1 = new Pet();
        pet1.setId(1);
        pet1.setName("Leo");
        pet1.setOwner(owner1);

        vet1 = new Vet();
        vet1.setId(1);
        vet1.setFirstName("James");
        vet1.setLastName("Carter");

        visit1 = new Visit();
        visit1.setId(1);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);
        visit1.setVeterinarian(vet1);
        visit1.setStatus(VisitStatus.COMPLETED);

        visit2 = new Visit();
        visit2.setId(2);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);
        visit2.setVeterinarian(vet1);
        visit2.setStatus(VisitStatus.SCHEDULED);
    }

    @Test
    void findVisitsShouldReturnFilteredResults() {
        VisitSearchCriteriaDTO criteria = new VisitSearchCriteriaDTO();
        criteria.setPetName("Leo");
        criteria.setStatus(VisitStatus.SCHEDULED);

        when(visitRepository.findAll(any(Specification.class))).thenReturn(Arrays.asList(visit2));

        List<Visit> result = visitService.findVisits(criteria);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescription()).isEqualTo("Vaccination");
        verify(visitRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void findVisitByIdShouldReturnVisit() {
        when(visitRepository.findById(1)).thenReturn(Optional.of(visit1));

        Visit result = visitService.findVisitById(1);

        assertThat(result).isEqualTo(visit1);
        verify(visitRepository, times(1)).findById(1);
    }

    @Test
    void findVisitByIdShouldThrowExceptionIfNotFound() {
        when(visitRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> visitService.findVisitById(99));
        verify(visitRepository, times(1)).findById(99);
    }

    @Test
    void saveVisitShouldPersistVisit() {
        visitService.saveVisit(visit1);
        verify(visitRepository, times(1)).save(visit1);
    }

    @Test
    void deleteVisitShouldRemoveVisit() {
        visitService.deleteVisit(1);
        verify(visitRepository, times(1)).deleteById(1);
    }
}
