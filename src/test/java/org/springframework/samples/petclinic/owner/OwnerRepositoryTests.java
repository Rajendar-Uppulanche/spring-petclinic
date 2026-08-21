package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class OwnerRepositoryTests {

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void findByUserId() {
		Owner owner = new Owner();
		owner.setFirstName("John");
		owner.setLastName("Doe");
		owner.setAddress("123 Main St");
		owner.setCity("Anytown");
		owner.setTelephone("1234567890");
		owner.setUserId("john.doe.123");

		entityManager.persist(owner);
		entityManager.flush();

		Optional<Owner> foundOwner = ownerRepository.findByUserId("john.doe.123");
		assertThat(foundOwner).isPresent();
		assertThat(foundOwner.get().getFirstName()).isEqualTo("John");
		assertThat(foundOwner.get().getUserId()).isEqualTo("john.doe.123");
	}

	@Test
	void findByUserIdNotFound() {
		Optional<Owner> foundOwner = ownerRepository.findByUserId("nonexistent");
		assertThat(foundOwner).isNotPresent();
	}

	@Test
	void findByUserIdCaseSensitive() {
		Owner owner = new Owner();
		owner.setFirstName("Jane");
		owner.setLastName("Smith");
		owner.setAddress("456 Oak Ave");
		owner.setCity("Otherville");
		owner.setTelephone("0987654321");
		owner.setUserId("jane.smith.456");

		entityManager.persist(owner);
		entityManager.flush();

		Optional<Owner> foundOwner = ownerRepository.findByUserId("Jane.Smith.456");
		assertThat(foundOwner).isNotPresent();
		foundOwner = ownerRepository.findByUserId("jane.smith.456");
		assertThat(foundOwner).isPresent();
	}
}
