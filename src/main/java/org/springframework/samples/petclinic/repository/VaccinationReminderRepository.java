package org.springframework.samples.petclinic.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.model.Pet;

public interface VaccinationReminderRepository extends Repository<Pet, Integer> {

	/**
	 * Retrieve all pets whose vaccination due date is within a specified range,
	 * and whose owner has not unsubscribed from reminders.
	 * @param startDate the start of the due date range (inclusive)
	 * @param endDate the end of the due date range (inclusive)
	 * @return a List of Pets
	 */
	@Query("SELECT pet FROM Pet pet WHERE pet.vaccinationDueDate BETWEEN :startDate AND :endDate AND pet.owner.unsubscribePreference = false")
	List<Pet> findPetsWithUpcomingVaccinations(LocalDate startDate, LocalDate endDate);

}