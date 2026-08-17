package org.springframework.samples.petclinic.visit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.vet.Vet;

@DataJpaTest
class VisitRepositoryTests {

	@Autowired
	private VisitRepository visitRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Pet pet1;
	private Pet pet2;
	private Owner owner1;
	private Vet vet1;
	private Vet vet2;

	@BeforeEach
	void setUp() {
		// Setup Owners
		owner1 = new Owner();
		owner1.setFirstName("George");
		owner1.setLastName("Franklin");
		owner1.setAddress("110 W. Liberty St.");
		owner1.setCity("Madison");
		owner1.setTelephone("6085551023");
		entityManager.persist(owner1);

		// Setup PetType
		PetType cat = new PetType();
		cat.setName("cat");
		entityManager.persist(cat);

		// Setup Pets
		pet1 = new Pet();
		pet1.setName("Leo");
		pet1.setBirthDate(LocalDate.of(2000, 9, 7));
		pet1.setType(cat);
		pet1.setOwner(owner1);
		entityManager.persist(pet1);

		pet2 = new Pet();
		pet2.setName("Max");
		pet2.setBirthDate(LocalDate.of(2001, 10, 1));
		pet2.setType(cat);
		pet2.setOwner(owner1);
		entityManager.persist(pet2);

		// Setup Vets
		vet1 = new Vet();
		vet1.setFirstName("James");
		vet1.setLastName("Carter");
		entityManager.persist(vet1);

		vet2 = new Vet();
		vet2.setFirstName("Helen");	
		vet2.setLastName("Leary");
		entityManager.persist(vet2);

		// Setup Visits
		Visit visit1 = new Visit();
		visit1.setDate(LocalDate.of(2023, 1, 15));
		visit1.setDescription("Routine checkup for Leo");
		visit1.setPet(pet1);
		visit1.setVet(vet1);
		entityManager.persist(visit1);

		Visit visit2 = new Visit();
		visit2.setDate(LocalDate.of(2023, 1, 20));
		visit2.setDescription("Vaccination for Max");
		visit2.setPet(pet2);
		visit2.setVet(vet1);
		entityManager.persist(visit2);

		Visit visit3 = new Visit();
		visit3.setDate(LocalDate.of(2023, 2, 10));
		visit3.setDescription("Dental cleaning for Leo");
		visit3.setPet(pet1);
		visit3.setVet(vet2);
		entityManager.persist(visit3);

		entityManager.flush();
		entityManager.clear();
	}

	@Test
	void testFindAllVisits() {
		List<Visit> visits = visitRepository.findAll();
		assertThat(visits).hasSize(3);
	}

	@Test
	void testFindVisitsByPetId() {
		List<Visit> visits = visitRepository.findByPetId(pet1.getId());
		assertThat(visits).hasSize(2);
		assertThat(visits.get(0).getPet().getName()).isEqualTo("Leo");
	}

	@Test
	void testFindVisitsByCriteria_NoFilters() {
		List<Visit> visits = visitRepository.findVisitsByCriteria(null, null, null, null, null);
		assertThat(visits).hasSize(3);
	}

	@Test
	void testFindVisitsByCriteria_ByStartDate() {
		List<Visit> visits = visitRepository.findVisitsByCriteria(LocalDate.of(2023, 1, 16), null, null, null, null);
		assertThat(visits).hasSize(2);
		assertThat(visits.get(0).getDate()).isEqualTo(LocalDate.of(2023, 1, 20));
	}

	@Test
	void testFindVisitsByCriteria_ByEndDate() {
		List<Visit> visits = visitRepository.findVisitsByCriteria(null, LocalDate.of(2023, 1, 19), null, null, null);
		assertThat(visits).hasSize(1);
		assertThat(visits.get(0).getDate()).isEqualTo(LocalDate.of(2023, 1, 15));
	}

	@Test
	void testFindVisitsByCriteria_ByDateRange() {
		List<Visit> visits = visitRepository.findVisitsByCriteria(LocalDate.of(2023, 1, 16), LocalDate.of(2023, 1, 25), null, null, null);
		assertThat(visits).hasSize(1);
		assertThat(visits.get(0).getDate()).isEqualTo(LocalDate.of(2023, 1, 20));
	}

	@Test
	void testFindVisitsByCriteria_ByPetId() {
		List<Visit> visits = visitRepository.findVisitsByCriteria(null, null, pet1.getId(), null, null);
		assertThat(visits).hasSize(2);
		assertThat(visits.get(0).getPet().getId()).isEqualTo(pet1.getId());
	}

	@Test
	void testFindVisitsByCriteria_ByOwnerId() {
		List<Visit> visits = visitRepository.findVisitsByCriteria(null, null, null, owner1.getId(), null);
		assertThat(visits).hasSize(3);
	}

	@Test
	void testFindVisitsByCriteria_ByVetId() {
		List<Visit> visits = visitRepository.findVisitsByCriteria(null, null, null, null, vet1.getId());
		assertThat(visits).hasSize(2);
		assertThat(visits.get(0).getVet().getId()).isEqualTo(vet1.getId());
	}

	@Test
	void testFindVisitsByCriteria_CombinedFilters() {
		List<Visit> visits = visitRepository.findVisitsByCriteria(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31), pet1.getId(), owner1.getId(), vet1.getId());
		assertThat(visits).hasSize(1);
		assertThat(visits.get(0).getPet().getName()).isEqualTo("Leo");
		assertThat(visits.get(0).getVet().getFirstName()).isEqualTo("James");
		assertThat(visits.get(0).getDate()).isEqualTo(LocalDate.of(2023, 1, 15));
	}

	@Test
	void testFindVisitsByCriteria_NoMatchingFilters() {
		List<Visit> visits = visitRepository.findVisitsByCriteria(LocalDate.of(2024, 1, 1), null, null, null, null);
		assertThat(visits).isEmpty();
	}
}
