package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Transactional(readOnly = true)
    public OwnerDetailsDTO findOwnerDetailsById(int ownerId) {
        Optional<Owner> optionalOwner = ownerRepository.findOwnerWithPetsAndVisits(ownerId);
        Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
                "Owner not found with id: " + ownerId + ". Please ensure the ID is correct " + "and the owner exists in the database."));

        List<PetDetailsDTO> petDetails = owner.getPets().stream()
                .map(pet -> new PetDetailsDTO(pet, pet.getVisits().size()))
                .collect(Collectors.toList());

        return new OwnerDetailsDTO(owner, petDetails);
    }
}
