package com.springboot.pizzamanager.service;

import com.springboot.pizzamanager.dto.PizzaUpdateRequest;
import com.springboot.pizzamanager.model.Pizza;
import com.springboot.pizzamanager.model.Topping;
import com.springboot.pizzamanager.repository.PizzaRepository;
import com.springboot.pizzamanager.repository.ToppingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PizzaServiceTest {

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private ToppingRepository toppingRepository;

    @InjectMocks
    private PizzaService pizzaService;

    private Pizza pizza;
    private Topping topping1;
    private Topping topping2;
    private Set<Long> toppingIds;

    @BeforeEach
    void setUp() {
        // Initialize test data
        topping1 = new Topping("Mushrooms");
        topping1.setId(1L);
        topping2 = new Topping("Onions");
        topping2.setId(2L);
        
        toppingIds = new HashSet<>();
        toppingIds.add(topping1.getId());
        toppingIds.add(topping2.getId());

        pizza = new Pizza("Margherita");
        pizza.setId(1L);
        pizza.setToppings(new HashSet<>(Set.of(topping1, topping2)));
    }

    @Test
    void createPizzaWithToppings_Success() {
        when(toppingRepository.findById(1L)).thenReturn(Optional.of(topping1));
        when(toppingRepository.findById(2L)).thenReturn(Optional.of(topping2));
        when(pizzaRepository.save(any(Pizza.class))).thenReturn(pizza);

        Pizza result = pizzaService.createPizzaWithToppings(new Pizza("Margherita"), toppingIds);

        assertNotNull(result);
        assertEquals("Margherita", result.getName());
        assertTrue(result.getToppings().containsAll(Set.of(topping1, topping2)));
        verify(pizzaRepository).save(any(Pizza.class));
    }

    @Test
    void updatePizza_Success() {
        // Create a list from the set for topping IDs
        PizzaUpdateRequest request = new PizzaUpdateRequest("Margherita Updated", new ArrayList<>(List.of(1L, 2L)));

        when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
        when(toppingRepository.findById(1L)).thenReturn(Optional.of(topping1));
        when(toppingRepository.findById(2L)).thenReturn(Optional.of(topping2));
        when(pizzaRepository.save(any(Pizza.class))).thenReturn(pizza);

        Pizza updatedPizza = pizzaService.updatePizza(1L, request);

        assertNotNull(updatedPizza);
        assertEquals("Margherita Updated", updatedPizza.getName());
        assertTrue(updatedPizza.getToppings().containsAll(Set.of(topping1, topping2)));
    }


    @Test
    void deletePizza_Success() {
        when(pizzaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pizzaRepository).deleteById(1L);

        pizzaService.deletePizza(1L);

        verify(pizzaRepository).deleteById(1L);
    }

    @Test
    void createPizzaWithNonExistentTopping_ThrowsException() {
        Set<Long> invalidToppingIds = new HashSet<>(Set.of(99L));
        when(toppingRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> 
            pizzaService.createPizzaWithToppings(new Pizza("Margherita"), invalidToppingIds));

        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    void updatePizzaWithNonExistentId_ThrowsException() {
        // Change Set.of(1L) to List.of(1L) to match the constructor's expected parameters
        PizzaUpdateRequest updateRequest = new PizzaUpdateRequest("Margherita Updated", List.of(1L));
        when(pizzaRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> 
            pizzaService.updatePizza(99L, updateRequest));

        assertTrue(exception.getMessage().contains("does not exist"));
    }

 @Test
 void createPizza_FailsWhenNameExists() {
     when(pizzaRepository.existsByName("Margherita")).thenReturn(true);

     Exception exception = assertThrows(IllegalStateException.class, () -> 
         pizzaService.createPizzaWithToppings(new Pizza("Margherita"), toppingIds));

     assertTrue(exception.getMessage().contains("already exists"));
 }

 @Test
 void deletePizza_FailsWhenIdDoesNotExist() {
     when(pizzaRepository.existsById(anyLong())).thenReturn(false);

     Exception exception = assertThrows(IllegalStateException.class, () -> 
         pizzaService.deletePizza(99L));

     assertTrue(exception.getMessage().contains("does not exist"));
 }

 @Test
 void updatePizza_FailsWhenNewToppingDoesNotExist() {
     PizzaUpdateRequest request = new PizzaUpdateRequest("Margherita Updated", List.of(99L));
     when(pizzaRepository.findById(1L)).thenReturn(Optional.of(pizza));
     when(toppingRepository.findById(99L)).thenReturn(Optional.empty());

     Exception exception = assertThrows(IllegalStateException.class, () -> 
         pizzaService.updatePizza(1L, request));

     assertTrue(exception.getMessage().contains("does not exist"));
 }

}
