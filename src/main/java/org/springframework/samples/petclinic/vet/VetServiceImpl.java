package org.springframework.samples.petclinic.vet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;

    public VetServiceImpl(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Vet> findAllVets() {
        return vetRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Vet> findVetsPaginated(Pageable pageable) {
        return vetRepository.findAll(pageable);
    }
}
