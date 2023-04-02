package org.abbaspour.catapi.service;

import org.abbaspour.catapi.model.Cat;

import java.util.List;

public interface CarServiceInterface {
    List<Cat> getAll();

    Cat getCat(Long id);

    Cat addCat(Cat cat) throws RuntimeException;

    void deleteCat(Long id);
}
