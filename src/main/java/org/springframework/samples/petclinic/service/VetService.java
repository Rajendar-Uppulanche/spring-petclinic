package org.springframework.samples.petclinic.service;

import org.springframework.samples.petclinic.model.Veterinarian;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Service class for <code>Veterinarian</code>-related operations.
 */
@Service
public class VetService {

    private final VetRepository vetRepository;

    public VetService(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @Transactional(readOnly = true)
    public Collection<Veterinarian> findAllVets() {
        return vetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Veterinarian findVetById(Integer id) {
        return vetRepository.findById(id);
    }
}
