package com.example.petclinic.service;

import com.example.petclinic.model.Veterinarian;
import com.example.petclinic.repository.VeterinarianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;

    @Autowired
    public VeterinarianService(VeterinarianRepository veterinarianRepository) {
        this.veterinarianRepository = veterinarianRepository;
    }

    @Transactional(readOnly = true)
    public List<Veterinarian> findAllVeterinarians() {
        return veterinarianRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Veterinarian> findVeterinarianById(Integer id) {
        return veterinarianRepository.findById(id);
    }

    @Transactional
    public Veterinarian saveVeterinarian(Veterinarian veterinarian) {
        return veterinarianRepository.save(veterinarian);
    }

    @Transactional
    public void deleteVeterinarian(Integer id) {
        veterinarianRepository.deleteById(id);
    }
}