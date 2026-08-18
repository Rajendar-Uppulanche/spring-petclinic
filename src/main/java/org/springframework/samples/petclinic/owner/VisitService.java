package org.springframework.samples.petclinic.owner;

import java.util.List;

/**
 * Service interface for managing {@link Visit} instances.
 * Provides methods for searching, retrieving, saving, and deleting visits.
 *
 * @author Synapse Builder
 */
public interface VisitService {
    List<Visit> findVisits(VisitSearchCriteriaDTO criteria);
    Visit findVisitById(int visitId);
    void saveVisit(Visit visit);
    void deleteVisit(int visitId);
}
