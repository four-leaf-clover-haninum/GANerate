package com.example.GANerate.response;

import com.example.GANerate.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserResponse {

    @NoArgsConstructor
    @Getter
    public static class signup{

        private String email;
        private String name;
        private String phoneNum;

        @Builder
        private signup(String email, String name, String phoneNum){
            this.email=email;
            this.name=name;
            this.phoneNum=phoneNum;
        }

        public static UserResponse.signup response(User user) {
            return UserResponse.signup.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .phoneNum(user.getPhoneNum())
                    .build();
        }
    }


    @NoArgsConstructor
    @Getter
    public static class signin {
        private String email;
        private String accessToken;
        private String refreshToken;


        @Builder
        private signin(String email, String accessToken, String refreshToken) {
            this.email = email;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public static UserResponse.signin response(User user, String atk) {
            return UserResponse.signin.builder()
                    .email(user.getEmail())
                    .accessToken(atk)
                    .build();
        }

        public static UserResponse.signin response(User user, String atk, String rtk) {
            return UserResponse.signin.builder()
                    .email(user.getEmail())
                    .accessToken(atk)
                    .refreshToken(rtk)
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class userAll {
        private String email;
        private String name;
        private String phoneNum;

        @Builder
        private userAll(String email, String name, String phoneNum){
            this.email=email;
            this.name=name;
            this.phoneNum=phoneNum;
        }

        public static UserResponse.userAll response(User user) {
            return UserResponse.userAll.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .phoneNum(user.getPhoneNum())
                    .build();
        }
    }

}
