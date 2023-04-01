package org.abbaspour.catapi.repository;

import org.abbaspour.catapi.model.Cat;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatRepo extends PagingAndSortingRepository<Cat, Long> {

    Optional<Cat> findCatById(Long id);

    List<Cat> findAll();
}
