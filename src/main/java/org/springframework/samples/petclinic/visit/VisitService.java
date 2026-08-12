package org.springframework.samples.petclinic.visit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.pet.Pet;
import org.springframework.samples.petclinic.pet.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VisitService {

    private final VisitRepository visitRepository;
    private final PetRepository petRepository; // Needed to fetch pet name for CSV filename

    public VisitService(VisitRepository visitRepository, PetRepository petRepository) {
        this.visitRepository = visitRepository;
        this.petRepository = petRepository;
    }

    @Transactional(readOnly = true)
    public Page<Visit> findVisitsByPetId(Integer petId, Pageable pageable) {
        return visitRepository.findByPetId(petId, pageable);
    }

    @Transactional(readOnly = true)
    public List<Visit> findAllVisitsByPetId(Integer petId) {
        return visitRepository.findByPetId(petId);
    }

    @Transactional(readOnly = true)
    public Pet findPetById(Integer petId) {
        return petRepository.findById(petId);
    }

    public void saveVisit(Visit visit) {
        visitRepository.save(visit);
    }
}
