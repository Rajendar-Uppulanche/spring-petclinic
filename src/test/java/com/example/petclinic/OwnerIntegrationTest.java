package com.example.petclinic;

import com.example.petclinic.model.Owner;
import com.example.petclinic.model.Pet;
import com.example.petclinic.model.PetType;
import com.example.petclinic.model.Visit;
import com.example.petclinic.repository.OwnerRepository;
import com.example.petclinic.repository.PetRepository;
import com.example.petclinic.repository.PetTypeRepository;
import com.example.petclinic.repository.VisitRepository;
import com.example.petclinic.service.VisitService;
import com.example.petclinic.service.dto.OwnerDetailsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class OwnerIntegrationTest {

    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PetTypeRepository petTypeRepository;
    @Autowired
    private VisitRepository visitRepository;

    @SpyBean // Use SpyBean to verify method calls on the actual service
    private VisitService visitService;

    @Autowired
    private com.example.petclinic.service.OwnerService ownerService; // The service under test

    private Owner testOwner;
    private Pet testPet1;
    private Pet testPet2;

    @BeforeEach
    void setUp() {
        // Clear repositories (for clean test state, though @Transactional will rollback)
        visitRepository.deleteAll();
        petRepository.deleteAll();
        ownerRepository.deleteAll();
        petTypeRepository.deleteAll();

        // Setup PetType
        PetType dog = new PetType();
        dog.setName("dog");
        petTypeRepository.save(dog);

        // Setup Owner
        testOwner = new Owner();
        testOwner.setFirstName("Integration");
        testOwner.setLastName("Test");
        testOwner.setAddress("123 Test St");
        testOwner.setCity("Testville");
        testOwner.setTelephone("111-222-3333");
        ownerRepository.save(testOwner);

        // Setup Pets for the owner
        testPet1 = new Pet();
        testPet1.setName("Buddy");
        testPet1.setBirthDate(LocalDate.of(2020, 1, 1));
        testPet1.setType(dog);
        testPet1.setOwner(testOwner);
        petRepository.save(testPet1);

        testPet2 = new Pet();
        testPet2.setName("Lucy");
        testPet2.setBirthDate(LocalDate.of(2021, 5, 10));
        testPet2.setType(dog);
        testPet2.setOwner(testOwner);
        petRepository.save(testPet2);

        // Setup Visits for pets
        Visit visit1 = new Visit();
        visit1.setDate(LocalDate.now());
        visit1.setDescription("Checkup for Buddy");
        visit1.setPet(testPet1);
        visitRepository.save(visit1);

        Visit visit2 = new Visit();
        visit2.setDate(LocalDate.now().minusDays(7));
        visit2.setDescription("Vaccination for Buddy");
        visit2.setPet(testPet1);
        visitRepository.save(visit2);

        // No visits for Lucy
    }

    @Test
    void getOwnerDetailsById_shouldFetchVisitCountsEfficiently_Scenario60() {
        // When
        Optional<OwnerDetailsDTO> ownerDetailsOptional = ownerService.findOwnerDetailsById(testOwner.getId());

        // Then
        assertTrue(ownerDetailsOptional.isPresent(), "Owner details should be found");
        OwnerDetailsDTO dto = ownerDetailsOptional.get();

        assertEquals(testOwner.getFirstName(), dto.getFirstName());
        assertEquals(2, dto.getPets().size(), "Should have two pets");

        // Verify visit counts
        dto.getPets().forEach(petDto -> {
            if (petDto.getId().equals(testPet1.getId())) {
                assertEquals(2L, petDto.getVisitCount(), "Buddy should have 2 visits");
            } else if (petDto.getId().equals(testPet2.getId())) {
                assertEquals(0L, petDto.getVisitCount(), "Lucy should have 0 visits");
            } else {
                fail("Unexpected pet found: " + petDto.getName());
            }
        });

        // Crucial N+1 check: verify that countVisitsByPetForOwner was called exactly once
        // This ensures that visit counts for all pets of an owner are fetched in a single query.
        verify(visitService, times(1)).countVisitsByPetForOwner(testOwner.getId());
    }
}