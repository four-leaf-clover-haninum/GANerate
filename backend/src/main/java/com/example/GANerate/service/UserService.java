package com.example.GANerate.service;

import com.example.GANerate.domain.User;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.UserRepository;
import com.example.GANerate.request.UserRequest;
import com.example.GANerate.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    //회원가입
    @Transactional
    public UserResponse.signup singup(UserRequest.signup request){
        //아이디 중복 검사
        validateDuplicatedUserEmail(request.getEmail());

        //저장
        User user = userRepository.save(request.toEntity());

        return UserResponse.signup.response(user);
    }

    //로그인
//    @Transactional(readOnly = true)
//    public UserResponse.signin signin(UserRequest.signin request){
//
//        // 일치하는 userId 없음
//        User user = userRepository.findByEmail(request.getEmail())
//                .orElseThrow(() -> new CustomException(USERID_NOT_FOUND));
//
//        // password 틀림
//        if(!passwordEncoder.matches(request.getUserPw(), user.getUserPw())){
//            throw new CustomException(INVALID_PASSWORD);
//        }
//
//        //앞에서 exception 안나면 access token 발행
//        String accessToken = tokenProvider.createToken(user.getId(), getAuthentication(request.getEmail(), request.getUserPw()));
//
//        return UserResponse.signin.response(user, accessToken, refreshToken);
//    }

    private void validateDuplicatedUserEmail(String userEmail) {
        Boolean existsByNickName = userRepository.existsByEmail(userEmail);
        if (existsByNickName) {
            throw new CustomException(Result.USERID_DUPLICATED);
        }
    }

    private Authentication getAuthentication(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

//    //로그아웃
//    @Transactional(readOnly = true)
//    public Boolean logout(String userEmail, String accessToken) {
//        String email = getUser(userId).getEmail();
//        Long accessTokenExpiration = tokenProvider.getExpiration(accessToken);
//
//        return redisService.logoutFromRedis(email, accessToken, accessTokenExpiration);
//    }

}
