package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.retry.RetryOnTransientFailure; // Import the new annotation

import java.util.Collection;
import java.util.Optional;

public interface OwnerRepository extends Repository<Owner, Integer> {
	
	/**
	 * Save an {@link Owner} to the data store, either inserting or updating it.
	 * @param owner the {@link Owner} to save
	 * @see BaseEntity#isNew
	 */
	@RetryOnTransientFailure // Apply retry to save operations
	void save(Owner owner);

	/**
	 * Retrieve {@link Owner}s from the data store by last name, returning all owners whose last name
	 * starts with the given name.
	 * @param lastName Value to search for
	 * @return a Collection of matching {@link Owner}s (or an empty Collection if none found)
	 */
	@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
	@RetryOnTransientFailure // Apply retry to find operations
	Collection<Owner> findByLastName(@Param("lastName") String lastName);

	/**
	 * Retrieve an {@link Owner} from the data store by id.
	 * @param id the id to search for
	 * @return the {@link Owner} if found
	 */
	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id = :id")
	@RetryOnTransientFailure // Apply retry to find operations
	Optional<Owner> findById(@Param("id") Integer id);

}
