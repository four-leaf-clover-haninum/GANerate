package com.example.GANerate.controller.userController;

import com.example.GANerate.enumuration.Result;
import com.example.GANerate.request.user.UserRequest;
import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import com.example.GANerate.response.user.UserResponse;
import com.example.GANerate.service.user.EmailService;
import com.example.GANerate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    //회원가입
    @PostMapping("/v1/users/sign-up")
    public CustomResponseEntity<UserResponse.signup> signUp(
            @RequestBody @Valid final UserRequest.signup request){
        return CustomResponseEntity.success(userService.signup(request));
    }

    //로그인
    @PostMapping("/v1/users/sign-in")
    public CustomResponseEntity<UserResponse.signin> signIn(
            @RequestBody @Valid final UserRequest.signin request){
        return CustomResponseEntity.success(userService.signin(request));
    }

    //이메일 전송
    @PostMapping("/v1/users/email")
    public CustomResponseEntity<String> sendEmail(@RequestBody @Valid final UserRequest.emailAuth request) {
        return CustomResponseEntity.success(emailService.authEmail(request));
    }

    //이메일 인증번호 인증
    @GetMapping("/v1/users/email")
    public CustomResponseEntity<UserResponse.email> checkEmailNum(@RequestBody @Valid final UserRequest.emailNum request) {
        return CustomResponseEntity.success(emailService.checkNum(request));
    }

    //access 토큰 만료 o, refresh token 만료 x => access token 재발급
    @PostMapping("/v1/users/reissue")
    public CustomResponseEntity<UserResponse.reissue> reissue(@RequestBody @Valid final UserRequest.reissue request){
        return CustomResponseEntity.success(userService.reissue(request));
    }

    //로그아웃
    @PostMapping("/v1/users/logout")
    public CustomResponseEntity<UserResponse.logout> logout(@RequestBody @Valid final UserRequest.logout request){
        return CustomResponseEntity.success(userService.logout(request));
    }

    //좋아요한 데이터 상품 목록
    @GetMapping("/v1/users/hearts")
    public CustomResponseEntity<List<DataProductResponse.findHeartDataProducts>> findHeartDataProducts(@AuthenticationPrincipal Long userId){
        return CustomResponseEntity.success(userService.findHeartDataProducts(userId));
    }


//    //유저 전체 정보 조회
//    @GetMapping("/v1/users")
//    public CustomResponseEntity<List<UserResponse.userAll>> findUsers(){
//        return CustomResponseEntity.success(userService.findAll());
//    }

    //유저 단건 조회
    @GetMapping("/v1/users/")
    public CustomResponseEntity<UserResponse.user> findUser(@AuthenticationPrincipal Long id){
        return CustomResponseEntity.success(userService.findOne(id));
    }

    //프로필 수정
//    @PostMapping("/v1/users/profile")
//    public CustomResponseEntity<UserResponse>


    //좋아요 상품 조회


    //구매 상품 조회




}
