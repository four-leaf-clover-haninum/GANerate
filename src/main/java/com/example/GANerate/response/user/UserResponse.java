package com.example.GANerate.response.user;

import com.example.GANerate.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

public class UserResponse {

    @NoArgsConstructor
    @Getter
    public static class signup{

        @NotNull(message = "이메일은 필수입니다.")
        private String email;
        @NotNull(message = "이름은 필수입니다.")
        private String name;
        @NotNull(message = "전화번호는 필수입니다.")
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

        @NotNull
        private String email;
        @NotNull
        private String accessToken;
        @NotNull
        private String refreshToken;


        @Builder
        private signin(String email, String accessToken, String refreshToken) {
            this.email = email;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

//        public static UserResponse.signIn response(User user, String atk) {
//            return UserResponse.signIn.builder()
//                    .email(user.getEmail())
//                    .accessToken(atk)
//                    .build();
//        }
//
//        public static UserResponse.signIn response(User user, String atk, String rtk) {
//            return UserResponse.signIn.builder()
//                    .email(user.getEmail())
//                    .accessToken(atk)
//                    .refreshToken(rtk)
//                    .build();
//        }
    }

    @NoArgsConstructor
    @Getter
    public static class reissue {
        private String accessToken;

        @Builder
        private reissue(String accessToken){
            this.accessToken=accessToken;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class userAll {
        private String email;
        private String name;
        private String phoneNum;

        @Builder
        private userAll(String email, String name, String phoneNum) {
            this.email = email;
            this.name = name;
            this.phoneNum = phoneNum;
        }

//        public static UserResponse.userAll response(User user) {
//            return UserResponse.userAll.builder()
//                    .email(user.getEmail())
//                    .name(user.getName())
//                    .phoneNum(user.getPhoneNum())
//                    .build();
//        }
    }
    @NoArgsConstructor
    @Getter
    public static class logout {
        private Long userId;
        @Builder
        private logout(Long userId) {
            this.userId = userId;
        }
    }

    @NoArgsConstructor
    @Getter
    public static class email {
        private boolean emailAuth;

        @Builder
        private email(boolean emailAuth) {
            this.emailAuth = emailAuth;
        }
    }

    @NoArgsConstructor
    @Getter
    @Builder
    @AllArgsConstructor
    public static class user {
        private Long id;
    }

    @NoArgsConstructor
    @Getter
    @Builder
    @AllArgsConstructor
    public static class point{
        private Long point;
    }
}
