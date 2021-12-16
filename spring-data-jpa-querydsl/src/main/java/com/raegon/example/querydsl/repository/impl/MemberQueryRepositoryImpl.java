package com.raegon.example.querydsl.repository.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.raegon.example.querydsl.dto.MemberSearchCondition;
import com.raegon.example.querydsl.entity.Member;
import com.raegon.example.querydsl.repository.MemberQueryRepository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.raegon.example.querydsl.entity.QMember.member;
import static com.raegon.example.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

public class MemberQueryRepositoryImpl implements MemberQueryRepository {

  private final JPAQueryFactory queryFactory;

  public MemberQueryRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  public List<Member> search(MemberSearchCondition condition) {
    return queryFactory.selectFrom(member)
        .leftJoin(member.team, team)
        .where(
            eq(member.name, condition.getUserName()),
            eq(member.team.name, condition.getTeamName()),
            goe(member.age, condition.getAgeGoe()),
            loe(member.age, condition.getAgeLoe())
        )
        .fetch();
  }

  private Predicate eq(StringPath path, String name) {
    return hasText(name) ? path.eq(name) : null;
  }

  private Predicate goe(NumberPath<?> path, Integer value) {
    return value != null ? path.goe(value) : null;
  }

  private Predicate loe(NumberPath<?> path, Integer value) {
    return value != null ? path.loe(value) : null;
  }

}
