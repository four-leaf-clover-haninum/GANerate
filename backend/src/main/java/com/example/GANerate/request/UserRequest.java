package com.example.GANerate.request;

import com.example.GANerate.domain.Role;
import com.example.GANerate.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UserRequest {

    @NoArgsConstructor
    @Getter
    public static class signup {

        @NotBlank(message = "이메일은 필수입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다.")
        // @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$", message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
        // 위의 조건은 배포시 다시 설정해놓기.
        private String userPw;

        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        @NotBlank(message = "휴대폰 번호는 필수입니다.")
        private String phoneNum;

        private Role role;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .userPw(userPw)
                    .name(name)
                    .phoneNum(phoneNum)
                    .role(Role.USER) // 이부분은 나중에 관리자 모드 같은것 만들때 다시 고려
                    .build();
        }
    }

    @NoArgsConstructor
    @Getter
    public static class signin {
        @NotBlank(message = "아이디는 필수입니다.")
        private String email;
        @NotBlank(message = "비밀번호는 필수입니다.")
        private String userPw;

        public User toEntity(){
            return User.builder()
                    .email(email)
                    .userPw(userPw)
                    .build();
        }
    }
}

