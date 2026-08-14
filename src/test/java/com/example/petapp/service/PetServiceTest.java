package com.example.petapp.service;

import com.example.petapp.model.Pet;
import com.example.petapp.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @InjectMocks
    private PetService petService;

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testSavePet_validBirthDate() {
        Pet pet = new Pet("Buddy", LocalDate.of(2020, 1, 1));
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        Pet savedPet = petService.savePet(pet);

        assertNotNull(savedPet);
        assertEquals("Buddy", savedPet.getName());
        assertEquals(LocalDate.of(2020, 1, 1), savedPet.getBirthDate());
    }

    @Test
    void testSavePet_futureBirthDate_shouldThrowConstraintViolationException() {
        Pet pet = new Pet("FutureDog", LocalDate.now().plusDays(1));

        // Manually validate the pet object to simulate what happens before persistence
        // or if the service layer explicitly validates.
        // In a real Spring Boot app, this would often be caught by the controller
        // or a global exception handler after the repository save attempt.
        Set<ConstraintViolation<Pet>> violations = validator.validate(pet);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<Pet> violation = violations.iterator().next();
        assertEquals("birthDate", violation.getPropertyPath().toString());
        assertEquals("Pet birth date cannot be in the future.", violation.getMessage());
    }

    @Test
    void testSavePet_nullBirthDate_shouldThrowConstraintViolationException() {
        Pet pet = new Pet("NoDateDog", null);

        Set<ConstraintViolation<Pet>> violations = validator.validate(pet);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        ConstraintViolation<Pet> violation = violations.iterator().next();
        assertEquals("birthDate", violation.getPropertyPath().toString());
        assertEquals("Pet birth date cannot be null.", violation.getMessage());
    }
}