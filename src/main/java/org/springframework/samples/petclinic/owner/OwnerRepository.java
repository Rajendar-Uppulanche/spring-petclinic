package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

public interface OwnerRepository extends Repository<Owner, Integer> {

	@Transactional(readOnly = true)
	Page<Owner> findByLastNameStartingWith(String lastName, Pageable pageable);

	@Transactional(readOnly = true)
	@EntityGraph(attributePaths = {"pets", "pets.visits"}) // Eagerly fetch pets and their visits
	Optional<Owner> findById(Integer id);

	void save(Owner owner);

	void saveAndFlush(Owner owner);

	Collection<Owner> findAll();

}