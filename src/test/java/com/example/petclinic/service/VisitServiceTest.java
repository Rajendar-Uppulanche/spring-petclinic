package com.example.petclinic.service;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.Veterinarian;
import com.example.petclinic.model.Visit;
import com.example.petclinic.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class VisitServiceTest {

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
    private Veterinarian vet1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        owner1 = new Owner();
        owner1.setId(1L);
        owner1.setFirstName("John");
        owner1.setLastName("Doe");

        pet1 = new Pet();
        pet1.setId(1L);
        pet1.setName("Buddy");
        pet1.setOwner(owner1);

        pet2 = new Pet();
        pet2.setId(2L);
        pet2.setName("Lucy");
        pet2.setOwner(owner1);

        vet1 = new Veterinarian();
        vet1.setId(1L);
        vet1.setFirstName("Dr. James");
        vet1.setLastName("Carter");

        visit1 = new Visit();
        visit1.setId(1L);
        visit1.setVisitDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(pet1);
        visit1.setVeterinarian(vet1);

        visit2 = new Visit();
        visit2.setId(2L);
        visit2.setVisitDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Vaccination");
        visit2.setPet(pet1);
        visit2.setVeterinarian(vet1);

        visit3 = new Visit();
        visit3.setId(3L);
        visit3.setVisitDate(LocalDate.of(2023, 3, 10));
        visit3.setDescription("Dental cleaning");
        visit3.setPet(pet2);
        visit3.setVeterinarian(vet1);
    }

    @Test
    void testFindAll() {
        when(visitRepository.findAll()).thenReturn(Arrays.asList(visit1, visit2, visit3));
        List<Visit> visits = visitService.findAll();
        assertThat(visits).hasSize(3);
        verify(visitRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit1));
        Optional<Visit> foundVisit = visitService.findById(1L);
        assertThat(foundVisit).isPresent();
        assertThat(foundVisit.get().getDescription()).isEqualTo("Routine checkup");
        verify(visitRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        when(visitRepository.save(any(Visit.class))).thenReturn(visit1);
        Visit savedVisit = visitService.save(new Visit());
        assertThat(savedVisit).isNotNull();
        verify(visitRepository, times(1)).save(any(Visit.class));
    }

    @Test
    void testDeleteById() {
        visitService.deleteById(1L);
        verify(visitRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindVisitsByCriteria() {
        when(visitRepository.findVisitsByCriteria(
            eq(LocalDate.of(2023, 1, 1)), eq(LocalDate.of(2023, 12, 31)),
            eq(1L), eq(1L), eq(1L), eq("checkup")
        )).thenReturn(Arrays.asList(visit1));

        List<Visit> filteredVisits = visitService.findVisitsByCriteria(
            LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31),
            1L, 1L, 1L, "checkup"
        );

        assertThat(filteredVisits).hasSize(1);
        assertThat(filteredVisits.get(0).getDescription()).isEqualTo("Routine checkup");
        verify(visitRepository, times(1)).findVisitsByCriteria(
            eq(LocalDate.of(2023, 1, 1)), eq(LocalDate.of(2023, 12, 31)),
            eq(1L), eq(1L), eq(1L), eq("checkup")
        );
    }

    @Test
    void testFindVisitsByCriteria_noFilters() {
        when(visitRepository.findVisitsByCriteria(
            isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        )).thenReturn(Arrays.asList(visit1, visit2, visit3));

        List<Visit> filteredVisits = visitService.findVisitsByCriteria(
            null, null, null, null, null, null
        );

        assertThat(filteredVisits).hasSize(3);
        verify(visitRepository, times(1)).findVisitsByCriteria(
            isNull(), isNull(), isNull(), isNull(), isNull(), isNull()
        );
    }
}
