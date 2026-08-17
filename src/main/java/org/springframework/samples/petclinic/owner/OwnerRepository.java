package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository extends Repository<Owner, Integer> {

	Optional<Owner> findById(Integer id);

	void save(Owner owner);

	Page<Owner> findByLastNameStartingWith(String lastName, Pageable pageable);

	/**
	 * Retrieves the count of visits for each pet belonging to a specific owner.
	 * This query performs a LEFT JOIN to include pets with no visits (count will be 0).
	 * @param ownerId The ID of the owner.
	 * @return A list of Object arrays, where each array contains [petId, visitCount].
	 */
	@Query("SELECT p.id, COUNT(v) FROM Owner o JOIN o.pets p LEFT JOIN p.visits v WHERE o.id = :ownerId GROUP BY p.id")
	List<Object[]> findPetVisitCountsByOwnerId(Integer ownerId);

}
