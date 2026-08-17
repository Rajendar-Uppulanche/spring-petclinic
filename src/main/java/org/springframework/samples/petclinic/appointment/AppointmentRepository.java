package org.springframework.samples.petclinic.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.pet.owner.lastName LIKE %:ownerLastName%")
    List<Appointment> findByOwnerLastNameContaining(@Param("ownerLastName") String ownerLastName);

    @Query("SELECT a FROM Appointment a WHERE a.pet.name LIKE %:petName%")
    List<Appointment> findByPetNameContaining(@Param("petName") String petName);

    @Query("SELECT a FROM Appointment a WHERE " +
           "(:startDateTime IS NULL OR a.appointmentDateTime >= :startDateTime) AND " +
           "(:endDateTime IS NULL OR a.appointmentDateTime <= :endDateTime) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:ownerLastName IS NULL OR a.pet.owner.lastName LIKE %:ownerLastName%) AND " +
           "(:petName IS NULL OR a.pet.name LIKE %:petName%)")
    List<Appointment> findAppointmentsByCriteria(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") AppointmentStatus status,
            @Param("ownerLastName") String ownerLastName,
            @Param("petName") String petName);
}
