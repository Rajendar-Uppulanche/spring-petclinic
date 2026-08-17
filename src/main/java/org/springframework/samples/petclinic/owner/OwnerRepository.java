package org.springframework.samples.petclinic.owner;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class for `Owner` domain objects
 * All method names are compliant with Spring Data naming conventions
 * so this interface can easily be extended for Spring Data JPA
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
public interface OwnerRepository extends Repository<Owner, Integer> {

	/**
	 * Save an {@link Owner} to the data store, either inserting or updating it.
	 * @param owner the {@link Owner} to save
	 */
	void save(Owner owner);

	/**
	 * Retrieve `Owner`s from the data store by last name, returning
	 * all owners whose last name starts with the given name.
	 * @param lastName Value to search for
	 * @return a Collection of matching `Owner`s (or an empty Collection if none
	 * found)
	 */
	@Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.pets WHERE owner.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Collection<Owner> findByLastName(@Param("lastName") String lastName);

	/**
	 * Retrieve an {@link Owner} from the data store by id.
	 * @param id the id to search for
	 * @return the {@link Owner} if found
	 */
	@Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.pets WHERE owner.id = :id")
	@Transactional(readOnly = true)
	Optional<Owner> findById(@Param("id") Integer id);

	/**
	 * Retrieve all {@link Owner}s from the data store.
	 * @return a Collection of {@link Owner}s
	 */
	@Transactional(readOnly = true)
	Collection<Owner> findAll();

	/**
	 * Retrieve `Owner`s from the data store by last name, returning
	 * all owners whose last name starts with the given name.
	 * @param lastName Value to search for
	 * @return a Page of matching `Owner`s (or an empty Page if none
	 * found)
	 */
	@Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.pets WHERE owner.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Page<Owner> findByLastNameStartingWith(@Param("lastName") String lastName, Pageable pageable);

	/**
	 * Retrieve an {@link Owner} from the data store by id, eagerly fetching its pets and their visits.
	 * This is to prevent N+1 query issues when displaying owner details with pet visit counts.
	 * @param id the id to search for
	 * @return the {@link Owner} if found, with pets and visits eagerly loaded
	 */
	@Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.pets pet LEFT JOIN FETCH pet.visits WHERE owner.id = :id")
	@Transactional(readOnly = true)
	Optional<Owner> findByIdWithPetsAndVisits(@Param("id") Integer id);

}
