package org.springframework.samples.petclinic.vaccination;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.samples.petclinic.owner.Pet;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VaccinationRepository extends Repository<Vaccination, Integer> {

    @Transactional(readOnly = true)
    Optional<Vaccination> findById(Integer id);

    @Transactional(readOnly = true)
    Collection<Vaccination> findByPet(Pet pet);

    void save(Vaccination vaccination);

    void delete(Vaccination vaccination);
}
