package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class PetService {

    private final OwnerRepository ownerRepository;
    private final PetTypeRepository petTypeRepository;

    public PetService(OwnerRepository ownerRepository, PetTypeRepository petTypeRepository) {
        this.ownerRepository = ownerRepository;
        this.petTypeRepository = petTypeRepository;
    }

    @Transactional(readOnly = true)
    public Collection<PetType> findPetTypes() {
        return petTypeRepository.findPetTypes();
    }

    @Transactional(readOnly = true)
    public Owner findOwnerById(int ownerId) {
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        return optionalOwner.orElseThrow(() -> new IllegalArgumentException(
                "Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
    }

    @Transactional(readOnly = true)
    public Pet findPetById(int petId, int ownerId) {
        Owner owner = findOwnerById(ownerId);
        Pet pet = owner.getPet(petId);
        if (pet == null) {
            throw new IllegalArgumentException("Pet with id " + petId + " not found for owner with id " + ownerId + ".");
        }
        return pet;
    }

    @Transactional(readOnly = true)
    public boolean hasVisits(Integer petId, Integer ownerId) {
        if (petId == null) {
            return false; // A new pet cannot have visits
        }
        Pet pet = findPetById(petId, ownerId);
        return pet.hasVisits();
    }

    @Transactional
    public void saveOwner(Owner owner) {
        ownerRepository.saveAndFlush(owner);
    }
}
