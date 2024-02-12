package com.springboot.pizzamanager.service;

import com.springboot.pizzamanager.model.Topping;
import com.springboot.pizzamanager.repository.ToppingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ToppingService {

    private final ToppingRepository toppingRepository;

    @Autowired
    public ToppingService(ToppingRepository toppingRepository) {
        this.toppingRepository = toppingRepository;
    }

    // Retrieves all topping records from the database
    public List<Topping> findAllToppings() {
        return toppingRepository.findAll();
    }

    // Searches for a single topping by its ID
    public Optional<Topping> findToppingById(Long id) {
        return toppingRepository.findById(id);
    }

    // Adds a new topping to the database
    @Transactional
    public Topping addTopping(Topping topping) {
    	// Check if the topping already exists
        if (toppingRepository.existsByName(topping.getName())) {
            throw new IllegalStateException("Topping already exists.");
        }
        return toppingRepository.save(topping);
    }

    // Deletes a topping by its ID after checking if the topping exists in the database
    @Transactional
    public void deleteTopping(Long id) {
        if (!toppingRepository.existsById(id)) {
            throw new IllegalStateException("Topping with id " + id + " does not exist.");
        }
        toppingRepository.deleteById(id);
    }

    // Updates an existing topping's details, fetches the topping by ID, checks if it exists, and then updates
    @Transactional
    public Topping updateTopping(Long id, Topping updatedTopping) {
        Topping topping = toppingRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Topping with id " + id + " does not exist."
                ));

        String newName = updatedTopping.getName();
        if (newName != null && newName.length() > 0 && !topping.getName().equals(newName)) {
            topping.setName(newName);
        }
        

        return toppingRepository.save(topping);
    }
}
