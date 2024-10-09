package com.example.ecommerce.service;

import com.example.ecommerce.model.Item;
import com.example.ecommerce.model.CartResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    private static final double IVA_NORMAL = 0.21; // 21% standard VAT rate
    private static final double IVA_REDUCIDO = 0.10; // 10% reduced VAT rate
    private static final double IVA_SUPERREDUCIDO = 0.04; // 4% super-reduced VAT rate

    public CartResponse calculateTotalWithIva(List<Item> items, String ivaType) {
        double ivaRate = determineIvaRate(ivaType);

        // Validate and filter items with non-null values
        double total = items.stream()
                .filter(item -> item.getItemValue() != null) // Filter out items with null values
                .mapToDouble(Item::getItemValue)
                .sum();

        double iva = total * ivaRate;
        double totalWithIva = total + iva; // Calculate the total including IVA

        return new CartResponse(total, iva, totalWithIva);
    }

    private double determineIvaRate(String ivaType) {
        switch (ivaType.toLowerCase()) {
            case "normal":
                return IVA_NORMAL;
            case "reducido":
                return IVA_REDUCIDO;
            case "superreducido":
                return IVA_SUPERREDUCIDO;
            default:
                try {
                    double customRate = Double.parseDouble(ivaType);
                    if (customRate < 0) {
                        throw new IllegalArgumentException("Custom IVA rate must be zero or positive.");
                    }
                    return customRate;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid IVA type or rate provided. Please use 'normal', 'reducido', 'superreducido', or a custom percentage.");
                }
        }
    }
}

