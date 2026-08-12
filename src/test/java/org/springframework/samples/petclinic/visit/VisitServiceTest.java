package org.springframework.samples.petclinic.visit;

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
import org.springframework.data.domain.Sort;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.pet.PetRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private VisitService visitService;

    private Pet testPet;
    private Visit visit1;
    private Visit visit2;

    @BeforeEach
    void setup() {
        testPet = new Pet();
        testPet.setId(1);
        testPet.setName("Leo");

        visit1 = new Visit();
        visit1.setId(10);
        visit1.setDate(LocalDate.of(2023, 1, 15));
        visit1.setDescription("Routine checkup");
        visit1.setPet(testPet);
        visit1.setStatus("Completed");
        visit1.setDurationMinutes(30);
        visit1.setDiagnosisCode("A01");
        visit1.setTreatmentTags("Vaccination,Deworming");

        visit2 = new Visit();
        visit2.setId(11);
        visit2.setDate(LocalDate.of(2023, 2, 20));
        visit2.setDescription("Follow-up");
        visit2.setPet(testPet);
        visit2.setStatus("Scheduled");
        visit2.setDurationMinutes(45);
        visit2.setDiagnosisCode("B02");
        visit2.setTreatmentTags("Medication");
    }

    @Test
    void testFindVisitsByPetIdPaginated() {
        List<Visit> visits = Arrays.asList(visit2, visit1);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "date"));
        Page<Visit> visitPage = new PageImpl<>(visits, pageable, 2);

        given(visitRepository.findByPetId(eq(1), any(Pageable.class))).willReturn(visitPage);

        Page<Visit> result = visitService.findVisitsByPetId(1, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(11);
        assertThat(result.getContent().get(0).getDiagnosisCode()).isEqualTo("B02");
        assertThat(result.getContent().get(0).getTreatmentTags()).isEqualTo("Medication");
        verify(visitRepository).findByPetId(eq(1), any(Pageable.class));
    }

    @Test
    void testFindAllVisitsByPetId() {
        List<Visit> visits = Arrays.asList(visit1, visit2);
        given(visitRepository.findByPetId(1)).willReturn(visits);

        List<Visit> result = visitService.findAllVisitsByPetId(1);

        assertThat(result).isNotNull().hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(10);
        assertThat(result.get(0).getDiagnosisCode()).isEqualTo("A01");
        assertThat(result.get(0).getTreatmentTags()).isEqualTo("Vaccination,Deworming");
        verify(visitRepository).findByPetId(1);
    }

    @Test
    void testFindPetById() {
        given(petRepository.findById(1)).willReturn(testPet);

        Pet result = visitService.findPetById(1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Leo");
        verify(petRepository).findById(1);
    }

    @Test
    void testSaveVisit() {
        visitService.saveVisit(visit1);
        verify(visitRepository).save(visit1);
    }
}
