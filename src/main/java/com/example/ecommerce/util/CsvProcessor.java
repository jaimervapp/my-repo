package com.example.ecommerce.util;

import com.example.ecommerce.model.Item;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
public class CsvProcessor {

    private static final int BATCH_SIZE = 100; // Tamaño del lote para la lectura

    public void readItemsFromCsvInBatches(File file, Consumer<List<Item>> batchConsumer) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            List<Item> batch = new ArrayList<>();
            
            // Leer la primera línea para ignorar el encabezado
            String headerLine = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                try {
                    // Intentar parsear los valores
                    Long id = Long.parseLong(values[0]);
                    String name = values[1];
                    Double value = Double.parseDouble(values[2]);
                    Item item = new Item(id, name, value);
                    batch.add(item);
                } catch (NumberFormatException e) {
                    // Ignorar líneas que no se pueden parsear (por ejemplo, encabezados mal formados)
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                    continue;
                }

                // Procesar el lote cuando alcance el tamaño definido
                if (batch.size() >= BATCH_SIZE) {
                    batchConsumer.accept(batch);
                    batch.clear();
                }
            }

            // Procesar el lote restante si hay elementos
            if (!batch.isEmpty()) {
                batchConsumer.accept(batch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidCsvFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            return false;
        }
        String mimeType = file.getContentType();
        return "text/csv".equalsIgnoreCase(mimeType) || "application/vnd.ms-excel".equalsIgnoreCase(mimeType);
    }
}

