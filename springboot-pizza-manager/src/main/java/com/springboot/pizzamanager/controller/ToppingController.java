package com.springboot.pizzamanager.controller;

import com.springboot.pizzamanager.model.Topping;
import com.springboot.pizzamanager.service.ToppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/toppings")
public class ToppingController {

    private final ToppingService toppingService;

    @Autowired
    public ToppingController(ToppingService toppingService) {
        this.toppingService = toppingService;
    }

    // Defines a GET endpoint to retrieve all toppings
    @GetMapping
    public List<Topping> getAllToppings() {
        return toppingService.findAllToppings();
    }

    // Defines a POST endpoint to creates a new topping
    // Returns Topping object
    @PostMapping
    public Topping addTopping(@RequestBody Topping topping) {
        return toppingService.addTopping(topping);
    }

    // Defines a DELETE endpoint to remove a topping by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTopping(@PathVariable Long id) {
        toppingService.deleteTopping(id);
        return ResponseEntity.ok().build();
    }

    // Defines a PUT endpoint to update the details of an existing topping by it's id
    @PutMapping("/{id}")
    public Topping updateTopping(@PathVariable Long id, @RequestBody Topping topping) {
        return toppingService.updateTopping(id, topping);
    }
}
