package com.raegon.example.querydsl.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Team {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "team")
  private final List<Member> members = new ArrayList<>();

  public void addMember(Member member) {
    this.members.add(member);
  }

  @Builder
  public Team(String name) {
    this.name = name;
  }

}
