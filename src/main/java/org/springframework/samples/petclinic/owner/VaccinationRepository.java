package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, Integer> {

    /**
     * Find vaccinations due within a specific date range.
     * @param startDate the start date of the range (inclusive)
     * @param endDate the end date of the range (inclusive)
     * @return a list of vaccinations due within the specified range
     */
    @Query("SELECT v FROM Vaccination v WHERE v.dueDate BETWEEN :startDate AND :endDate")
    List<Vaccination> findByDueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find all vaccinations for a specific pet.
     * @param pet the pet for which to find vaccinations
     * @return a list of vaccinations for the given pet
     */
    List<Vaccination> findByPet(Pet pet);
}
