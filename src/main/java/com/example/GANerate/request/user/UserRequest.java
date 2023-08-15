package com.example.GANerate.request.user;

import com.example.GANerate.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;


public class UserRequest {

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    public static class signup {

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 양식을 지켜주세요.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$", message = "비밀번호는 8자 이상이며, 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
//                (regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$", message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
        private String userPw;

        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        @NotBlank(message = "휴대폰 번호는 필수입니다.")
        private String phoneNum;

        @NotNull(message = "이메일 인증은 필수입니다.")
        private boolean emailAuth;
    }

    @NoArgsConstructor
    @Getter
    @Builder
    @AllArgsConstructor
    public static class signin {
        @NotBlank(message = "아이디는 필수입니다.")
        private String email;
        @NotBlank(message = "비밀번호는 필수입니다.")
        private String userPw;
    }

    @NoArgsConstructor
    @Getter
    @Builder
    @AllArgsConstructor
    public static class emailAuth{
        @Email
        @NotBlank(message = "이메일을 입력하시오.")
        private String email;
    }

    @NoArgsConstructor
    @Getter
    @AllArgsConstructor
    @Builder
    public static class emailNum{

        @Email
        @NotBlank(message = "이메일을 입력하시오.")
        private String email;

        @NotBlank(message = "인증번호를 입력하시오")
        private String certificationNum;

        public List<String> toEntity(){
            ArrayList<String> lst = new ArrayList<>();
            lst.add(email);
            lst.add(certificationNum);
            return lst;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class reissue {
        @NotBlank(message = "accessToken 을 입력해주세요.")
        private String accessToken;

        @NotBlank(message = "refreshToken 을 입력해주세요.")
        private String refreshToken;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class logout {
        @NotBlank(message = "잘못된 요청입니다.")
        private String accessToken;

        @NotBlank(message = "잘못된 요청입니다.")
        private String refreshToken;
    }
}

