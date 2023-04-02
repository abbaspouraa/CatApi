package org.abbaspour.catapi.service;

import org.abbaspour.catapi.model.Cat;
import org.abbaspour.catapi.repository.CatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatService implements CarServiceInterface {

    @Autowired
    CatRepo catRepo;

    @Override
    public List<Cat> getAll() {
        return catRepo.findAll();
    }

    @Override
    public Cat getCat(Long id) {
        return  catRepo.findCatById(id).orElse(null);
    }

    @Override
    public Cat addCat(Cat cat) throws RuntimeException {
        if (
                catRepo.findCatByBirthDateAndNameAndOwnerAndFavoriteFood(
                        cat.getBirthDate(),
                        cat.getName(),
                        cat.getOwner(),
                        cat.getFavoriteFood()
                ).isPresent()
        ) {
            throw new RuntimeException();
        }
        return catRepo.save(cat);
    }

    @Override
    public void deleteCat(Long id) {
        catRepo.deleteById(id);
    }
}
