package org.springframework.samples.petclinic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.PreventiveCare;
import org.springframework.samples.petclinic.repository.PreventiveCareRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class PreventiveCareService {

    private PreventiveCareRepository preventiveCareRepository;

    @Autowired
    public PreventiveCareService(PreventiveCareRepository preventiveCareRepository) {
        this.preventiveCareRepository = preventiveCareRepository;
    }

    @Transactional(readOnly = true)
    public Collection<PreventiveCare> findPreventiveCaresByPetId(int petId) throws DataAccessException {
        return preventiveCareRepository.findByPetId(petId);
    }

    @Transactional(readOnly = true)
    public PreventiveCare findPreventiveCareById(int preventiveCareId) throws DataAccessException {
        return preventiveCareRepository.findById(preventiveCareId);
    }

    @Transactional
    public void savePreventiveCare(PreventiveCare preventiveCare) throws DataAccessException {
        preventiveCareRepository.save(preventiveCare);
    }

    @Transactional
    public void deletePreventiveCare(int preventiveCareId) throws DataAccessException {
        preventiveCareRepository.deleteById(preventiveCareId);
    }
}
