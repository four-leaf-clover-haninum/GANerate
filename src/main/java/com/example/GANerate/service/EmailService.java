package com.example.GANerate.service;

import com.example.GANerate.config.redis.RedisUtil;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.request.UserRequest;
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

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);//포함된 텍스트가 HTML이라는 의미로 true.
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        // 유효 시간(5분)동안 {email, authKey} 저장
        redisUtil.setDataExpire(authKey, email, 60 * 5L);
    }

    //인증번호 대조
    public String checkNum(UserRequest.emailNum request) {
        String res = null;
        List<String> info = request.toEntity();
        String email = redisUtil.getData(info.get(1));
        log.info(email);

        if (email.equals(info.get(0))){ //equals 사용!!
            res= "인증이 완료되었습니다";
        }else{
            throw new CustomException(Result.UNCORRECT_CERTIFICATION_NUM);
        }
        return res;
    }
}
