package com.example.ecommerce.controller;

import com.example.ecommerce.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/import")
    public String importItems(@RequestParam("file") MultipartFile file,
                              @RequestParam("uploadDir") String uploadDir) {
        try {
            // Call the service to import items with the provided upload directory
            return itemService.importItems(file, uploadDir);
        } catch (IOException e) {
            // Return a message in case of a failure during import
            return "Failed to import items: " + e.getMessage();
        }
    }
}
