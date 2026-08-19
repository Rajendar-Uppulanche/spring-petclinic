package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, Integer> {

    /**
     * Retrieve all vaccinations due within a specific date range.
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a List of Vaccinations
     */
    @Query("SELECT v FROM Vaccination v WHERE v.dueDate BETWEEN :startDate AND :endDate")
    List<Vaccination> findByDueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Retrieve all vaccinations for a specific pet.
     * @param petId the ID of the pet
     * @return a List of Vaccinations
     */
    List<Vaccination> findByPetId(Integer petId);
}