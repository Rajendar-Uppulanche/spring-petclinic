package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {

	/**
	 * Retrieve all <code>Owner</code>s from the data store.
	 * @return a <code>Collection</code> of <code>Owner</code>s
	 */
	@Transactional(readOnly = true)
	Collection<Owner> findAll();

	/**
	 * Retrieve <code>Owner</code>s from the data store by last name, returning all owners
	 * whose last name <i>starts</i> with the given name.
	 * @param lastName Value to search for
	 * @return a <code>Collection</code> of matching <code>Owner</code>s (or an empty
	 * <code>Collection</code> if none found)
	 */
	@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Collection<Owner> findByLastName(@Param("lastName") String lastName);

	/**
	 * Retrieve an <code>Owner</code> from the data store by id.
	 * @param id the id to search for
	 * @return the <code>Owner</code> if found
	 */
	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id = :id")
	@Transactional(readOnly = true)
	Optional<Owner> findById(@Param("id") Integer id);

    /**
     * Retrieve an <code>Owner</code> from the data store by pet id.
     * @param petId the id of the pet
     * @return the <code>Owner</code> if found
     */
    @Query("SELECT owner FROM Owner owner JOIN owner.pets pet WHERE pet.id = :petId")
    @Transactional(readOnly = true)
    Owner findByPetId(@Param("petId") Integer petId);
}