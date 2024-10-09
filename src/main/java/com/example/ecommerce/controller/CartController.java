package com.example.ecommerce.controller;

import com.example.ecommerce.model.Item;
import com.example.ecommerce.model.CartResponse;
import com.example.ecommerce.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/calculate")
    public CartResponse calculateTotalWithIva(
            @RequestBody List<Item> items, 
            @RequestParam(name = "ivaType", required = true) String ivaType) {
        return cartService.calculateTotalWithIva(items, ivaType);
    }
}

