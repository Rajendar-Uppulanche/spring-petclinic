package org.springframework.samples.petclinic.owner;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository class for <code>Owner</code> domain objects all methods return <code>Collection</code> of <code>Owner</code>
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
public interface OwnerRepository extends Repository<Owner, Integer> {
	/**
	 * Retrieve all <code>Vet</code>s from the data store.
	 * @return a <code>Collection</code> of <code>Vet</code>s
	 */
	@Transactional(readOnly = true)
	Collection<Owner> findAll();


	/**
	 * Retrieve <code>Owner</code>s from the data store by last name, returning all owners whose last name <i>starts</i> with
	 * the given name.
	 * @param lastName Value to search for
	 * @return a <code>Collection</code> of matching <code>Owner</code>s (or an empty <code>Collection</code> if none
	 * found)
	 */
	@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%"
			+ "ORDER BY owner.lastName, owner.firstName")
	@Transactional(readOnly = true)
	Collection<Owner> findByLastName(@Param("lastName") String lastName);

	/**
	 * Retrieve an <code>Owner</code> from the data store by id.
	 * @param id the id to search for
	 * @return the <code>Owner</code> if found
	 */
	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id =:id")
	@Transactional(readOnly = true)
	Owner findById(@Param("id") Integer id);


	/**
	 * Save an <code>Owner</code> to the data store, either inserting or updating it.
	 * @param owner the <code>Owner</code> to save
	 */
	void save(Owner owner);

	/**
	 * Retrieve <code>Owner</code>s from the data store by last name, allowing for partial, case-insensitive matching,
	 * and ordering results alphabetically by last name.
	 * @param lastName Value to search for (can be a partial match)
	 * @return a <code>Collection</code> of matching <code>Owner</code>s (or an empty <code>Collection</code> if none found)
	 */
	@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE lower(owner.lastName) LIKE lower(concat('%', :lastName, '%')) ORDER BY owner.lastName ASC")
	@Transactional(readOnly = true)
	Collection<Owner> findByLastNameContainingIgnoreCaseOrderByLastNameAsc(@Param("lastName") String lastName);

}
