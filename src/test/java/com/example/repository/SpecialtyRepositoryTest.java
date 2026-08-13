package com.example.repository;

import com.example.model.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SpecialtyRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SpecialtyRepository specialtyRepository;

    private Specialty radiology;
    private Specialty surgery;

    @BeforeEach
    void setUp() {
        radiology = new Specialty("Radiology");
        surgery = new Specialty("Surgery");
        entityManager.persistAndFlush(radiology);
        entityManager.persistAndFlush(surgery);
    }

    @Test
    void whenFindByName_thenReturnSpecialty() {
        Optional<Specialty> found = specialtyRepository.findByName(radiology.getName());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(radiology.getName());
    }

    @Test
    void whenFindByName_thenNotFound() {
        Optional<Specialty> found = specialtyRepository.findByName("NonExistent");
        assertThat(found).isNotPresent();
    }

    @Test
    void whenSaveSpecialty_thenItCanBeFound() {
        Specialty dentistry = new Specialty("Dentistry");
        Specialty savedSpecialty = specialtyRepository.save(dentistry);
        entityManager.flush();
        entityManager.clear();
        Optional<Specialty> found = specialtyRepository.findById(savedSpecialty.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Dentistry");
    }

    @Test
    void whenDeleteSpecialty_thenItIsRemoved() {
        Long idToDelete = radiology.getId();
        specialtyRepository.deleteById(idToDelete);
        entityManager.flush();
        Optional<Specialty> found = specialtyRepository.findById(idToDelete);
        assertThat(found).isNotPresent();
    }
}