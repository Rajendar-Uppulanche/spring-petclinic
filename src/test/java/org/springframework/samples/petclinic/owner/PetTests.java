package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PetTests {

    @Test
    void getNameReturnsCorrectNameWhenNotNull() {
        Pet pet = new Pet();
        pet.setName("Buddy");
        assertThat(pet.getName()).isEqualTo("Buddy");
    }

    @Test
    void getNameReturnsEmptyStringWhenNameIsNull() {
        Pet pet = new Pet();
        pet.setName(null);
        assertThat(pet.getName()).isEqualTo("");
    }

    @Test
    void getNameReturnsEmptyStringForNewPetWithDefaultNullName() {
        Pet pet = new Pet(); // Name field should default to null
        assertThat(pet.getName()).isEqualTo("");
    }
}
