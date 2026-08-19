package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class NotificationPreferenceService {

    private final OwnerRepository ownerRepository;

    public NotificationPreferenceService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Transactional
    public boolean unsubscribe(Integer ownerId) {
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        if (ownerOptional.isPresent()) {
            Owner owner = ownerOptional.get();
            owner.setReceivesVaccinationReminders(false);
            ownerRepository.save(owner);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean subscribe(Integer ownerId) {
        Optional<Owner> ownerOptional = ownerRepository.findById(ownerId);
        if (ownerOptional.isPresent()) {
            Owner owner = ownerOptional.get();
            owner.setReceivesVaccinationReminders(true);
            ownerRepository.save(owner);
            return true;
        }
        return false;
    }
}