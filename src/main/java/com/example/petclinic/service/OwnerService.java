package com.example.petclinic.service;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.repository.OwnerRepository;
import com.example.petclinic.service.dto.OwnerDetailsDTO;
import com.example.petclinic.service.dto.PetDTO;
import com.example.petclinic.service.dto.PetVisitCountDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final VisitService visitService; // Inject VisitService

    public OwnerService(OwnerRepository ownerRepository, VisitService visitService) {
        this.ownerRepository = ownerRepository;
        this.visitService = visitService;
    }

    @Transactional(readOnly = true)
    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Owner> findById(Long id) {
        return ownerRepository.findById(id);
    }

    @Transactional
    public Owner save(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Transactional
    public void deleteById(Long id) {
        ownerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<OwnerDetailsDTO> findOwnerDetailsById(Long ownerId) {
        return ownerRepository.findByIdWithPets(ownerId).map(owner -> {
            OwnerDetailsDTO dto = new OwnerDetailsDTO();
            dto.setId(owner.getId());
            dto.setFirstName(owner.getFirstName());
            dto.setLastName(owner.getLastName());
            dto.setAddress(owner.getAddress());
            dto.setCity(owner.getCity());
            dto.setTelephone(owner.getTelephone());

            // Fetch visit counts for all pets of this owner in one go
            List<PetVisitCountDTO> petVisitCounts = visitService.countVisitsByPetForOwner(ownerId);
            Map<Long, Long> visitCountMap = petVisitCounts.stream()
                .collect(Collectors.toMap(PetVisitCountDTO::getPetId, PetVisitCountDTO::getVisitCount));

            dto.setPets(owner.getPets().stream().map(pet -> {
                PetDTO petDto = new PetDTO();
                petDto.setId(pet.getId());
                petDto.setName(pet.getName());
                petDto.setBirthDate(pet.getBirthDate());
                petDto.setTypeName(pet.getType() != null ? pet.getType().getName() : null);
                petDto.setOwnerId(owner.getId());
                // Populate visit count from the pre-fetched map
                petDto.setVisitCount(visitCountMap.getOrDefault(pet.getId(), 0L));
                return petDto;
            }).collect(Collectors.toSet()));

            return dto;
        });
    }
}