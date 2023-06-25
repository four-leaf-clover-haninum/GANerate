package com.example.GANerate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserJoinRequest {
    private String userId;
    private String userPw;
    private String name;
    private String email;
    private String phoneNum;
    private String role;
}
