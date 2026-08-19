package org.springframework.samples.petclinic.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michael Isvy
 * @author Oliver Gierke
 */
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class OwnerRepositoryTests {

	@Autowired
	OwnerRepository ownerRepository;

	@Test
	void findByLastName() {
		Collection<Owner> owners = this.ownerRepository.findByLastName("Franklin");
		assertThat(owners).hasSize(1);
		owners = this.ownerRepository.findByLastName("Day");
		assertThat(owners).isEmpty();
	}

	@Test
	void findAll() {
		Collection<Owner> owners = this.ownerRepository.findByLastName("");
		assertThat(owners).hasSize(10);
	}

	@Test
	void findById() {
		Optional<Owner> ownerOptional = this.ownerRepository.findById(1);
		assertThat(ownerOptional).isPresent();
		Owner owner = ownerOptional.get();
		assertThat(owner.getLastName()).startsWith("Franklin");
		assertThat(owner.getPets()).hasSize(1);
		assertThat(owner.getPets().iterator().next().getName()).isEqualTo("Leo");
	}

	@Test
	void saveOwner() {
		Owner owner = new Owner();
		owner.setFirstName("George");
		owner.setLastName("Bush");
		owner.setAddress("110 W. Liberty St.");
		owner.setCity("Madison");
		owner.setTelephone("6085551000");
		this.ownerRepository.save(owner);

		Owner savedOwner = this.ownerRepository.findById(owner.getId()).get();
		assertThat(savedOwner.getLastName()).isEqualTo("Bush");
	}

	@Test
	void shouldUpdateOwner() {
		Owner owner = this.ownerRepository.findById(1).get();
		String oldLastName = owner.getLastName();
		String newLastName = oldLastName + "X";
		owner.setLastName(newLastName);
		this.ownerRepository.save(owner);
		owner = this.ownerRepository.findById(1).get();
		assertThat(owner.getLastName()).isEqualTo(newLastName);
	}

	// New test for NFR-065: Verify eager loading of visits
	@Test
	@Transactional // Ensure lazy loading would fail without JOIN FETCH
	void findByIdShouldEagerlyLoadVisits() {
		// Owner 1 (George Franklin) has one pet (Leo), Leo has one visit.
		Optional<Owner> ownerOptional = this.ownerRepository.findById(1);
		assertThat(ownerOptional).isPresent();
		Owner owner = ownerOptional.get();

		assertThat(owner.getPets()).isNotEmpty();
		Pet pet = owner.getPets().iterator().next(); // Get Leo

		// Verify visits are loaded and count is correct
		assertThat(pet.getVisits()).isNotNull();
		assertThat(pet.getVisits()).hasSize(1); // Leo has 1 visit in test data
		assertThat(pet.getVisitCount()).isEqualTo(1); // Using the new helper method

		// Owner 2 (Betty Davis) has two pets (Basil, Jewel). Basil has 2 visits, Jewel has 0.
		ownerOptional = this.ownerRepository.findById(2);
		assertThat(ownerOptional).isPresent();
		Owner owner2 = ownerOptional.get();

		assertThat(owner2.getPets()).hasSize(2);
		List<Pet> pets = owner2.getPets().stream().sorted((p1, p2) -> p1.getName().compareTo(p2.getName())).toList();

		Pet basil = pets.get(0); // Basil
		assertThat(basil.getName()).isEqualTo("Basil");
		assertThat(basil.getVisits()).isNotNull();
		assertThat(basil.getVisits()).hasSize(2); // Basil has 2 visits
		assertThat(basil.getVisitCount()).isEqualTo(2);

		Pet jewel = pets.get(1); // Jewel
		assertThat(jewel.getName()).isEqualTo("Jewel");
		assertThat(jewel.getVisits()).isNotNull();
		assertThat(jewel.getVisits()).hasSize(0); // Jewel has 0 visits
		assertThat(jewel.getVisitCount()).isEqualTo(0);
	}

}
