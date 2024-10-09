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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ItemService {

    private final CsvProcessor csvProcessor;

    public ItemService(CsvProcessor csvProcessor) {
        this.csvProcessor = csvProcessor;
    }

    public String importItems(MultipartFile file, String uploadDir) throws IOException {
        // Validate the file before processing
        if (!csvProcessor.isValidCsvFile(file)) {
            throw new IllegalArgumentException("Invalid file type. Please upload a CSV file.");
        }

        // Create the uploads directory if it does not exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the file to the uploads directory
        File tempFile = new File(uploadDir + File.separator + file.getOriginalFilename());
        file.transferTo(tempFile);

        // Variables to store the total value and the number of items
        AtomicReference<Double> totalValue = new AtomicReference<>(0.0);
        AtomicInteger totalItems = new AtomicInteger(0);

        // Process items from the CSV file in batches
        csvProcessor.readItemsFromCsvInBatches(tempFile, batch -> {
            // Sum the value of the items in the current batch
            double batchTotal = batch.stream()
                .filter(item -> item.getItemValue() != null) // Ensure no null values
                .mapToDouble(Item::getItemValue)
                .sum();
            totalValue.updateAndGet(v -> v + batchTotal);
            totalItems.addAndGet(batch.size());
        });

        // Return the number of items and the total value
        return "Import successful: " + totalItems.get() + " items imported. Total value: " + totalValue.get();
    }
}
