package com.example.ecommerce.controller;

import com.example.ecommerce.service.ItemService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/import")
    public String importItems(@RequestParam("file") MultipartFile file) {
        try {
            // Import the items and return the response
            return itemService.importItems(file);
        } catch (IOException e) {
            return "Failed to import items: " + e.getMessage();
        }
    }
}

