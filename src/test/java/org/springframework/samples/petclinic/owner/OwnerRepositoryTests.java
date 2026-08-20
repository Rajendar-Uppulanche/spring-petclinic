package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class OwnerRepositoryTests {

	@Autowired
	OwnerRepository ownerRepository;

	@Test
	void shouldFindOwnerById() {
		Owner owner = this.ownerRepository.findById(1);
		assertThat(owner.getLastName()).isEqualTo("Franklin");
		assertThat(owner.getPets().size()).isEqualTo(2);
	}

	@Test
	void shouldFindOwnerByLastName() {
		Collection<Owner> owners = this.ownerRepository.findByLastName("Franklin");
		assertThat(owners.size()).isEqualTo(1);
		assertThat(owners.iterator().next().getFirstName()).isEqualTo("George");	}

	@Test
	void shouldFindOwnerByLastNameContainingIgnoreCaseOrderByLastNameAsc() {
		Collection<Owner> owners = this.ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("lin");
		assertThat(owners.size()).isEqualTo(2);
		assertThat(owners.iterator().next().getLastName()).isEqualTo("Franklin");

		owners = this.ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("fRaNkLiN");
		assertThat(owners.size()).isEqualTo(1);
		assertThat(owners.iterator().next().getFirstName()).isEqualTo("George");

		owners = this.ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("nonexistent");
		assertThat(owners).isEmpty();

		owners = this.ownerRepository.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("da");
		assertThat(owners.size()).isEqualTo(2);
		assertThat(owners.iterator().next().getLastName()).isEqualTo("Davis");
	}

	@Test
	void shouldInsertOwner() {
		Collection<Owner> owners = this.ownerRepository.findAll();
		int found = owners.size();
		Owner owner = new Owner();
		owner.setFirstName("Joe");
		owner.setLastName("Bloggs");
		owner.setAddress("123 Main St.");
		owner.setCity("Herndon");
		owner.setTelephone("7035551212");
		this.ownerRepository.save(owner);
		assertThat(owner.getId().longValue()).isNotEqualTo(0);
		assertThat(this.ownerRepository.findAll().size()).isEqualTo(found + 1);
	}

	@Test
	void shouldUpdateOwner() {
		Owner owner = this.ownerRepository.findById(1);
		String oldLastName = owner.getLastName();
		String newLastName = oldLastName + "X";

		owner.setLastName(newLastName);
		this.ownerRepository.save(owner);

		owner = this.ownerRepository.findById(1);
		assertThat(owner.getLastName()).isEqualTo(newLastName);
	}

}
