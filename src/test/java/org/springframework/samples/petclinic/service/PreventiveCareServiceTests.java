package org.springframework.samples.petclinic.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PreventiveCare;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Service.class))
class PreventiveCareServiceTests {

    @Autowired
    protected PreventiveCareService preventiveCareService;

    @Autowired
    protected PetService petService; // To get a pet for testing

    @Autowired
    protected OwnerService ownerService; // To get an owner for testing

    @Test
    @Transactional
    void shouldFindPreventiveCaresByPetId() {
        Owner owner = this.ownerService.findOwnerById(1); // Owner with pets
        Pet pet = owner.getPet("Leo"); // Pet with ID 3

        PreventiveCare pc1 = new PreventiveCare();
        pc1.setCareDate(LocalDate.of(2023, 1, 1));
        pc1.setType("Vaccination");
        pc1.setDescription("Rabies");
        pet.addPreventiveCare(pc1);
        this.preventiveCareService.savePreventiveCare(pc1);

        PreventiveCare pc2 = new PreventiveCare();
        pc2.setCareDate(LocalDate.of(2023, 2, 1));
        pc2.setType("Treatment");
        pc2.setDescription("Flea treatment");
        pet.addPreventiveCare(pc2);
        this.preventiveCareService.savePreventiveCare(pc2);

        Collection<PreventiveCare> preventiveCares = this.preventiveCareService.findPreventiveCaresByPetId(pet.getId());
        assertThat(preventiveCares).hasSize(2);
        assertThat(preventiveCares).extracting(PreventiveCare::getType).containsExactlyInAnyOrder("Vaccination", "Treatment");
    }

    @Test
    @Transactional
    void shouldFindPreventiveCareById() {
        Owner owner = this.ownerService.findOwnerById(1);
        Pet pet = owner.getPet("Leo");

        PreventiveCare pc = new PreventiveCare();
        pc.setCareDate(LocalDate.of(2023, 3, 1));
        pc.setType("Diagnosis");
        pc.setDescription("Checkup");
        pet.addPreventiveCare(pc);
        this.preventiveCareService.savePreventiveCare(pc);

        PreventiveCare foundPc = this.preventiveCareService.findPreventiveCareById(pc.getId());
        assertThat(foundPc).isNotNull();
        assertThat(foundPc.getDescription()).isEqualTo("Checkup");
    }

    @Test
    @Transactional
    void shouldSavePreventiveCare() {
        Owner owner = this.ownerService.findOwnerById(1);
        Pet pet = owner.getPet("Leo");

        int initialCount = this.preventiveCareService.findPreventiveCaresByPetId(pet.getId()).size();

        PreventiveCare pc = new PreventiveCare();
        pc.setCareDate(LocalDate.of(2023, 4, 1));
        pc.setType("Vaccination");
        pc.setDescription("Distemper");
        pc.setPet(pet); // Explicitly set pet for saving

        this.preventiveCareService.savePreventiveCare(pc);
        assertThat(pc.getId()).isNotNull();

        Collection<PreventiveCare> preventiveCares = this.preventiveCareService.findPreventiveCaresByPetId(pet.getId());
        assertThat(preventiveCares).hasSize(initialCount + 1);
        assertThat(preventiveCares).contains(pc);
    }

    @Test
    @Transactional
    void shouldDeletePreventiveCare() {
        Owner owner = this.ownerService.findOwnerById(1);
        Pet pet = owner.getPet("Leo");

        PreventiveCare pc = new PreventiveCare();
        pc.setCareDate(LocalDate.of(2023, 5, 1));
        pc.setType("Treatment");
        pc.setDescription("Worming");
        pc.setPet(pet);
        this.preventiveCareService.savePreventiveCare(pc);

        int initialCount = this.preventiveCareService.findPreventiveCaresByPetId(pet.getId()).size();
        assertThat(initialCount).isGreaterThan(0);

        this.preventiveCareService.deletePreventiveCare(pc.getId());

        Collection<PreventiveCare> preventiveCares = this.preventiveCareService.findPreventiveCaresByPetId(pet.getId());
        assertThat(preventiveCares).hasSize(initialCount - 1);
        assertThat(preventiveCares).doesNotContain(pc);
    }
}
