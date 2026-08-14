package org.springframework.samples.petclinic.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.model.PreventiveCare;

import java.util.List;

public interface PreventiveCareRepository extends Repository<PreventiveCare, Integer> {

    /**
     * Save a {@link PreventiveCare} to the data store.
     * @param preventiveCare the {@link PreventiveCare} to save
     */
    void save(PreventiveCare preventiveCare);

    /**
     * Retrieve all {@link PreventiveCare}s from the data store for a specific pet.
     * @param petId the pet id
     * @return a List of {@link PreventiveCare}s
     */
    @Query("SELECT pc FROM PreventiveCare pc WHERE pc.pet.id = ?1 ORDER BY pc.careDate DESC")
    List<PreventiveCare> findByPetId(Integer petId);

    /**
     * Retrieve a {@link PreventiveCare} by its id.
     * @param id the id to search for
     * @return the {@link PreventiveCare} if found
     */
    PreventiveCare findById(Integer id);

    /**
     * Delete a {@link PreventiveCare} by its id.
     * @param id the id of the {@link PreventiveCare} to delete
     */
    void deleteById(Integer id);
}
