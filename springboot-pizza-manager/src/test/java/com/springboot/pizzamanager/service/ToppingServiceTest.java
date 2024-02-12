package com.springboot.pizzamanager.service;

import com.springboot.pizzamanager.model.Topping;
import com.springboot.pizzamanager.repository.ToppingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ToppingServiceTest {

    @Mock
    private ToppingRepository toppingRepository;

    @InjectMocks
    private ToppingService toppingService;

    private Topping topping;

    @BeforeEach
    void setUp() {
        topping = new Topping("Cheese");
        topping.setId(1L);
    }

    @Test
    void findAllToppings_Success() {
        when(toppingRepository.findAll()).thenReturn(List.of(topping));
        List<Topping> toppings = toppingService.findAllToppings();
        assertFalse(toppings.isEmpty());
        assertEquals("Cheese", toppings.get(0).getName());
    }

    @Test
    void findToppingById_Success() {
        when(toppingRepository.findById(1L)).thenReturn(Optional.of(topping));
        Optional<Topping> foundTopping = toppingService.findToppingById(1L);
        assertTrue(foundTopping.isPresent());
        assertEquals("Cheese", foundTopping.get().getName());
    }

    @Test
    void addTopping_Success() {
        when(toppingRepository.existsByName(anyString())).thenReturn(false);
        when(toppingRepository.save(any(Topping.class))).thenReturn(topping);
        Topping savedTopping = toppingService.addTopping(new Topping("Cheese"));
        assertNotNull(savedTopping);
        assertEquals("Cheese", savedTopping.getName());
    }

    @Test
    void deleteTopping_Success() {
        when(toppingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(toppingRepository).deleteById(1L);
        toppingService.deleteTopping(1L);
        verify(toppingRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateTopping_Success() {
        when(toppingRepository.findById(1L)).thenReturn(Optional.of(topping));
        when(toppingRepository.save(any(Topping.class))).thenReturn(topping);
        Topping updatedTopping = new Topping("Updated Cheese");
        updatedTopping.setId(1L);
        Topping result = toppingService.updateTopping(1L, updatedTopping);
        assertNotNull(result);
        assertEquals("Updated Cheese", result.getName());
    }

    @Test
    void addTopping_ThrowsExceptionWhenToppingExists() {
        when(toppingRepository.existsByName(anyString())).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> toppingService.addTopping(new Topping("Cheese")),
            "Topping already exists.");
    }
    
    @Test
    void deleteTopping_FailsWhenIdDoesNotExist() {
        when(toppingRepository.existsById(anyLong())).thenReturn(false);

        Exception exception = assertThrows(IllegalStateException.class, () ->
            toppingService.deleteTopping(99L));

        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    void updateTopping_FailsWhenIdDoesNotExist() {
        Topping updatedTopping = new Topping("Updated Cheese");
        updatedTopping.setId(99L); // Use a non-existent ID for this test
        when(toppingRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () ->
            toppingService.updateTopping(99L, updatedTopping));

        assertTrue(exception.getMessage().contains("does not exist"));
    }

    
}
