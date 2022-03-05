package com.raegon.example.batch.job;

import com.raegon.example.batch.jpa.Product;
import com.raegon.example.batch.jpa.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductTasklet implements Tasklet {

  private final ProductRepository repo;

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    List<Product> products = repo.findAll();
    products.stream().peek(p -> p.setDescription("tasklet")).forEach(this::print);
    return RepeatStatus.FINISHED;
  }

  private void print(Product product) {
    log.info("{}: {} {}", product.getId(), product.getName(), product.getDescription());
  }

}
