package com.example.GANerate.controller;

import com.example.GANerate.request.UserRequest;
import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.UserResponse;
import com.example.GANerate.service.EmailService;
import com.example.GANerate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    //회원가입
    @PostMapping("/auth/signup")
    public CustomResponseEntity<UserResponse.signup> signup(
            @RequestBody @Valid final UserRequest.signup request){
        return CustomResponseEntity.success(userService.signup(request));
    }

    //이메일 전송
    @PostMapping("/auth/email")
    public CustomResponseEntity<String> sendEmail(@RequestBody @Valid final UserRequest.emailAuth request) {
        return CustomResponseEntity.success(emailService.authEmail(request));
    }

    //이메일 인증번호 인증
    @GetMapping("/auth/email")
    public CustomResponseEntity<String> checkEmailNum(@RequestBody @Valid final UserRequest.emailNum request) {
        return CustomResponseEntity.success(emailService.checkNum(request));
    }

    //로그인
    @PostMapping("/auth/signin")
    public CustomResponseEntity<UserResponse.signin> signin(
            @RequestBody @Valid final UserRequest.signin request){
        return CustomResponseEntity.success(userService.signin(request));
    }

    //access 토큰 만료 o, refresh token 만료 x => access token 재발급
    @PostMapping("/auth/reissue")
    public CustomResponseEntity<UserResponse.reissue> reissue(@RequestBody @Valid final UserRequest.reissue request){
        return CustomResponseEntity.success(userService.reissue(request));
    }

    //로그아웃


    //유저 정보 조회
    @GetMapping("/auth/users")
    public CustomResponseEntity<List<UserResponse.userAll>> findUsers(){
        return CustomResponseEntity.success(userService.findAll());
    }

    //프로필 수정


    //좋아요 상품 조회


    //구매 상품 조회




}
