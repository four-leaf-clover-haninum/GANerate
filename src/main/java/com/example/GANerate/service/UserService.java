package com.example.GANerate.service;

import com.example.GANerate.config.jwt.TokenProvider;
import com.example.GANerate.domain.Authority;
import com.example.GANerate.domain.User;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.UserRepository;
import com.example.GANerate.request.UserRequest;
import com.example.GANerate.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.GANerate.enumuration.Result.USERID_NOT_FOUND;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    //회원가입
    @Transactional
    public UserResponse.signup signup(UserRequest.signup request){
        //아이디 중복 검사
        validateDuplicatedUserEmail(request.getEmail());

        //저장
        User user = userRepository.save(createEntityUserFromDto(request));

        return UserResponse.signup.response(user);
    }

    //로그인
    @Transactional(readOnly = true)
    public UserResponse.signin signin(UserRequest.signin request){

        // 일치하는 userId 없음
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(USERID_NOT_FOUND));

        // password 틀림
        if(!passwordEncoder.matches(request.getUserPw(), user.getUserPw())){
            throw new CustomException(Result.INVALID_PASSWORD);
        }

        //앞에서 exception 안나면 access token 발행
        String accessToken = tokenProvider.createToken(user.getId(), getAuthentication(request.getEmail(), request.getUserPw()));
        //String refreshToken = tokenProvider.createRefreshToken(user.getId());
        return UserResponse.signin.response(user, accessToken);
    }

    //전체 유저 조회
    @Transactional(readOnly = true)
    public List<UserResponse.userAll> findAll(){
        List<User> all = userRepository.findAll();
        List<UserResponse.userAll> userAll = new ArrayList<>();

        for(User user: all){
            UserResponse.userAll a = UserResponse.userAll.builder()
                .email(user.getEmail())
                .name(user.getName())
                .phoneNum(user.getPhoneNum())
                .build();

            userAll.add(a);
        }
        return userAll;

        /*
        스트림으로
        List<User> all = userRepository.findAll();

        return all.stream()
            .map(user -> UserResponse.userAll.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .phoneNum(user.getPhoneNum())
                    .build())
            .collect(Collectors.toList());
         */
    }

    private void validateDuplicatedUserEmail(String userEmail) {
        Boolean existsByNickName = userRepository.existsByEmail(userEmail);
        if (existsByNickName) {
            throw new CustomException(Result.USERID_DUPLICATED);
        }
    }


    private Authentication getAuthentication(String email, String password) {
        //Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    //회원 가입시 권한을 ROLE_USER로 추가하는 DTO
    private User createEntityUserFromDto(UserRequest.signup request) {
        return User.builder()
                .email(request.getEmail())
                .userPw(passwordEncoder.encode(request.getUserPw()))
                .name(request.getName())
                .phoneNum(request.getPhoneNum())
                .authorities(getAuthorities())
                .build();
    }

    private static Set<Authority> getAuthorities() {
        return Collections.singleton(Authority.builder()
                .authorityName("ROLE_USER")
                .build());
    }
}
