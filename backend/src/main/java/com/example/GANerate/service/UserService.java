package com.example.GANerate.service;

import com.example.GANerate.entity.User;
import com.example.GANerate.exception.AppException;
import com.example.GANerate.exception.ErrorCode;
import com.example.GANerate.repository.UserRepository;
import com.example.GANerate.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String key;
    private Long expireTimeMs = 1000*60*60l;

    @Transactional
    public String join(String userId, String userPw, String name, String email, String phoneNum, String role){

        //아이디 중복 체크
        userRepository.findByUserId(userId).
                ifPresent(user -> {
                    throw new AppException(ErrorCode.USERID_DUPLICATED, userId + "는 이미 있습니다.");
                });
        //저장
        User user = User.builder()
                .userId(userId)
                .userPw(encoder.encode(userPw))
                .name(name)
                .email(email)
                .phoneNum(phoneNum)
                .role(role)
                .build();
        userRepository.save(user);

        return "Success";
    }

    @Transactional
    public String login(String userId, String userPw){
        // userId 없음
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.USERID_NOT_FOUND, userId + "가 없습니다."));

        // password 틀림
        if(!encoder.matches(userPw, user.getUserPw())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드가 잘못되었습니다.");
        }

        //앞에서 exception 안나면 토큰 발행
        String token = JwtTokenUtil.createToken(user.getUserId(), key, expireTimeMs);
        return token;
    }

    @Transactional(readOnly = true)
    public User find(Long id){
        return userRepository.findById(id).get();
    }
}
