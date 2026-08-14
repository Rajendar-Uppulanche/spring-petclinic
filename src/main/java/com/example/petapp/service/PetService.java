package com.example.petapp.service;

import com.example.petapp.model.Pet;
import com.example.petapp.repository.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<Pet> findAllPets() {
        return petRepository.findAll();
    }

    public Optional<Pet> findPetById(Long id) {
        return petRepository.findById(id);
    }

    @Transactional
    public Pet savePet(Pet pet) {
        // The @PastOrPresent validation on the Pet entity's birthDate field
        // will be automatically triggered by Spring's validation mechanism
        // before persisting. If validation fails, a ConstraintViolationException
        // will be thrown.
        return petRepository.save(pet);
    }

    @Transactional
    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }
}