package com.springboot.pizzamanager.controller;

import com.springboot.pizzamanager.dto.PizzaRequest;
import com.springboot.pizzamanager.dto.PizzaResponse;
import com.springboot.pizzamanager.dto.PizzaUpdateRequest;
import com.springboot.pizzamanager.dto.ToppingDTO;
import com.springboot.pizzamanager.model.Pizza;
import com.springboot.pizzamanager.model.Topping;
import com.springboot.pizzamanager.service.PizzaService;
import com.springboot.pizzamanager.service.ToppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/pizzas")
public class PizzaController {

    private final PizzaService pizzaService;
    private final ToppingService toppingService;

    @Autowired
    public PizzaController(PizzaService pizzaService, ToppingService toppingService) {
        this.pizzaService = pizzaService;
        this.toppingService = toppingService;
    }

    // Defines a GET endpoint to retrieve a pizza by its ID
    // Returns a PizzaResponse DTO containing the pizza's details, including its toppings converted to ToppingDTO objects.
    @GetMapping("/{id}")
    public ResponseEntity<PizzaResponse> getPizzaById(@PathVariable Long id) {
        Pizza pizza = pizzaService.findPizzaById(id)
                .orElseThrow(() -> new RuntimeException("Pizza not found"));

        // convert Topping entities to ToppingDTO objects
        Set<ToppingDTO> toppingDTOs = pizza.getToppings().stream()
                .map(topping -> new ToppingDTO(topping.getId(), topping.getName()))
                .collect(Collectors.toSet());

        PizzaResponse response = new PizzaResponse(pizza.getId(), pizza.getName(), toppingDTOs);

        return ResponseEntity.ok(response);
    }
    
    // Defines a GET endpoint to retrieve all pizzas.
    // Returns a list of PizzaResponse DTOs for all pizzas in the database.
    @GetMapping
    public ResponseEntity<List<PizzaResponse>> getAllPizzas() {
        List<Pizza> pizzas = pizzaService.findAllPizzas();
        List<PizzaResponse> responses = pizzas.stream()
                .map(pizza -> {
                    Set<ToppingDTO> toppingDTOs = pizza.getToppings().stream()
                            .map(topping -> new ToppingDTO(topping.getId(), topping.getName()))
                            .collect(Collectors.toSet());
                    return new PizzaResponse(pizza.getId(), pizza.getName(), toppingDTOs);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // Defines a POST endpoint to create a new pizza with specified toppings
    // Accepts a PizzaRequest DTO, creates a new pizza, and returns a PizzaResponse DTO with the created pizza's details.
    @PostMapping
    public ResponseEntity<?> createPizzaWithToppings(@RequestBody PizzaRequest pizzaRequest) {
        try {
            Pizza pizza = new Pizza();
            pizza.setName(pizzaRequest.getName());

            Pizza newPizza = pizzaService.createPizzaWithToppings(pizza, pizzaRequest.getToppingIds());
            
            // Convert Pizza entity to PizzaResponse DTO with ToppingDTO objects
            Set<ToppingDTO> toppingDTOs = newPizza.getToppings().stream()
                                               .map(topping -> new ToppingDTO(topping.getId(), topping.getName()))
                                               .collect(Collectors.toSet());
            PizzaResponse pizzaResponse = new PizzaResponse(newPizza.getId(), newPizza.getName(), toppingDTOs);

            return new ResponseEntity<>(pizzaResponse, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while creating the pizza.");
        }
    }

    // Defines a DELETE endpoint to remove a pizza by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePizza(@PathVariable Long id) {
        pizzaService.deletePizza(id);
        return ResponseEntity.ok().build();
    }

    // Defines a PUT endpoint to update an existing pizza's name and/or toppings based on a PizzaUpdateRequest DTO
    @PutMapping("/{id}")
    public ResponseEntity<PizzaResponse> updatePizza(@PathVariable Long id, @RequestBody PizzaUpdateRequest updateRequest) {
        Pizza updatedPizza = pizzaService.updatePizza(id, updateRequest);
        // Conversion of Pizza to PizzaResponse
        PizzaResponse response = convertToPizzaResponse(updatedPizza);
        return ResponseEntity.ok(response);
    }
    
    private PizzaResponse convertToPizzaResponse(Pizza pizza) {
        Set<ToppingDTO> toppingDTOs = pizza.getToppings().stream()
                .map(topping -> new ToppingDTO(topping.getId(), topping.getName()))
                .collect(Collectors.toSet());
        return new PizzaResponse(pizza.getId(), pizza.getName(), toppingDTOs);
    }

    // Defines a POST endpoint to add a topping to a pizza
    // Takes the pizza's ID and the topping's ID, adds the topping to the pizza, and returns the updated pizza
    @PostMapping("/{pizzaId}/toppings/{toppingId}")
    public Pizza addToppingToPizza(@PathVariable Long pizzaId, @PathVariable Long toppingId) {
        Topping topping = toppingService.findToppingById(toppingId)
                .orElseThrow(() -> new IllegalArgumentException("Topping not found"));
        return pizzaService.addToppingToPizza(pizzaId, topping);
    }

}
