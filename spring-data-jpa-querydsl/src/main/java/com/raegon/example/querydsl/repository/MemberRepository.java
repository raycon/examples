package com.raegon.example.querydsl.repository;

import com.raegon.example.querydsl.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryRepository {

}
