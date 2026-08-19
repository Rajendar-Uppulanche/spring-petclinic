/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.samples.petclinic.owner.Pet;
import org.springframework.samples.petclinic.owner.PetType;
import org.springframework.samples.petclinic.owner.PetTypeRepository;
import org.springframework.samples.petclinic.owner.Visit;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test of the Service and the Repository layer.
 * &lt;p&gt;
 * ClinicServiceSpringDataJpaTests subclasses benefit from the following services provided
 * by the Spring TestContext Framework:
 * &lt;/p&gt;
 * &lt;ul&gt;
 * &lt;li&gt;&lt;strong&gt;Spring IoC container caching&lt;/strong&gt; which spares us unnecessary set up
 * time between test execution.&lt;/li&gt;
 * &lt;li&gt;&lt;strong&gt;Dependency Injection&lt;/strong&gt; of test fixture instances, meaning that we
 * don't need to perform application context lookups. See the use of
 * {@link Autowired @Autowired} on the &lt;code&gt; &lt;/code&gt; instance variable, which uses
 * autowiring &lt;em&gt;by type&lt;/em&gt;.
 * &lt;li&gt;&lt;strong&gt;Transaction management&lt;/strong&gt;, meaning each test method is executed in
 * its own transaction, which is automatically rolled back by default. Thus, even if tests
 * insert or otherwise change database state, there is no need for a teardown or cleanup
 * script.
 * &lt;li&gt;An {@link org.springframework.context.ApplicationContext ApplicationContext} is
 * also inherited and can be used for explicit bean lookup if necessary.&lt;/li&gt;
 * &lt;/ul&gt;
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 * @author Dave Syer
 */
@DataJpaTest
// Ensure that if the mysql profile is active we connect to the real database:
@AutoConfigureTestDatabase(replace = Replace.NONE)
// @TestPropertySource("/application-postgres.properties")
class ClinicServiceTests {

	@Autowired
	protected OwnerRepository owners;

	@Autowired
	protected PetTypeRepository types;

	@Autowired
	protected VetRepository vets;

	private final Pageable pageable = Pageable.unpaged();

	@Test
	@Transactional // Ensure transaction for data insertion and rollback
	void shouldFindOwnersByLastNameContainingIgnoreCaseAndSorted() {
		// Add test data for case-insensitivity and sorting
		Owner owner1 = new Owner();
		owner1.setFirstName("Alice");
		owner1.setLastName("Smith");
		owner1.setAddress("123 Main St");
		owner1.setCity("Anytown");
		owner1.setTelephone("1111111111");
		this.owners.save(owner1);

		Owner owner2 = new Owner();
		owner2.setFirstName("Bob");
		owner2.setLastName("smith"); // Case-insensitive match
		owner2.setAddress("456 Oak Ave");
		owner2.setCity("Anytown");
		owner2.setTelephone("2222222222");
		this.owners.save(owner2);

		Owner owner3 = new Owner();
		owner3.setFirstName("Charlie");
		owner3.setLastName("Smyth"); // Partial match
		owner3.setAddress("789 Pine Ln");
		owner3.setCity("Anytown");
		owner3.setTelephone("3333333333");
		this.owners.save(owner3);

		Owner owner4 = new Owner();
		owner4.setFirstName("David");
		owner4.setLastName("Smither"); // Partial match
		owner4.setAddress("101 Elm St");
		owner4.setCity("Anytown");
		owner4.setTelephone("4444444444");
		this.owners.save(owner4);

		// Test partial and case-insensitive search for "smi"
		Page<Owner> foundOwners = this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("smi", pageable);
		assertThat(foundOwners).hasSize(4); // Smith, smith, Smyth, Smither

		// Verify sorting: Smith (Alice), smith (Bob), Smither (David), Smyth (Charlie)
		List<Owner> ownerList = foundOwners.getContent();
		assertThat(ownerList.get(0).getLastName()).isEqualTo("Smith");
		assertThat(ownerList.get(0).getFirstName()).isEqualTo("Alice");
		assertThat(ownerList.get(1).getLastName()).isEqualTo("smith");
		assertThat(ownerList.get(1).getFirstName()).isEqualTo("Bob");
		assertThat(ownerList.get(2).getLastName()).isEqualTo("Smither");
		assertThat(ownerList.get(2).getFirstName()).isEqualTo("David");
		assertThat(ownerList.get(3).getLastName()).isEqualTo("Smyth");
		assertThat(ownerList.get(3).getFirstName()).isEqualTo("Charlie");


		// Test search for "franklin" (case-insensitive, partial) - should find existing Franklin
		foundOwners = this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("franklin", pageable);
		assertThat(foundOwners).hasSize(1); // Only the existing Franklin
		assertThat(foundOwners.iterator().next().getLastName()).isEqualTo("Franklin");

		// Test empty string (should return all owners, including newly added ones)
		foundOwners = this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("", pageable);
		// Initial 10 owners + 4 new ones = 14
		assertThat(foundOwners.getTotalElements()).isEqualTo(14);

		// Test no match
		foundOwners = this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("NonExistent", pageable);
		assertThat(foundOwners).isEmpty();

		// Test special characters (assuming literal match)
		Owner ownerWithSpecialChar = new Owner();
		ownerWithSpecialChar.setFirstName("Special");
		ownerWithSpecialChar.setLastName("O'Connell");
		ownerWithSpecialChar.setAddress("123 Test");
		ownerWithSpecialChar.setCity("TestCity");
		ownerWithSpecialChar.setTelephone("5555555555");
		this.owners.save(ownerWithSpecialChar);

		foundOwners = this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("o'conn", pageable);
		assertThat(foundOwners).hasSize(1);
		assertThat(foundOwners.iterator().next().getLastName()).isEqualTo("O'Connell");

		foundOwners = this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("O'CONNELL", pageable);
		assertThat(foundOwners).hasSize(1);
		assertThat(foundOwners.iterator().next().getLastName()).isEqualTo("O'Connell");
	}

	@Test
	void shouldFindSingleOwnerWithPet() {
		Optional<Owner> optionalOwner = this.owners.findById(1);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		assertThat(owner.getLastName()).startsWith("Franklin");
		assertThat(owner.getPets()).hasSize(1);
		assertThat(owner.getPets().get(0).getType()).isNotNull();
		assertThat(owner.getPets().get(0).getType().getName()).isEqualTo("cat");
	}

	@Test
	@Transactional
	void shouldInsertOwner() {
		Page<Owner> owners = this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("Schultz", pageable);
		int found = (int) owners.getTotalElements();

		Owner owner = new Owner();
		owner.setFirstName("Sam");
		owner.setLastName("Schultz");
		owner.setAddress("4, Evans Street");
		owner.setCity("Wollongong");
		owner.setTelephone("4444444444");
		this.owners.save(owner);
		assertThat(owner.getId()).isNotZero();

		owners = this.owners.findByLastNameContainingIgnoreCaseOrderByLastNameAsc("Schultz", pageable);
		assertThat(owners.getTotalElements()).isEqualTo(found + 1);
	}

	@Test
	@Transactional
	void shouldUpdateOwner() {
		Optional<Owner> optionalOwner = this.owners.findById(1);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();
		String oldLastName = owner.getLastName();
		String newLastName = oldLastName + "X";

		owner.setLastName(newLastName);
		this.owners.save(owner);

		// retrieving new name from database
		optionalOwner = this.owners.findById(1);
		assertThat(optionalOwner).isPresent();
		owner = optionalOwner.get();
		assertThat(owner.getLastName()).isEqualTo(newLastName);
	}

	@Test
	void shouldFindAllPetTypes() {
		Collection<PetType> petTypes = this.types.findPetTypes();

		PetType petType1 = EntityUtils.getById(petTypes, PetType.class, 1);
		assertThat(petType1.getName()).isEqualTo("cat");
		PetType petType4 = EntityUtils.getById(petTypes, PetType.class, 4);
		assertThat(petType4.getName()).isEqualTo("snake");
	}

	@Test
	@Transactional
	void shouldInsertPetIntoDatabaseAndGenerateId() {
		Optional<Owner> optionalOwner = this.owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner6 = optionalOwner.get();

		int found = owner6.getPets().size();

		Pet pet = new Pet();
		pet.setName("bowser");
		Collection<PetType> types = this.types.findPetTypes();
		pet.setType(EntityUtils.getById(types, PetType.class, 2));
		pet.setBirthDate(LocalDate.now());
		owner6.addPet(pet);
		assertThat(owner6.getPets()).hasSize(found + 1);

		this.owners.save(owner6);

		optionalOwner = this.owners.findById(6);
		assertThat(optionalOwner).isPresent();
		owner6 = optionalOwner.get();
		assertThat(owner6.getPets()).hasSize(found + 1);
		// checks that id has been generated
		pet = owner6.getPet("bowser");
		assertThat(pet.getId()).isNotNull();
	}

	@Test
	@Transactional
	void shouldUpdatePetName() {
		Optional<Owner> optionalOwner = this.owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner6 = optionalOwner.get();

		Pet pet7 = owner6.getPet(7);
		String oldName = pet7.getName();

		String newName = oldName + "X";
		pet7.setName(newName);
		this.owners.save(owner6);

		optionalOwner = this.owners.findById(6);
		assertThat(optionalOwner).isPresent();
		owner6 = optionalOwner.get();
		pet7 = owner6.getPet(7);
		assertThat(pet7.getName()).isEqualTo(newName);
	}

	@Test
	void shouldFindVets() {
		Collection<Vet> vets = this.vets.findAll();

		Vet vet = EntityUtils.getById(vets, Vet.class, 3);
		assertThat(vet.getLastName()).isEqualTo("Douglas");
		assertThat(vet.getNrOfSpecialties()).isEqualTo(2);
		assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry");
		assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery");
	}

	@Test
	@Transactional
	void shouldAddNewVisitForPet() {
		Optional<Owner> optionalOwner = this.owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner6 = optionalOwner.get();

		Pet pet7 = owner6.getPet(7);
		int found = pet7.getVisits().size();
		Visit visit = new Visit();
		visit.setDescription("test");

		owner6.addVisit(pet7.getId(), visit);
		this.owners.save(owner6);

		assertThat(pet7.getVisits()) //
			.hasSize(found + 1) //
			.allMatch(value -> value.getId() != null);
	}

	@Test
	void shouldFindVisitsByPetId() {
		Optional<Owner> optionalOwner = this.owners.findById(6);
		assertThat(optionalOwner).isPresent();
		Owner owner6 = optionalOwner.get();

		Pet pet7 = owner6.getPet(7);
		Collection<Visit> visits = pet7.getVisits();

		assertThat(visits) //
			.hasSize(2) //
			.element(0)
			.extracting(Visit::getDate)
			.isNotNull();
	}

	@Test
	@Transactional
	void shouldFailToInsertDuplicatePetNameForSameOwner() {
		Optional<Owner> optionalOwner = this.owners.findById(1);
		assertThat(optionalOwner).isPresent();
		Owner owner = optionalOwner.get();

		Pet pet1 = new Pet();
		pet1.setName("DuplicatePetName");
		Collection<PetType> types = this.types.findPetTypes();
		pet1.setType(EntityUtils.getById(types, PetType.class, 1));
		pet1.setBirthDate(LocalDate.now());
		owner.addPet(pet1);
		this.owners.saveAndFlush(owner);

		Pet pet2 = new Pet();
		pet2.setName("duplicatepetname"); // Case-insensitive duplicate name
		pet2.setType(EntityUtils.getById(types, PetType.class, 1));
		pet2.setBirthDate(LocalDate.now());
		owner.addPet(pet2);

		assertThrows(DataIntegrityViolationException.class, () -> {
			this.owners.saveAndFlush(owner);
		});
	}

	@Test
	@Transactional
	void shouldAllowSamePetNameForDifferentOwners() {
		Collection<PetType> types = this.types.findPetTypes();
		PetType catType = EntityUtils.getById(types, PetType.class, 1);

		Optional<Owner> owner1Opt = this.owners.findById(1);
		assertThat(owner1Opt).isPresent();
		Owner owner1 = owner1Opt.get();

		Pet pet1 = new Pet();
		pet1.setName("SamePetName");
		pet1.setType(catType);
		pet1.setBirthDate(LocalDate.now());
		owner1.addPet(pet1);
		this.owners.saveAndFlush(owner1);

		Optional<Owner> owner2Opt = this.owners.findById(2);
		assertThat(owner2Opt).isPresent();
		Owner owner2 = owner2Opt.get();

		Pet pet2 = new Pet();
		pet2.setName("samepetname"); // Case-insensitive duplicate name, but for a
										// different owner
		pet2.setType(catType);
		pet2.setBirthDate(LocalDate.now());
		owner2.addPet(pet2);

		// Saving for different owner should succeed
		this.owners.saveAndFlush(owner2);

		// Verify both exist
		assertThat(owner1.getPet("SamePetName")).isNotNull();
		assertThat(owner2.getPet("samepetname")).isNotNull();
	}

}
