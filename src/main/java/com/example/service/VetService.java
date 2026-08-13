package com.example.service;

import com.example.model.Specialty;
import com.example.model.Vet;
import com.example.repository.SpecialtyRepository;
import com.example.repository.VetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class VetService {

    private final VetRepository vetRepository;
    private final SpecialtyRepository specialtyRepository;

    @Autowired
    public VetService(VetRepository vetRepository, SpecialtyRepository specialtyRepository) {
        this.vetRepository = vetRepository;
        this.specialtyRepository = specialtyRepository;
    }

    @Transactional(readOnly = true)
    public List<Vet> findAllVets() {
        return vetRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Vet> findVetsBySpecialty(String specialtyName) {
        if (specialtyName == null || specialtyName.trim().isEmpty() || "all".equalsIgnoreCase(specialtyName)) {
            return vetRepository.findAll();
        }
        return vetRepository.findBySpecialtyName(specialtyName);
    }

    @Transactional(readOnly = true)
    public Set<Specialty> findAllDistinctSpecialties() {
        return vetRepository.findAllDistinctSpecialties();
    }

    @Transactional
    public Vet saveVet(Vet vet) {
        return vetRepository.save(vet);
    }

    @Transactional(readOnly = true)
    public Vet findVetById(Long id) {
        return vetRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteVet(Long id) {
        vetRepository.deleteById(id);
    }

    @Transactional
    public Vet addSpecialtyToVet(Long vetId, String specialtyName) {
        Vet vet = vetRepository.findById(vetId)
                .orElseThrow(() -> new IllegalArgumentException("Vet not found with ID: " + vetId));

        Specialty specialty = specialtyRepository.findByName(specialtyName)
                .orElseGet(() -> specialtyRepository.save(new Specialty(specialtyName)));

        vet.addSpecialty(specialty);
        return vetRepository.save(vet);
    }
}