package com.raegon.example.batch.job;

import com.raegon.example.batch.jpa.Product;
import com.raegon.example.batch.jpa.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductJobConfig {

  private final JobBuilderFactory jobFactory;
  private final StepBuilderFactory stepFactory;
  private final ProductRepository repo;
  private final PlatformTransactionManager transactionManager;

  @Bean
  public Job productJob(Step createStep, Step taskletStep) {
    return jobFactory.get("product-job")
        .start(createStep)
        .next(taskletStep)
        .build();
  }

  @Bean
  public Step createStep(ItemReader<Product> reader,
                         ItemProcessor<Product, Product> processor,
                         ItemWriter<Product> writer) {
    log.info("TransactionManager: {}", transactionManager.getClass());
    return stepFactory.get("create-step")
        .<Product, Product>chunk(5)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  public Step taskletStep(ProductTasklet tasklet) {
    return stepFactory.get("tasklet-step")
        .tasklet(tasklet)
        .build();
  }

  @Bean
  public ItemReader<Product> productReader() {
    List<Product> products = IntStream.rangeClosed(1, 10)
        .mapToObj(i -> "Product " + i)
        .map(Product::new)
        .collect(Collectors.toList());
    return new ListItemReader<>(products);
  }

  @Bean
  public ItemProcessor<Product, Product> productProcessor() {
    return item -> {
      log.info("{}", item.getName());
      item.setDescription("Description");
      return item;
    };
  }

  @Bean
  public ItemWriter<Product> productWriter() {
    return repo::saveAll;
  }

}
