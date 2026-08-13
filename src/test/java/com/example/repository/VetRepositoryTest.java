package com.example.repository;

import com.example.model.Specialty;
import com.example.model.Vet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VetRepository vetRepository;

    private Specialty radiology;
    private Specialty surgery;
    private Specialty dentistry;

    private Vet vet1;
    private Vet vet2;
    private Vet vet3;

    @BeforeEach
    void setUp() {
        entityManager.getEntityManager().createQuery("DELETE FROM Vet").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM Specialty").executeUpdate();
        entityManager.flush();

        radiology = new Specialty("Radiology");
        surgery = new Specialty("Surgery");
        dentistry = new Specialty("Dentistry");

        entityManager.persist(radiology);
        entityManager.persist(surgery);
        entityManager.persist(dentistry);
        entityManager.flush();

        vet1 = new Vet("James", "Carter");
        vet1.addSpecialty(radiology);
        vet1.addSpecialty(surgery);

        vet2 = new Vet("Helen", "Leary");
        vet2.addSpecialty(dentistry);

        vet3 = new Vet("Linda", "Douglas");

        entityManager.persist(vet1);
        entityManager.persist(vet2);
        entityManager.persist(vet3);
        entityManager.flush();
    }

    @Test
    void whenFindAll_thenReturnAllVets() {
        List<Vet> vets = vetRepository.findAll();
        assertThat(vets).hasSize(3);
        assertThat(vets).containsExactlyInAnyOrder(vet1, vet2, vet3);
    }

    @Test
    void whenFindBySpecialtyName_thenReturnVetsWithThatSpecialty() {
        List<Vet> vetsWithRadiology = vetRepository.findBySpecialtyName("Radiology");
        List<Vet> vetsWithDentistry = vetRepository.findBySpecialtyName("Dentistry");
        List<Vet> vetsWithSurgery = vetRepository.findBySpecialtyName("Surgery");
        assertThat(vetsWithRadiology).hasSize(1);
        assertThat(vetsWithRadiology).containsExactly(vet1);
        assertThat(vetsWithDentistry).hasSize(1);
        assertThat(vetsWithDentistry).containsExactly(vet2);
        assertThat(vetsWithSurgery).hasSize(1);
        assertThat(vetsWithSurgery).containsExactly(vet1);
    }

    @Test
    void whenFindBySpecialtyName_thenNoVetsFoundForNonExistentSpecialty() {
        List<Vet> vets = vetRepository.findBySpecialtyName("Cardiology");
        assertThat(vets).isEmpty();
    }

    @Test
    void whenFindAllDistinctSpecialties_thenReturnAllUniqueSpecialtiesAssociatedWithVets() {
        Set<Specialty> distinctSpecialties = vetRepository.findAllDistinctSpecialties();
        assertThat(distinctSpecialties).hasSize(3);
        assertThat(distinctSpecialties).containsExactlyInAnyOrder(radiology, surgery, dentistry);
    }

    @Test
    void whenNoVetsHaveSpecialties_thenFindAllDistinctSpecialtiesReturnsEmpty() {
        entityManager.getEntityManager().createQuery("DELETE FROM Vet").executeUpdate();
        entityManager.persist(new Vet("New", "Vet"));
        entityManager.flush();
        Set<Specialty> distinctSpecialties = vetRepository.findAllDistinctSpecialties();
        assertThat(distinctSpecialties).isEmpty();
    }
}