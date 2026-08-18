package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.owner.dto.PetVisitCountDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {

	@Transactional(readOnly = true)
	Page<Owner> findByLastNameStartingWith(String lastName, Pageable pageable);

	@Transactional(readOnly = true)
	@Query("SELECT owner FROM Owner owner LEFT JOIN FETCH owner.pets WHERE owner.id = :id")
	Optional<Owner> findById(@Param("id") Integer id);

	// New method for fetching pets with visit counts
	@Query("SELECT new org.springframework.samples.petclinic.owner.dto.PetVisitCountDto(" +
		   "p.id, p.name, p.birthDate, pt.name, COUNT(v.id)) " +
		   "FROM Owner o JOIN o.pets p JOIN p.type pt LEFT JOIN p.visits v " +
		   "WHERE o.id = :ownerId " +
		   "GROUP BY p.id, p.name, p.birthDate, pt.name " +
		   "ORDER BY p.name")
	List<PetVisitCountDto> findPetsWithVisitCountsByOwnerId(@Param("ownerId") Integer ownerId);

}