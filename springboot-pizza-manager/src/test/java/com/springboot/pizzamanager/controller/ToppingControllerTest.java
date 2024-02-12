package com.springboot.pizzamanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.pizzamanager.model.Topping;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ToppingController.class)
public class ToppingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToppingService toppingService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Topping cheese = new Topping("Cheese");
        cheese.setId(1L);
        Topping tomatoSauce = new Topping("Tomato Sauce");
        tomatoSauce.setId(2L);
        Topping basil = new Topping("Basil");
        basil.setId(3L);
    }

    @Test
    void getAllToppings_Success() throws Exception {
        Topping topping1 = new Topping("Cheese");
        Topping topping2 = new Topping("Tomato Sauce");
        List<Topping> toppings = Arrays.asList(topping1, topping2);

        given(toppingService.findAllToppings()).willReturn(toppings);

        mockMvc.perform(get("/api/toppings")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Cheese"))
                .andExpect(jsonPath("$[1].name").value("Tomato Sauce"));
    }

    @Test
    void addTopping_Success() throws Exception {
        Topping newTopping = new Topping("Basil");
        given(toppingService.addTopping(any(Topping.class))).willReturn(newTopping);

        mockMvc.perform(post("/api/toppings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTopping)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Basil"));
    }

    @Test
    void deleteTopping_Success() throws Exception {
        long toppingId = 1L;

        mockMvc.perform(delete("/api/toppings/{id}", toppingId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(toppingService).deleteTopping(toppingId);
    }

    @Test
    void updateTopping_Success() throws Exception {
        Topping updatedTopping = new Topping("Updated Basil");
        updatedTopping.setId(1L);
        given(toppingService.updateTopping(anyLong(), any(Topping.class))).willReturn(updatedTopping);

        mockMvc.perform(put("/api/toppings/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTopping)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Basil"));
    }

}

