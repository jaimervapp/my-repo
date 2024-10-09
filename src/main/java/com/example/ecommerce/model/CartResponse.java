package com.example.ecommerce.model;

public class CartResponse {
    private double total;
    private double iva;
    private double totalWithIva; // Total including IVA

    public CartResponse(double total, double iva, double totalWithIva) {
        this.total = total;
        this.iva = iva;
        this.totalWithIva = totalWithIva;
    }

    // Getters and setters
    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getTotalWithIva() {
        return totalWithIva;
    }

    public void setTotalWithIva(double totalWithIva) {
        this.totalWithIva = totalWithIva;
    }
}

