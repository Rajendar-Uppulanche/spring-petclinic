package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OwnerRepository extends Repository<Owner, Integer> {

	@Transactional(readOnly = true)
	Owner findById(Integer id);

	@Transactional(readOnly = true)
	@Query("SELECT DISTINCT owner FROM Owner owner left join fetch owner.pets WHERE owner.lastName LIKE :lastName%")
	List<Owner> findByLastName(@Param("lastName") String lastName);

	void save(Owner owner);

	@Query("SELECT pet FROM Pet pet WHERE pet.owner.id = :ownerId")
	@Transactional(readOnly = true)
	List<Pet> findPetsByOwnerId(@Param("ownerId") Integer ownerId);

	@Query("SELECT pet, COUNT(visit) FROM Pet pet LEFT JOIN pet.visits visit WHERE pet.owner.id = :ownerId GROUP BY pet")
	@Transactional(readOnly = true)
	List<Object[]> findPetsWithVisitCountsByOwnerId(@Param("ownerId") Integer ownerId);

}
