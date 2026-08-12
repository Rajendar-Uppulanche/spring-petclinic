package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

    /**
     * Retrieve all visits for a specific pet, optionally filtered by date range and keyword.
     * @param petId the ID of the pet
     * @param fromDate optional start date for filtering (inclusive)
     * @param toDate optional end date for filtering (inclusive)
     * @param keyword optional keyword to search in description (case-insensitive)
     * @return a Collection of Visits
     */
    @Query("SELECT visit FROM Visit visit WHERE visit.pet.id = :petId " +
           "AND (:fromDate IS NULL OR visit.date >= :fromDate) " +
           "AND (:toDate IS NULL OR visit.date <= :toDate) " +
           "AND (:keyword IS NULL OR LOWER(visit.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Collection<Visit> findByPetIdAndFilters(@Param("petId") Integer petId,
                                            @Param("fromDate") LocalDate fromDate,
                                            @Param("toDate") LocalDate toDate,
                                            @Param("keyword") String keyword);

    /**
     * Retrieve all visits for a specific pet.
     * @param petId the ID of the pet
     * @return a Collection of Visits
     */
    Collection<Visit> findByPetId(Integer petId);
}
