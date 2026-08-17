package org.springframework.samples.petclinic.visit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.vet.Vet;

@ExtendWith(MockitoExtension.class)
class VisitServiceTests {

	@Mock
	private VisitRepository visitRepository;

	@InjectMocks
	private VisitService visitService;

	private Visit visit1;
	private Visit visit2;
	private Pet pet1;
	private Vet vet1;

	@BeforeEach
	void setUp() {
		pet1 = new Pet();
		pet1.setId(1);
		pet1.setName("Leo");

		vet1 = new Vet();
		vet1.setId(10);
		vet1.setFirstName("James");

		visit1 = new Visit();
		visit1.setId(1);
		visit1.setDate(LocalDate.of(2023, 1, 15));
		visit1.setDescription("Routine checkup");
		visit1.setPet(pet1);
		visit1.setVet(vet1);

		visit2 = new Visit();
		visit2.setId(2);
		visit2.setDate(LocalDate.of(2023, 1, 20));
		visit2.setDescription("Vaccination");
		visit2.setPet(pet1);
		visit2.setVet(vet1);
	}

	@Test
	void testFindAllVisits() {
		when(visitRepository.findAll()).thenReturn(Arrays.asList(visit1, visit2));
		List<Visit> visits = visitService.findAllVisits();
		assertThat(visits).hasSize(2);
		assertThat(visits.get(0).getDescription()).isEqualTo("Routine checkup");
	}

	@Test
	void testFindVisitsByCriteria_NoFilters() {
		when(visitRepository.findVisitsByCriteria(null, null, null, null, null))
			.thenReturn(Arrays.asList(visit1, visit2));
		List<Visit> visits = visitService.findVisitsByCriteria(null, null, null, null, null);
		assertThat(visits).hasSize(2);
	}

	@Test
	void testFindVisitsByCriteria_WithPetId() {
		when(visitRepository.findVisitsByCriteria(null, null, pet1.getId(), null, null))
			.thenReturn(Arrays.asList(visit1, visit2));
		List<Visit> visits = visitService.findVisitsByCriteria(null, null, pet1.getId(), null, null);
		assertThat(visits).hasSize(2);
		assertThat(visits.get(0).getPet().getId()).isEqualTo(pet1.getId());
	}

	@Test
	void testFindVisitsByCriteria_WithVetId() {
		when(visitRepository.findVisitsByCriteria(null, null, null, null, vet1.getId()))
			.thenReturn(Arrays.asList(visit1, visit2));
		List<Visit> visits = visitService.findVisitsByCriteria(null, null, null, null, vet1.getId());
		assertThat(visits).hasSize(2);
		assertThat(visits.get(0).getVet().getId()).isEqualTo(vet1.getId());
	}

	@Test
	void testSaveVisit() {
		when(visitRepository.save(any(Visit.class))).thenReturn(visit1);
		visitService.saveVisit(visit1);
		verify(visitRepository).save(visit1);
	}

	@Test
	void testFindVisitById() {
		when(visitRepository.findById(1)).thenReturn(Optional.of(visit1));
		Visit foundVisit = visitService.findVisitById(1);
		assertThat(foundVisit).isEqualTo(visit1);
	}

	@Test
	void testFindVisitById_NotFound() {
		when(visitRepository.findById(99)).thenReturn(Optional.empty());
		Visit foundVisit = visitService.findVisitById(99);
		assertThat(foundVisit).isNull();
	}
}
