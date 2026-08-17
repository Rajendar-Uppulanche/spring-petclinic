package org.springframework.samples.petclinic.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

	Collection<Appointment> findByDate(LocalDate date);

	@Query("SELECT a FROM Appointment a WHERE LOWER(a.pet.name) LIKE LOWER(CONCAT('%', :petName, '%'))")
	Collection<Appointment> findByPetNameContainingIgnoreCase(@Param("petName") String petName);

	@Query("SELECT a FROM Appointment a WHERE LOWER(a.pet.owner.lastName) LIKE LOWER(CONCAT('%', :ownerLastName, '%'))")
	Collection<Appointment> findByOwnerLastNameContainingIgnoreCase(@Param("ownerLastName") String ownerLastName);

	@Query("SELECT a FROM Appointment a WHERE LOWER(a.vet.lastName) LIKE LOWER(CONCAT('%', :vetLastName, '%'))")
	Collection<Appointment> findByVetLastNameContainingIgnoreCase(@Param("vetLastName") String vetLastName);

	Collection<Appointment> findByStatus(AppointmentStatus status);

}
