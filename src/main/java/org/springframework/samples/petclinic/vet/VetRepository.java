package org.springframework.samples.petclinic.vet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface VetRepository extends JpaRepository<Vet, Integer> {

    @Transactional(readOnly = true)
    Collection<Vet> findAll();
}
