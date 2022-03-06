package com.raegon.example.batch.job;

import com.raegon.example.batch.jpa.Product;
import com.raegon.example.batch.jpa.Product.State;
import com.raegon.example.batch.jpa.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductJobConfig {

  public static final int CHUNK_SIZE = 5;
  public static final int PAGE_SIZE = CHUNK_SIZE;

  private final JobBuilderFactory jobFactory;
  private final StepBuilderFactory stepFactory;
  private final PlatformTransactionManager transactionManager;

  private final ProductRepository repo;

  @Bean
  public Job productJob(Step createStep, Step updateStep, JobExecutionListener jobExecutionListener) {
    return jobFactory.get("product-job")
        .start(createStep)
        .next(updateStep)
        .listener(jobExecutionListener)
        .build();
  }

  @Bean
  public Step createStep(Tasklet createProductTasklet) {
    return stepFactory.get("create-step")
        .tasklet(createProductTasklet)
        .build();
  }

  @Bean
  public Step updateStep(ItemReader<Product> reader,
                         ItemProcessor<Product, Product> processor,
                         ItemWriter<Product> writer,
                         ChunkListener chunkListener) {
    return stepFactory.get("update-step")
        .<Product, Product>chunk(CHUNK_SIZE)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .listener(chunkListener)
        .build();
  }

  @Bean
  public Tasklet createProductTasklet() {
    return (contribution, chunkContext) -> {
      repo.deleteAll();
      IntStream.rangeClosed(1, 7)
          .mapToObj(Product::new)
          .forEach(repo::save);
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  public ItemReader<Product> productReader(EntityManagerFactory emf) {
    return new JpaPagingItemReaderBuilder<Product>()
        .queryString("SELECT p FROM Product p")
        .pageSize(PAGE_SIZE)
        .entityManagerFactory(emf)
        .name("product-reader")
        .build();
  }

  @Bean
  public ItemProcessor<Product, Product> productProcessor() {
    return item -> {
      State state = State.PROCESS;
      log.info("Processor {}, {} -> {}", item.getId(), item.getState(), state);
      item.setState(state);
      return item;
    };
  }

  @Bean
  public ItemWriter<Product> productWriter() {
    return items -> items.forEach(item -> {
      Product p = repo.getById(item.getId());
      State state = State.DONE;
      log.info("Writer {}, {} -> {}", item.getId(), item.getState(), state);
      p.setState(state);
      repo.save(p);
    });
  }

  /*
   * Listeners
   */

  @Bean
  public JobExecutionListener jobExecutionListener() {
    return new JobExecutionListener() {
      @Override
      public void beforeJob(JobExecution jobExecution) {
        log.info("TransactionManager: {}", transactionManager.getClass());
      }

      @Override
      public void afterJob(JobExecution jobExecution) {
        repo.findAll().forEach(product -> log.info(product.toString()));
      }
    };
  }

  @Bean
  public ChunkListener chunkListener() {
    return new ChunkListener() {
      @Override
      public void beforeChunk(ChunkContext context) {
        log.info("Chunk start");
      }

      @Override
      public void afterChunk(ChunkContext context) {
        log.info("Chunk end");
      }

      @Override
      public void afterChunkError(ChunkContext context) {

      }
    };
  }

}
