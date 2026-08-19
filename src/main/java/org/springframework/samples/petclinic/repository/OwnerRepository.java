package org.springframework.samples.petclinic.repository;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.model.Owner;

/**
 * Repository class for <code>Owner</code> domain objects.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
public interface OwnerRepository extends Repository<Owner, Integer> {

	/**
	 * Save an <code>Owner</code> to the data store, either inserting or updating it.
	 * @param owner the <code>Owner</code> to save
	 */
	void save(Owner owner);

	/**
	 * Retrieve <code>Owner</code>s from the data store by last name, returning all owners
	 * whose last name <i>starts</i> with the given name.
	 * @param lastName Value to search for
	 * @return a <code>Collection</code> of matching <code>Owner</code>s (or an empty
	 * <code>Collection</code> if none found)
	 */
	@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
	Collection<Owner> findByLastName(@Param("lastName") String lastName);

	/**
	 * Retrieve an <code>Owner</code> from the data store by id.
	 * @param id the id to search for
	 * @return the <code>Owner</code> if found
	 */
	// Original: Optional<Owner> findById(Integer id);
	// Modified to eagerly fetch pets and their visits for performance (NFR-065)
	@Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.pets pet LEFT JOIN FETCH pet.visits WHERE owner.id = :id")
	Optional<Owner> findById(@Param("id") Integer id);

}
