package org.springframework.samples.petclinic.service;

import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class PetService {

    private PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Transactional(readOnly = true)
    public Collection<PetType> findPetTypes() {
        return petRepository.findPetTypes();
    }

    @Transactional
    public void savePet(Pet pet) {
        petRepository.save(pet);
    }

    @Transactional(readOnly = true)
    public Pet findPetById(int id) {
        return petRepository.findById(id);
    }

    @Transactional
    public void saveVisit(Visit visit) {
        petRepository.findById(visit.getPet().getId()).addVisit(visit);
    }

    @Transactional(readOnly = true)
    public List<PetRepository.PetWithVisitCount> findPetsByOwnerIdWithVisitCount(Integer ownerId) {
        return petRepository.findByOwnerIdWithVisitCount(ownerId);
    }
}