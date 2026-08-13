package org.springframework.samples.petclinic.vet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface VetService {

    Collection<Vet> findAllVets();

    Page<Vet> findVetsPaginated(Pageable pageable);

}
