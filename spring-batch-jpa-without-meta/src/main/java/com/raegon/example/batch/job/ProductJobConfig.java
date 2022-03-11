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
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.stream.IntStream;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProductJobConfig {

  public static final int CHUNK_SIZE = 5;
  public static final int PAGE_SIZE = CHUNK_SIZE;

  private final JobBuilderFactory jobFactory;
  private final StepBuilderFactory stepFactory;

  private final PlatformTransactionManager transactionManager;
  private final EntityManagerFactory entityManagerFactory;
  private final ProductRepository repo;

  @Bean
  public Job productJob() {
    return jobFactory.get("product-job")
        .start(initProductStep())
        .next(jpaPagingItemReaderStep())
        .next(initProductStep())
        .next(repositoryItemReaderStep())
        .listener(jobExecutionListener())
        .build();
  }

  @Bean
  public Step initProductStep() {
    return stepFactory.get("init-step")
        .tasklet(initProductTasklet())
        .listener(stepExecutionListener())
        .build();
  }

  @Bean
  public Step jpaPagingItemReaderStep() {
    return stepFactory.get("jpa-paging-item-reader-step")
        .<Product, Product>chunk(CHUNK_SIZE)
        .reader(jpaPagingItemReader())
        .processor(productProcessor())
        .writer(productWriter())
        .listener(stepExecutionListener())
        .listener(chunkListener())
        .build();
  }

  @Bean
  public Step repositoryItemReaderStep() {
    return stepFactory.get("repository-item-reader-step")
        .<Product, Product>chunk(CHUNK_SIZE)
        .reader(repositoryItemReader())
        .processor(productProcessor())
        .writer(productWriter())
        .listener(chunkListener())
        .listener(stepExecutionListener())
        .build();
  }

  @Bean
  public Tasklet initProductTasklet() {
    return (contribution, chunkContext) -> {
      repo.deleteAll();
      IntStream.rangeClosed(1, 7)
          .mapToObj(Product::new)
          .forEach(repo::save);
      return RepeatStatus.FINISHED;
    };
  }

  @Bean
  public ItemReader<Product> jpaPagingItemReader() {
    return new JpaPagingItemReaderBuilder<Product>()
        .queryString("SELECT p FROM Product p")
        .pageSize(PAGE_SIZE)
        .entityManagerFactory(entityManagerFactory)
        .name("product-reader")
        .build();
  }

  @Bean
  public ItemReader<Product> repositoryItemReader() {
    return new RepositoryItemReaderBuilder<Product>()
        .repository(repo)
        .methodName("findAll")
        .pageSize(PAGE_SIZE)
        .saveState(false)
        .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
        .name("repository-item-reader")
        .build();
  }

  @Bean
  public ItemProcessor<Product, Product> productProcessor() {
    return item -> {
      State state = State.PROCESS;
      log.info("Process : {} > {}", item, state);
      item.setState(state);
      return item;
    };
  }

  @Bean
  public ItemWriter<Product> productWriter() {
    return items -> items.forEach(item -> {
      Product p = repo.getById(item.getId());
      State state = State.DONE;
      log.info("Write   : {} > {}", item, state);
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
        // Do nothing
      }
    };
  }

  @Bean
  public StepExecutionListener stepExecutionListener() {
    return new StepExecutionListener() {
      @Override
      public void beforeStep(StepExecution stepExecution) {
        // Do nothing
      }

      @Override
      public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("Step Finished");
        repo.findAll().forEach(product -> log.info("{}", product));
        return ExitStatus.COMPLETED;
      }
    };

  }

  @Bean
  public ChunkListener chunkListener() {
    return new ChunkListener() {
      @Override
      public void beforeChunk(ChunkContext context) {
        log.info("↓ Chunk start");
      }

      @Override
      public void afterChunk(ChunkContext context) {
        log.info("↑ Chunk end");
      }

      @Override
      public void afterChunkError(ChunkContext context) {
        // Do nothing
      }
    };
  }

}
