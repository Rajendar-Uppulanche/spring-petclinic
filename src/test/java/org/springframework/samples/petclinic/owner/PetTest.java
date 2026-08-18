package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PetTest {

    @Test
    void getNameReturnsEmptyStringWhenNameIsNull() {
        Pet pet = new Pet();
        pet.setName(null); // Assuming setName is inherited from NamedEntity
        assertEquals("", pet.getName(), "getName should return an empty string when name is null");
    }

    @Test
    void getNameReturnsCorrectNameWhenNameIsNotNull() {
        Pet pet = new Pet();
        String petName = "Buddy";
        pet.setName(petName);
        assertEquals(petName, pet.getName(), "getName should return the correct name when not null");
    }
}
