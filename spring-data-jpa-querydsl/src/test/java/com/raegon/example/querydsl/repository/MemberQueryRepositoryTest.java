package com.raegon.example.querydsl.repository;

import com.raegon.example.querydsl.dto.MemberSearchCondition;
import com.raegon.example.querydsl.entity.Member;
import com.raegon.example.querydsl.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberQueryRepositoryTest {

  @Autowired
  TestEntityManager em;

  @Autowired
  MemberRepository repo;

  @Test
  void searchTest() {
    // Given
    Team team = Team.builder().name("team").build();
    Member one = Member.builder().name("one").age(10).team(team).build();
    Member two = Member.builder().name("two").age(20).team(team).build();
    em.persist(team);
    em.persist(one);
    em.persist(two);

    // When
    MemberSearchCondition condition = MemberSearchCondition.builder()
        .ageGoe(10)
        .ageLoe(19)
        .build();
    List<Member> members = repo.search(condition);

    // Then
    assertThat(members).hasSize(1);
    assertThat(members).extracting("name").containsExactly("one");
  }

}