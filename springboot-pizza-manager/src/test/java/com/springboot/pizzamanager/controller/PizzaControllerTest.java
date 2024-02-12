package com.springboot.pizzamanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.pizzamanager.dto.PizzaUpdateRequest;
import com.springboot.pizzamanager.model.Pizza;
import com.springboot.pizzamanager.service.PizzaService;
import com.springboot.pizzamanager.service.ToppingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PizzaController.class)
class PizzaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PizzaService pizzaService;
    
    @MockBean
    private ToppingService toppingService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void getPizzaById_Success() throws Exception {
        Pizza pizza = new Pizza();
        pizza.setId(1L);
        pizza.setName("Margherita");
        pizza.setToppings(new HashSet<>());

        given(pizzaService.findPizzaById(1L)).willReturn(Optional.of(pizza));

        mockMvc.perform(get("/api/pizzas/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Margherita"));
    }

    @Test
    void getAllPizzas_Success() throws Exception {
        Pizza pizza1 = new Pizza("Margherita");
        pizza1.setId(1L);
        Pizza pizza2 = new Pizza("Pepperoni");
        pizza2.setId(2L);

        given(pizzaService.findAllPizzas()).willReturn(Arrays.asList(pizza1, pizza2));

        mockMvc.perform(get("/api/pizzas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Margherita", "Pepperoni")));
    }

    @Test
    void deletePizza_Success() throws Exception {
        doNothing().when(pizzaService).deletePizza(anyLong());

        mockMvc.perform(delete("/api/pizzas/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updatePizza_Success() throws Exception {
        PizzaUpdateRequest updateRequest = new PizzaUpdateRequest("Margherita Updated", Arrays.asList(1L, 2L));
        Pizza updatedPizzaEntity = new Pizza();
        updatedPizzaEntity.setId(1L);
        updatedPizzaEntity.setName("Margherita Updated");

        given(pizzaService.updatePizza(eq(1L), any(PizzaUpdateRequest.class))).willReturn(updatedPizzaEntity);

        mockMvc.perform(put("/api/pizzas/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Margherita Updated"));
    }


}

