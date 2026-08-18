package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    /**
     * Retrieve all {@link PetType}s from the data store.
     * @return a Collection of {@link PetType}s
     */
    @Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
    @Transactional(readOnly = true)
    List<PetType> findPetTypes();

    /**
     * Retrieve {@link Owner}s from the data store by last name, returning all owners
     * whose last name starts with the given name.
     * @param lastName Value to search for
     * @param pageable
     * @return a Collection of matching {@link Owner}s (or an empty Collection if none
     * found)
     */
    @Query("SELECT owner FROM Owner owner WHERE owner.lastName LIKE :lastName%")
    @Transactional(readOnly = true)
    Page<Owner> findByLastNameStartingWith(@Param("lastName") String lastName, Pageable pageable);

    /**
     * Retrieve all owners who receive vaccination reminders.
     * @param receivesReminders boolean flag
     * @return a List of Owners
     */
    @Query("SELECT owner FROM Owner owner WHERE owner.receivesVaccinationReminders = :receivesReminders")
    @Transactional(readOnly = true)
    List<Owner> findByReceivesVaccinationReminders(@Param("receivesReminders") boolean receivesReminders);
}
