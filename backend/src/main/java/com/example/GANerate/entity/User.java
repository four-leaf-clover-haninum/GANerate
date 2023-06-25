package com.example.GANerate.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String userPw;
    private String name;
    private String email;
    private String phoneNum;
    private String role;

    @Builder
    public User(String userId, String userPw, String name, String email, String phoneNum, String role){
        this.userId=userId;
        this.userPw=userPw;
        this.name=name;
        this.email=email;
        this.phoneNum=phoneNum;
        this.role=role;
    }
}
