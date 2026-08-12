package org.springframework.samples.petclinic.repository;

import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.model.Veterinarian;

import java.util.Collection;

/**
 * Repository class for <code>Veterinarian</code> domain objects
 * All method names are compliant with Spring Data naming conventions
 * so that Spring Data can create an appropriate query.
 */
public interface VetRepository extends Repository<Veterinarian, Integer> {

    /**
     * Retrieve all <code>Veterinarian</code>s from the data store.
     * @return a <code>Collection</code> of <code>Veterinarian</code>s
     */
    Collection<Veterinarian> findAll();

    /**
     * Retrieve a <code>Veterinarian</code> by id from the data store.
     * @param id the id to search for
     * @return the <code>Veterinarian</code> if found
     */
    Veterinarian findById(Integer id);
}
