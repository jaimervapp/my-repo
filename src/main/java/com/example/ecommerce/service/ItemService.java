package com.example.ecommerce.service;

import com.example.ecommerce.model.Item;
import com.example.ecommerce.util.CsvProcessor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ItemService {

    private final CsvProcessor csvProcessor;
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    public ItemService(CsvProcessor csvProcessor) {
        this.csvProcessor = csvProcessor;
    }

    public String importItems(MultipartFile file) throws IOException {
        // Validate the file before processing
        if (!csvProcessor.isValidCsvFile(file)) {
            throw new IllegalArgumentException("Invalid file type. Please upload a CSV file.");
        }

        // Create the uploads directory if it does not exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the file to the uploads directory
        File tempFile = new File(UPLOAD_DIR + file.getOriginalFilename());
        file.transferTo(tempFile);

        // Process items from the CSV file
        List<Item> items = csvProcessor.readItemsFromCsv(tempFile);

        // Calculate the total value of items using getItemValue
        double totalValue = items.stream()
            .filter(item -> item.getItemValue() != null) // Ensure no null values
            .mapToDouble(Item::getItemValue)
            .sum();

        // Return the number of items and the total value
        return "Import successful: " + items.size() + " items imported. Total value: " + totalValue;
    }
}

