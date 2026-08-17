package org.springframework.samples.petclinic.visit;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository class for `Visit` domain objects.
 */
@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

	/**
	 * Retrieve all `Visit`s from the data store.
	 * @return a `List` of `Visit`s
	 */
	List<Visit> findAll();

	/**
	 * Retrieve `Visit`s from the data store by various criteria.
	 * @param startDate Optional start date for filtering visits.
	 * @param endDate Optional end date for filtering visits.
	 * @param petId Optional pet ID for filtering visits.
	 * @param ownerId Optional owner ID for filtering visits.
	 * @param vetId Optional veterinarian ID for filtering visits.
	 * @return a `List` of `Visit`s matching the criteria.
	 */
	@Query("SELECT v FROM Visit v LEFT JOIN FETCH v.pet p LEFT JOIN FETCH p.owner o LEFT JOIN FETCH v.vet vt " +
		   "WHERE (:startDate IS NULL OR v.date >= :startDate) " +
		   "AND (:endDate IS NULL OR v.date <= :endDate) " +
		   "AND (:petId IS NULL OR p.id = :petId) " +
		   "AND (:ownerId IS NULL OR o.id = :ownerId) " +
		   "AND (:vetId IS NULL OR vt.id = :vetId)")
	List<Visit> findVisitsByCriteria(@Param("startDate") LocalDate startDate,
								 @Param("endDate") LocalDate endDate,
								 @Param("petId") Integer petId,
								 @Param("ownerId") Integer ownerId,
								 @Param("vetId") Integer vetId);

	List<Visit> findByPetId(Integer petId);
}
