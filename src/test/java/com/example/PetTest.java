package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PetTest {

    @Test
    void testGetNameReturnsCorrectName() {
        Pet pet = new Pet("Buddy", "Dog", 5);
        assertEquals("Buddy", pet.getName(), "getName should return the correct name when set.");
    }

    @Test
    void testGetNameReturnsEmptyStringWhenNameIsNull() {
        Pet pet = new Pet(null, "Cat", 3);
        assertEquals("", pet.getName(), "getName should return an empty string when name is null.");
    }

    @Test
    void testSetNameUpdatesNameCorrectly() {
        Pet pet = new Pet("Max", "Dog", 2);
        pet.setName("Daisy");
        assertEquals("Daisy", pet.getName(), "setName should update the pet's name.");
    }

    @Test
    void testGetSpeciesReturnsCorrectSpecies() {
        Pet pet = new Pet("Buddy", "Dog", 5);
        assertEquals("Dog", pet.getSpecies(), "getSpecies should return the correct species.");
    }

    @Test
    void testSetSpeciesUpdatesSpeciesCorrectly() {
        Pet pet = new Pet("Max", "Dog", 2);
        pet.setSpecies("Cat");
        assertEquals("Cat", pet.getSpecies(), "setSpecies should update the pet's species.");
    }

    @Test
    void testGetAgeReturnsCorrectAge() {
        Pet pet = new Pet("Buddy", "Dog", 5);
        assertEquals(5, pet.getAge(), "getAge should return the correct age.");
    }

    @Test
    void testSetAgeUpdatesAgeCorrectly() {
        Pet pet = new Pet("Max", "Dog", 2);
        pet.setAge(3);
        assertEquals(3, pet.getAge(), "setAge should update the pet's age.");
    }
}