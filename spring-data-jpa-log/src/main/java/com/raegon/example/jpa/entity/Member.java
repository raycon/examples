package com.raegon.example.jpa.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Getter
@Entity
@NoArgsConstructor
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name ="team_id")
  private Team team;

  @Builder
  public Member(String name, Team team) {
    this.name = name;
    this.team = team;
    this.team.addMember(this);
  }

}
