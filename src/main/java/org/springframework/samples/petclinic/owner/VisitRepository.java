package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Integer> {

    /**
     * Save a {@link Visit} to the data store.
     * @param visit the {@link Visit} to save
     */
    @Override
    void save(Visit visit);

    /**
     * Retrieve all {@link Visit}s from the data store for a specific {@link Pet},
     * ordered by visit date, descending.
     * @param petId the Pet's ID
     * @return a {@link List} of {@link Visit}s
     */
    List<Visit> findByPetId(Integer petId);

    /**
     * Retrieve all {@link Visit}s from the data store for a specific {@link Owner},
     * optionally filtered by date range and keyword in description.
     *
     * @param ownerId the Owner's ID
     * @param fromDate optional start date for filtering (inclusive)
     * @param toDate optional end date for filtering (inclusive)
     * @param keyword optional keyword to search in visit description (case-insensitive)
     * @return a {@link List} of {@link Visit}s
     */
    @Query("SELECT v FROM Visit v JOIN v.pet p JOIN p.owner o WHERE o.id = :ownerId " +
           "AND (:fromDate IS NULL OR v.date >= :fromDate) " +
           "AND (:toDate IS NULL OR v.date <= :toDate) " +
           "AND (:keyword IS NULL OR LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    @Transactional(readOnly = true)
    List<Visit> findByOwnerIdAndFilters(
        @Param("ownerId") Integer ownerId,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate,
        @Param("keyword") String keyword
    );
}
