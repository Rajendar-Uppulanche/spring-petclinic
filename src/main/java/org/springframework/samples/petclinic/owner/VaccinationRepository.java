package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, Integer> {

    List<Vaccination> findByPetId(Integer petId);

    @Query("SELECT v FROM Vaccination v WHERE v.dueDate BETWEEN :startDate AND :endDate")
    List<Vaccination> findByDueDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}