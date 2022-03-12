package com.raegon.example.spring.config;

import com.raegon.example.spring.model.Mart;
import com.raegon.example.spring.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class BeanCreationTest {

  @Test
  @DisplayName("@Component를 사용해서 등록할 경우: 설정 파일에 프록시가 사용되지 않는다.")
  void createUsingComponent() {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ComponentConfig.class);
    // 설정 클래스 확인
    ComponentConfig config = ac.getBean(ComponentConfig.class);
    System.out.println("config.getClass() = " + config.getClass());
    assertThat(config.getClass().getSimpleName()).doesNotContain("EnhancerBySpringCGLIB");
    // Bean 싱글톤 확인
    Product toy = (Product) ac.getBean("toy");
    Mart mart = (Mart) ac.getBean("mart");
    assertThat(mart.getProduct("toy")).isNotEqualTo(toy);
  }

  @Test
  @DisplayName("@Configuration을 사용해서 등록할 경우: 설정 파일에 프록시가 사용된다.")
  void createUsingConfiguration() {
    AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ConfigurationConfig.class);
    // 설정 클래스 확인
    ConfigurationConfig config = ac.getBean(ConfigurationConfig.class);
    System.out.println("config.getClass() = " + config.getClass());
    assertThat(config.getClass().getSimpleName()).contains("EnhancerBySpringCGLIB");
    // Bean 싱글톤 확인
    Product toy = (Product) ac.getBean("toy");
    Mart mart = (Mart) ac.getBean("mart");
    assertThat(mart.getProduct("toy")).isEqualTo(toy);
  }

}