package org.springframework.samples.petclinic.vaccination;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

public interface VaccineTypeRepository extends Repository<VaccineType, Integer> {

    @Transactional(readOnly = true)
    Collection<VaccineType> findAll();

    @Transactional(readOnly = true)
    Optional<VaccineType> findById(Integer id);

    @Transactional(readOnly = true)
    Optional<VaccineType> findByName(String name);

    void save(VaccineType vaccineType);
}
