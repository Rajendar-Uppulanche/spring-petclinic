package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreventiveCareRecordRepository extends JpaRepository<PreventiveCareRecord, Integer> {

    List<PreventiveCareRecord> findByPetId(Integer petId);

    List<PreventiveCareRecord> findByPetOwnerId(Integer ownerId);
}
