package org.springframework.samples.petclinic.owner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer>, JpaSpecificationExecutor<Visit> {

    List<Visit> findByPetId(Integer petId);

}