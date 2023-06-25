package com.example.GANerate.controller;

//import com.example.GANerate.config.SecurityConfig;
import com.example.GANerate.dto.UserJoinRequest;
import com.example.GANerate.dto.UserLoginRequest;
import com.example.GANerate.entity.User;
import com.example.GANerate.repository.UserRepository;
import com.example.GANerate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody UserJoinRequest dto){
        userService.join(dto.getUserId(), dto.getUserPw(), dto.getName(), dto.getEmail(), dto.getPhoneNum(), dto.getRole());
        return ResponseEntity.ok().body("회원가입이 성공했습니다.");
    }

    //로그인된 유저에 대한 유저 아이디는 토큰에 있어서 요청시 유저 아이디가 필요 없음
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest dto){
        String token = userService.login(dto.getUserId(), dto.getUserPw());
        return ResponseEntity.ok().body(token);
    }
    @GetMapping("/list")
    public ResponseEntity<User> find(Long id){
        return ResponseEntity.ok().body(userService.find(id));
    }


}
