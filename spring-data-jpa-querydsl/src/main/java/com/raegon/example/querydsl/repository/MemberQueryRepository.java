package com.raegon.example.querydsl.repository;

import com.raegon.example.querydsl.dto.MemberSearchCondition;
import com.raegon.example.querydsl.entity.Member;

import java.util.List;

public interface MemberQueryRepository {

  List<Member> search(MemberSearchCondition condition);

}
