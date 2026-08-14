package org.springframework.samples.petclinic.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class PreventiveCareTests {

    @Test
    void testSetAndGetCareDate() {
        PreventiveCare preventiveCare = new PreventiveCare();
        LocalDate date = LocalDate.of(2023, 1, 15);
        preventiveCare.setCareDate(date);
        assertThat(preventiveCare.getCareDate()).isEqualTo(date);
    }

    @Test
    void testSetAndGetType() {
        PreventiveCare preventiveCare = new PreventiveCare();
        preventiveCare.setType("Vaccination");
        assertThat(preventiveCare.getType()).isEqualTo("Vaccination");
    }

    @Test
    void testSetAndGetDescription() {
        PreventiveCare preventiveCare = new PreventiveCare();
        preventiveCare.setDescription("Rabies Vaccine");
        assertThat(preventiveCare.getDescription()).isEqualTo("Rabies Vaccine");
    }

    @Test
    void testSetAndGetNotes() {
        PreventiveCare preventiveCare = new PreventiveCare();
        preventiveCare.setNotes("Annual booster shot.");
        assertThat(preventiveCare.getNotes()).isEqualTo("Annual booster shot.");
    }

    @Test
    void testSetAndGetPet() {
        PreventiveCare preventiveCare = new PreventiveCare();
        Pet pet = new Pet();
        pet.setName("Leo");
        preventiveCare.setPet(pet);
        assertThat(preventiveCare.getPet()).isEqualTo(pet);
        assertThat(preventiveCare.getPet().getName()).isEqualTo("Leo");
    }

    @Test
    void testToString() {
        PreventiveCare preventiveCare = new PreventiveCare();
        preventiveCare.setCareDate(LocalDate.of(2023, 1, 15));
        preventiveCare.setType("Vaccination");
        preventiveCare.setDescription("Rabies Vaccine");
        Pet pet = new Pet();
        pet.setName("Leo");
        preventiveCare.setPet(pet);
        String expected = "PreventiveCare{careDate=2023-01-15, type='Vaccination', description='Rabies Vaccine', notes='null', pet=Leo}";
        assertThat(preventiveCare.toString()).isEqualTo(expected);
    }
}
