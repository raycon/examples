package com.raegon.example.spring.model;

import java.util.List;

public class Mart {

  private String name;
  private List<Product> products;

  public Mart(String name, List<Product> products) {
    this.name = name;
    this.products = products;
  }

  public String getName() {
    return name;
  }

  public List<Product> getProducts() {
    return products;
  }

  public Product getProduct(String name) {
    return products.stream()
        .filter(p -> p.getName().equals(name))
        .findFirst()
        .orElse(null);
  }

}
