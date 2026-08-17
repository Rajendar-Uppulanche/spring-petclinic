package org.springframework.samples.petclinic.owner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    @Autowired
    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Transactional(readOnly = true)
    public OwnerDetails findOwnerDetailsById(Integer ownerId) {
        Optional<Owner> optionalOwner = ownerRepository.findByIdWithPetsAndVisits(ownerId);
        if (optionalOwner.isEmpty()) {
            return null;
        }
        Owner owner = optionalOwner.get();
        OwnerDetails ownerDetails = new OwnerDetails();
        // Copy properties from owner to ownerDetails
        ownerDetails.setId(owner.getId());
        ownerDetails.setFirstName(owner.getFirstName());
        ownerDetails.setLastName(owner.getLastName());
        ownerDetails.setAddress(owner.getAddress());
        ownerDetails.setCity(owner.getCity());
        ownerDetails.setTelephone(owner.getTelephone());

        List<PetDetails> petDetailsList = owner.getPets().stream()
            .map(pet -> {
                PetDetails petDetails = new PetDetails();
                // Copy properties from pet to petDetails
                petDetails.setId(pet.getId());
                petDetails.setName(pet.getName());
                petDetails.setBirthDate(pet.getBirthDate());
                petDetails.setType(pet.getType());
                petDetails.setOwner(pet.getOwner()); // Maintain relationship if needed
                petDetails.setVisits(pet.getVisits()); // Keep visits for other logic if any

                int visitCount = pet.getVisits().size();
                if (visitCount == 0) {
                    petDetails.setFormattedVisitCount("No visits");
                } else if (visitCount == 1) {
                    petDetails.setFormattedVisitCount("1 visit");
                } else {
                    petDetails.setFormattedVisitCount(visitCount + " visits");
                }
                return petDetails;
            })
            .collect(Collectors.toList());
        ownerDetails.setPetDetails(petDetailsList);
        return ownerDetails;
    }

    @Transactional(readOnly = true)
    public Page<Owner> findByLastNameStartingWith(String lastName, Pageable pageable) {
        return ownerRepository.findByLastNameStartingWith(lastName, pageable);
    }

    @Transactional
    public void saveOwner(Owner owner) {
        ownerRepository.save(owner);
    }

    @Transactional(readOnly = true)
    public Owner findById(Integer ownerId) {
        return ownerRepository.findById(ownerId).orElse(null);
    }

    @Transactional(readOnly = true)
    public Collection<Owner> findAll() {
        return ownerRepository.findAll();
    }
}
