package org.springframework.samples.petclinic.owner;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.owner.dto.PetVisitCountDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Owner> findOwnerById(Integer ownerId) {
        return ownerRepository.findById(ownerId);
    }

    @Transactional(readOnly = true)
    public List<PetVisitCountDto> findPetsWithVisitCountsByOwnerId(Integer ownerId) {
        return ownerRepository.findPetsWithVisitCountsByOwnerId(ownerId);
    }

    @Transactional
    public Owner saveOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Transactional(readOnly = true)
    public Page<Owner> findOwnersByLastNameStartingWith(String lastName, Pageable pageable) {
        return ownerRepository.findByLastNameStartingWith(lastName, pageable);
    }
}