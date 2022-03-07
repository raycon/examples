package com.raegon.example.batch.config;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class CustomBatchConfigurer extends DefaultBatchConfigurer {

  @Override
  public void setDataSource(DataSource dataSource) {
    // Do nothing
    // https://www.raegon.com/spring-batch-jpa-without-meta
  }

  @Bean
  @Primary
  public PlatformTransactionManager jpaTransactionManager(EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }

  @Bean
  @Primary
  public StepBuilderFactory stepBuilderFactory(JobRepository jobRepository,
                                               PlatformTransactionManager transactionManager) {
    return new StepBuilderFactory(jobRepository, transactionManager);
  }

}

