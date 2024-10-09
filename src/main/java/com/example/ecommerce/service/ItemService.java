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
    private static final String UPLOAD_DIR = "/home/jaime/ecommerce-api/ecommerce-api/uploads/";

    public ItemService(CsvProcessor csvProcessor) {
        this.csvProcessor = csvProcessor;
    }

    public String importItems(MultipartFile file) throws IOException {
        // Validar el archivo antes de procesarlo
        if (!csvProcessor.isValidCsvFile(file)) {
            throw new IllegalArgumentException("Invalid file type. Please upload a CSV file.");
        }

        // Crear el directorio de subidas si no existe
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Guardar el archivo en el directorio de subidas
        File tempFile = new File(UPLOAD_DIR + file.getOriginalFilename());
        file.transferTo(tempFile);

        // Variables para el total de valor y número de artículos
        AtomicReference<Double> totalValue = new AtomicReference<>(0.0);
        AtomicInteger totalItems = new AtomicInteger(0);

        // Procesar artículos desde el archivo CSV en lotes
        csvProcessor.readItemsFromCsvInBatches(tempFile, batch -> {
            // Sumar el valor de los artículos en el lote actual
            double batchTotal = batch.stream()
                .filter(item -> item.getItemValue() != null) // Asegurarse de que no haya valores nulos
                .mapToDouble(Item::getItemValue)
                .sum();
            totalValue.updateAndGet(v -> v + batchTotal);
            totalItems.addAndGet(batch.size());
        });

        // Devolver el número de artículos y el valor total
        return "Import successful: " + totalItems.get() + " items imported. Total value: " + totalValue.get();
    }
}

