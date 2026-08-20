package com.example.petclinic.repository;

import com.example.petclinic.model.Owner;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface OwnerRepository extends CrudRepository<Owner, Integer> {

    Collection<Owner> findByLastName(String lastName);

    Optional<Owner> findById(Integer id);

    Optional<Owner> findByUserId(String userId);

    void delete(Owner owner);

    Owner save(Owner owner);
}