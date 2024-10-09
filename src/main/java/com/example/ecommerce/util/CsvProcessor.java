package com.example.ecommerce.util;

import com.example.ecommerce.model.Item;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvProcessor {

    public List<Item> readItemsFromCsv(File file) {
        List<Item> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                // Skip the first line if it contains headers
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Process the data lines
                String[] values = line.split(",");
                Item item = new Item(Long.parseLong(values[0]), values[1], Double.parseDouble(values[2]));
                items.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean isValidCsvFile(MultipartFile file) {
        // Check the file extension
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            return false;
        }

        // Check the MIME type of the file
        String mimeType = file.getContentType();
        return "text/csv".equalsIgnoreCase(mimeType) || "application/vnd.ms-excel".equalsIgnoreCase(mimeType);
    }
}

