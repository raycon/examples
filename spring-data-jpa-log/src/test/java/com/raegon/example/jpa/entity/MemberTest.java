package com.raegon.example.jpa.entity;

import com.raegon.example.jpa.EnableQueryLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableQueryLog
public class MemberTest {

  @Autowired
  TestEntityManager em;

  @Test
  public void test() {
    // Given
    Team team = Team.builder().name("team").build();
    Member member = Member.builder().name("member").team(team).build();
    em.persistAndFlush(team);
    em.persistAndFlush(member);
    em.clear();

    // When
    Member findMember = em.find(Member.class, member.getId());

    // Then
    assertThat(findMember.getName()).isEqualTo("member");
    assertThat(findMember.getTeam().getMembers()).containsExactly(findMember);
  }

}