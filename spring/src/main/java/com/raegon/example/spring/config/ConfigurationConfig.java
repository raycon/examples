package com.raegon.example.spring.config;

import com.raegon.example.spring.model.Mart;
import com.raegon.example.spring.model.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConfigurationConfig {

  @Bean
  public Product toy() {
    System.out.println("new toy");
    return new Product("toy");
  }

  @Bean
  public Mart mart() {
    System.out.println("new mart");
    return new Mart("mart", List.of(toy()));
  }

}
