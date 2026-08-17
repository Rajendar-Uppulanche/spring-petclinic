package org.springframework.samples.petclinic.owner;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PetTests {

    @Test
    void getNameReturnsEmptyStringWhenNameIsNull() {
        Pet pet = new Pet();
        // Assuming setName(null) is possible or name is null by default
        // NamedEntity's setName is not provided, so we assume it can be set to null
        // or is null by default if not set. For explicit testing, we set it.
        pet.setName(null);

        assertEquals("", pet.getName(), "getName() should return an empty string when the name is null");
    }

    @Test
    void getNameReturnsCorrectNameWhenNameIsNotNull() {
        Pet pet = new Pet();
        String expectedName = "Buddy";
        pet.setName(expectedName);

        assertEquals(expectedName, pet.getName(), "getName() should return the correct name when it is not null");
    }
}
