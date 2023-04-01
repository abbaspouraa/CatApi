package org.abbaspour.catapi.controller;

import org.abbaspour.catapi.model.Cat;
import org.abbaspour.catapi.service.CatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cat")
public class CatController {

    @Autowired
    CatService catService;

    @GetMapping("/single/{id}")
    public ResponseEntity<Cat> getCat(
            @PathVariable Long id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(catService.getCat(id));
    }

    @PostMapping("/new/")
    public ResponseEntity<Cat> createCat(
            @Valid @RequestBody Cat cat
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(catService.addCat(cat));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCat(
            @PathVariable Long id
    ) {
        catService.deleteCat(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/list/")
    public ResponseEntity<List<Cat>> GetCatList() {
        return ResponseEntity.status(HttpStatus.OK).body(catService.getAll());
    }
}
