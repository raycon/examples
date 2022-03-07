package com.raegon.example.batch.jpa;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)

@Entity
public class Product {

  @Id
  private Integer id;

  @Enumerated(EnumType.STRING)
  private State state = State.NEW;

  public Product(Integer id) {
    this.id = id;
  }

  @PostUpdate
  public void postUpdate() {
    log.info("Updated : {}", this);
  }

  @PostPersist
  public void postPersist() {
    log.info("Persisted : {}", this);
  }

  public enum State {
    NEW,
    PROCESS,
    DONE
  }

}
