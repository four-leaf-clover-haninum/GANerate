package com.example.GANerate.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authority { // 권한 정의 테이블

    @Id
    @Column(name = "authority_name", length = 50)
    private String authorityName;
}