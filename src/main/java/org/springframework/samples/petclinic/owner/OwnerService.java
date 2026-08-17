package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for managing Owner-related business logic, including pet visit count processing.
 */
@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    /**
     * Finds an owner and their pets, enriching pet data with formatted visit counts.
     * Applies business rules FR-012 and FR-013 for visit count formatting.
     * @param ownerId The ID of the owner to retrieve.
     * @return An OwnerDetailsDTO containing owner and pet information with formatted visit counts.
     * @throws IllegalArgumentException if the owner is not found.
     */
    @Transactional(readOnly = true)
    public OwnerDetailsDTO findOwnerDetailsWithPetVisitCounts(int ownerId) {
        Optional<Owner> optionalOwner = ownerRepository.findById(ownerId);
        Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
                "Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));

        OwnerDetailsDTO ownerDetailsDTO = new OwnerDetailsDTO(owner);

        // Get visit counts for all pets of this owner using the optimized repository query
        Map<Integer, Long> petVisitCounts = ownerRepository.findPetVisitCountsByOwnerId(ownerId)
            .stream()
            .collect(Collectors.toMap(
                row -> (Integer) row[0], // petId
                row -> (Long) row[1]    // visitCount
            ));

        // Populate PetDetailsDTOs with formatted visit counts
        for (Pet pet : owner.getPets()) {
            long visitCount = petVisitCounts.getOrDefault(pet.getId(), 0L);
            String visitCountString = formatVisitCount(visitCount); // Apply FR-012, FR-013
            ownerDetailsDTO.getPets().add(new PetDetailsDTO(
                pet.getId(),
                pet.getName(),
                pet.getBirthDate(),
                pet.getType(),
                visitCountString
            ));
        }
        return ownerDetailsDTO;
    }

    /**
     * Formats the raw visit count into a human-readable string (FR-012, FR-013).
     * @param count The raw visit count.
     * @return "No visits", "1 visit", or "N visits".
     */
    private String formatVisitCount(long count) {
        if (count == 0) {
            return "No visits";
        } else if (count == 1) {
            return "1 visit";
        } else {
            return count + " visits";
        }
    }
}
