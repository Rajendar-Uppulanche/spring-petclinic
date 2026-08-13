package org.springframework.samples.petclinic.owner;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface VisitRepository extends Repository<Visit, Integer> {

    /**
     * Save a {@link Visit} to the data store.
     * @param visit the {@link Visit} to save
     */
    void save(Visit visit);

    /**
     * Retrieve a {@link Visit} by its ID.
     * @param id the ID to search for
     * @return the {@link Visit} if found
     */
    Optional<Visit> findById(Integer id);

    /**
     * Retrieve all {@link Visit}s from the data store.
     * @return a List of {@link Visit}s
     */
    List<Visit> findAll();
}