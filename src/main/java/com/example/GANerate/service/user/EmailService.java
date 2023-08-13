package com.example.GANerate.service.user;

import com.example.GANerate.config.redis.RedisUtil;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.request.user.UserRequest;
import com.example.GANerate.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @Transactional
    public String authEmail(UserRequest.emailAuth request) {
 
    // 임의의 authKey 생성
    Random random = new Random();
    String authKey = String.valueOf(random.nextInt(888888) + 111111);// 범위 : 111111 ~ 999999
 
    // 이메일 발송
    sendAuthEmail(request.getEmail(), authKey);
    return "이메일이 발송되었습니다.";
    }

    private void sendAuthEmail(String email, String authKey) {
        String subject = "제목";
        String text = "회원 가입을 위한 인증번호는 " + authKey + "입니다. <br/>";

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);//포함된 텍스트가 HTML이라는 의미로 true.
        } catch (MessagingException e) {
            throw new CustomException(Result.FAIL_SEND_EMAIL);
        }
        javaMailSender.send(mimeMessage);

        // 유효 시간(5분)동안 {email, authKey} 저장
        redisUtil.setDataExpire(authKey, email, 60 * 5L);
    }

    //인증번호 대조
    @Transactional
    public UserResponse.email checkNum(UserRequest.emailNum request) {
        List<String> info = request.toEntity();
        String email = redisUtil.getData(info.get(1));
        log.info(email);

        // List에 이메일과 인증번호 둘다 넣은 이유는 다른 사람의 인증번호와 바뀌는 상황을 방지함.
        if (email.equals(info.get(0))){ //equals 사용!!
            UserResponse.email emailAuth = UserResponse.email.builder()
                    .emailAuth(true)
                    .build();
            return emailAuth;
        }else{
            throw new CustomException(Result.UNCORRECT_CERTIFICATION_NUM);
        }
    }
}
