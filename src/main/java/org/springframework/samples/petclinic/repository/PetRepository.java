package org.springframework.samples.petclinic.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;

import java.util.List;

public interface PetRepository extends Repository<Pet, Integer> {

    void save(Pet pet);

    List<Pet> findByOwnerId(Integer ownerId);

    Pet findById(Integer id);

    List<PetType> findPetTypes();

    // New DTO for pet with visit count
    public static class PetWithVisitCount {
        private Pet pet;
        private Long visitCount;

        public PetWithVisitCount(Pet pet, Long visitCount) {
            this.pet = pet;
            this.visitCount = visitCount;
        }

        public Pet getPet() {
            return pet;
        }

        public Long getVisitCount() {
            return visitCount;
        }
    }

    // Custom query to fetch pets with their visit counts
    @Query("SELECT new org.springframework.samples.petclinic.repository.PetRepository$PetWithVisitCount(p, COUNT(v)) " +
           "FROM Pet p LEFT JOIN p.visits v WHERE p.owner.id = :ownerId GROUP BY p")
    List<PetWithVisitCount> findByOwnerIdWithVisitCount(@Param("ownerId") Integer ownerId);
}