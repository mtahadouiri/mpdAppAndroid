package com.mtdev.musicbox.application.entities;

import java.util.List;

/**
 * Created by PC on 21/01/2018.
 */

public class Cart {
    private List<Product> products;
    private int customerId;
    private String email;

    public Cart() {
    }

    public Cart(List<Product> products, int customerId, String email) {
        this.products = products;
        this.customerId = customerId;
        this.email = email;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "products=" + products +
                ", customerId=" + customerId +
                '}';
    }
}
