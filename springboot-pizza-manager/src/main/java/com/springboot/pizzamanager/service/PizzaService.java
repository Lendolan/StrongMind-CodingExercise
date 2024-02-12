package com.springboot.pizzamanager.service;

import com.springboot.pizzamanager.dto.PizzaUpdateRequest;
import com.springboot.pizzamanager.model.Pizza;
import com.springboot.pizzamanager.model.Topping;
import com.springboot.pizzamanager.repository.PizzaRepository;
import com.springboot.pizzamanager.repository.ToppingRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PizzaService {

    private final PizzaRepository pizzaRepository;
    private final ToppingRepository toppingRepository;

    @Autowired
    public PizzaService(PizzaRepository pizzaRepository, ToppingRepository toppingRepository) {
        this.pizzaRepository = pizzaRepository;
        this.toppingRepository = toppingRepository;
    }

    // Fetches all pizzas from the database using PizzaRepository
    public List<Pizza> findAllPizzas() {
        return pizzaRepository.findAll();
    }

    // Retrieves a pizza by its ID
    public Optional<Pizza> findPizzaById(Long id) {
        return pizzaRepository.findById(id);
    }

    // Creates a new pizza with associated toppings
    @Transactional
    public Pizza createPizzaWithToppings(Pizza pizza, Set<Long> toppingIds) {
        // Check if the pizza name already exists
        if (pizzaRepository.existsByName(pizza.getName())) {
            throw new IllegalStateException("Pizza with name " + pizza.getName() + " already exists.");
        }

        // Fetch and associate toppings
        Set<Topping> toppings = new HashSet<>();
        for (Long id : toppingIds) {
            Topping topping = toppingRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Topping with id " + id + " does not exist."));
            toppings.add(topping);
        }

        pizza.setToppings(toppings);
        return pizzaRepository.save(pizza);
    }
    
    // Deletes a pizza by its ID after checking if it exists
    @Transactional
    public void deletePizza(Long id) {
        if (!pizzaRepository.existsById(id)) {
            throw new IllegalStateException("Pizza with id " + id + " does not exist.");
        }
        pizzaRepository.deleteById(id);
    }

    // Updates a pizza's name and toppings
    @Transactional
    public Pizza updatePizza(Long id, PizzaUpdateRequest updateRequest) {
        Pizza pizza = pizzaRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Pizza with id " + id + " does not exist."
                ));

        String newName = updateRequest.getName();
        if (newName != null && newName.length() > 0 && !pizza.getName().equals(newName)) {
            pizza.setName(newName);
        }

        // Handle topping updates
        Set<Topping> updatedToppings = updateRequest.getToppingIds().stream()
                .map(toppingId -> toppingRepository.findById(toppingId)
                        .orElseThrow(() -> new IllegalStateException("Topping with id " + toppingId + " does not exist.")))
                .collect(Collectors.toSet());

        pizza.setToppings(updatedToppings);

        return pizzaRepository.save(pizza);
    }

    // Adds a topping to a pizza, ensuring the pizza exists before updating it
    @Transactional
    public Pizza addToppingToPizza(Long pizzaId, Topping topping) {
        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new IllegalStateException(
                        "Pizza with id " + pizzaId + " does not exist."
                ));
        pizza.addTopping(topping);
        return pizzaRepository.save(pizza);
    }

    // Removes a topping from a pizza, it ensures the pizza exists before modifying its toppings list
    @Transactional
    public Pizza removeToppingFromPizza(Long pizzaId, Topping topping) {
        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new IllegalStateException(
                        "Pizza with id " + pizzaId + " does not exist."
                ));
        pizza.removeTopping(topping);
        return pizzaRepository.save(pizza);
    }
}
