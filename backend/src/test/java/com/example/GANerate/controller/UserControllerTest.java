package com.example.GANerate.controller;


import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class UserControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @MockBean
//    UserService userService;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Test
//    @DisplayName("회원가입 성공")
//    @WithMockUser
//    void join() throws Exception {
//        String userId = "abc";
//        String userPw = "123";
//        String name="kim";
//        String email="av@aa.aa";
//        String phoneNum="01010101111";
//        String role = "user";
//        mockMvc.perform(post("/users/join")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userId, userPw, name, email, phoneNum, role))))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("회원가입 실패 - 아이디 중복")
//    @WithMockUser
//    void joinFail() throws Exception {
//        String userId = "abc";
//        String userPw = "123";
//        String name="kim";
//        String email="av@aa.aa";
//        String phoneNum="01010101111";
//        String role = "user";
//
//        when(userService.join(any(), any(),any(), any(),any(), any()))
//                .thenThrow(new RuntimeException("해당 아이디는 중복입니다"));
//
//        mockMvc.perform(post("/users/join")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userId, userPw, name, email, phoneNum, role))))
//                .andDo(print())
//                .andExpect(status().isConflict());
//    }
//
//    @Test
//    @DisplayName("로그인 성공")
//    @WithMockUser
//    void login_success() throws Exception{
//        String userId = "abc";
//        String userPw = "123";
//
//        when(userService.login(any(), any()))
//                .thenReturn("token");
//
//        mockMvc.perform(post("/users/login")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userId, userPw))))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - id틀림")
//    @WithMockUser
//    void login_fail() throws Exception{
//        String userId = "abc";
//        String userPw = "123";
//
//        when(userService.login(any(), any()))
//                .thenThrow(new AppException(ErrorCode.USERID_NOT_FOUND, ""));
//
//        mockMvc.perform(post("/users/login")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userId, userPw))))
//                .andDo(print())
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - pw틀림")
//    @WithMockUser
//    void login_fail2() throws Exception{
//        String userId = "abc";
//        String userPw = "123";
//
//        when(userService.login(any(), any()))
//                .thenThrow(new AppException(ErrorCode.INVALID_PASSWORD, ""));
//
//        mockMvc.perform(post("/users/login")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userId, userPw))))
//                .andDo(print())
//                .andExpect(status().isUnauthorized());
//    }
}