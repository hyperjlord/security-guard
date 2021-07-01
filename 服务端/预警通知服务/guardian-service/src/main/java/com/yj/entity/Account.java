package com.yj.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {
    @Id
    Long id;
    String phone;
    String password;
    String nickname;
    String shareCode;
    String accountType;
    String token;
}
