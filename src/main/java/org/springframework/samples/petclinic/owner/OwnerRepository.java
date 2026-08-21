package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository extends Repository<Owner, Integer> {

	@Transactional(readOnly = true)
	List<Owner> findByLastName(String lastName);

	@Query("SELECT owner FROM Owner owner WHERE owner.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Page<Owner> findByLastNameStartingWith(@Param("lastName") String lastName, Pageable pageable);

	@Transactional(readOnly = true)
	Optional<Owner> findById(Integer id);

	void save(Owner owner);

	@Transactional(readOnly = true)
	Optional<Owner> findByUserId(String userId);

}
