package org.springframework.samples.petclinic.pet;

import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PetRepository extends Repository<Pet, Integer> {

    @Transactional(readOnly = true)
    Pet findById(@Param("id") Integer id);
}
