package com.example.GANerate.controller.userController;

import com.example.GANerate.domain.Category;
import com.example.GANerate.domain.DataProduct;
import com.example.GANerate.domain.Heart;
import com.example.GANerate.domain.User;
import com.example.GANerate.request.user.UserRequest;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import com.example.GANerate.response.user.UserResponse;
import com.example.GANerate.support.docs.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.GANerate.config.RestDocsConfig.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBean(JpaMetamodelMappingContext.class)
class UserControllerTest extends RestDocsTestSupport {

    @Test
    @DisplayName("회원가입")
    public void signUp() throws Exception {

        //given
        UserResponse.signup response = UserResponse.signup.builder()
                .email("test@test.com")
                .name("test")
                .phoneNum("01011111111")
                .build();

        given(userService.signup(any(UserRequest.signup.class))).willReturn(response);

        //when
        UserRequest.signup request = UserRequest.signup.builder()
                .email("test@test.com").userPw("123").name("test").phoneNum("01011111111").emailAuth(true).build();

        ResultActions result = this.mockMvc.perform(
                post("/v1/users/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                        requestFields(
                                                fieldWithPath("email").description("email").attributes(field("constraints", "이메일 형식")),
                                                fieldWithPath("userPw").description("password").attributes(field("constraints", "숫자, 영문 혼합 8자 이상")),
                                                fieldWithPath("name").description("name").attributes(field("constraints", "")),
                                                fieldWithPath("phoneNum").description("phoneNumber").attributes(field("constraints", "")),
                                                fieldWithPath("emailAuth").description("email 인증").attributes(field("constraints", ""))
                                        ),
                                        responseFields(
                                                fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                                fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("이름"),
                                                fieldWithPath("data.phoneNum").type(JsonFieldType.STRING).description("전화번호")
                                        )
                        )
                )
        ;
    }

    @Test
    @DisplayName("로그인")
    public void signIn() throws Exception {

        //given
        UserResponse.signin response = UserResponse.signin.builder().email("test@test.com").accessToken("accessToken").refreshToken("refreshToken").build();

        given(userService.signin(any(UserRequest.signin.class))).willReturn(response);

        //when
        UserRequest.signin request = UserRequest.signin.builder()
                .email("test@test.com").userPw("123").build();

        ResultActions result = this.mockMvc.perform(
                post("/v1/users/sign-in")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("email").description("email").attributes(field("constraints", "이메일 형식")),
                                        fieldWithPath("userPw").description("password").attributes(field("constraints", "숫자, 영문 혼합 8자 이상"))
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("accessToken"),
                                        fieldWithPath("data.refreshToken").type(JsonFieldType.STRING).description("refreshToken")
                                )
                        )
                )
        ;
    }

    @Test
    @DisplayName("이메일 전송")
    public void emailSend() throws Exception {

        //given
        String response = "이메일이 전송되었습니다";

        given(emailService.authEmail(any(UserRequest.emailAuth.class))).willReturn(response);

        //when
        UserRequest.emailAuth request = UserRequest.emailAuth.builder().email("test@test.com").build();

        ResultActions result = this.mockMvc.perform(
                post("/v1/users/email")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("email").description("email").attributes(field("constraints", "이메일 형식"))
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data").type(JsonFieldType.STRING).description("data")

                                )
                        )
                )
        ;
    }

    @Test
    @DisplayName("이메일 인증")
    public void checkEmailAuth() throws Exception {

        //given
        UserResponse.email response = UserResponse.email.builder().emailAuth(true).build();

        given(emailService.checkNum(any(UserRequest.emailNum.class))).willReturn(response);

        //when
        UserRequest.emailNum request = UserRequest.emailNum.builder().email("test@test.com").certificationNum("123456").build();

        ResultActions result = this.mockMvc.perform(
                get("/v1/users/email")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("email").description("email").attributes(field("constraints", "이메일 형식")),
                                        fieldWithPath("certificationNum").description("이메일 인증번호").attributes(field("constraints", ""))
                                        ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data.emailAuth").type(JsonFieldType.BOOLEAN).description("이메일 인증 여부")
                                )
                        )
                )
        ;
    }

    @Test
    @DisplayName("토큰 재발급")
    public void reissue() throws Exception {

        //given
        UserResponse.reissue response = UserResponse.reissue.builder().accessToken("accessToken").build();

        given(userService.reissue(any(UserRequest.reissue.class))).willReturn(response);

        //when
        UserRequest.reissue request = UserRequest.reissue.builder().accessToken("accessToken").refreshToken("refreshToken").build();

        ResultActions result = this.mockMvc.perform(
                post("/v1/users/reissue")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("accessToken").description("accessToken").attributes(field("constraints", "jwt 토큰")),
                                        fieldWithPath("refreshToken").description("refreshToken").attributes(field("constraints", "jwt 토큰, redis 저장"))
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data.accessToken").type(JsonFieldType.STRING).description("재발급 accessToken")
                                )
                        )
                )
        ;
    }

    @Test
    @DisplayName("로그아웃")
    public void logout() throws Exception {

        //given
        UserResponse.logout response = UserResponse.logout.builder().userId(1l).build();

        given(userService.logout(any(UserRequest.logout.class))).willReturn(response);

        //when
        UserRequest.logout request = UserRequest.logout.builder().accessToken("accessToken").refreshToken("refreshToken").build();

        ResultActions result = this.mockMvc.perform(
                post("/v1/users/logout")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestFields(
                                        fieldWithPath("accessToken").description("accessToken").attributes(field("constraints", "jwt 토큰")),
                                        fieldWithPath("refreshToken").description("refreshToken").attributes(field("constraints", "jwt 토큰, redis 저장"))
                                ),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("회원 아이디")
                                )
                        )
                )
        ;
    }

    @Test
    @DisplayName("좋아요한 데이터 상품 목록")
    public void findHeartDataProducts() throws Exception {

        Category category1 = Category.builder()
                .title("패션").categoryCode(101).build();
        Category category2 = Category.builder()
                .title("사람").categoryCode(102).build();

        User user = new User(1l,"test","test","test","test",null);
        DataProduct dataProduct1 = new DataProduct(1l,1l,"test1",100l,"testing1",1l);
        DataProduct dataProduct2 = new DataProduct(2l,2l,"test2",200l,"testing2",2l);

        //given
        DataProductResponse.findHeartDataProducts test1 = DataProductResponse.findHeartDataProducts.builder()
                .productId(1l)
                .title("test1")
                .price(1000l)
                .description("test1 설명입니다.")
                .imageUrl("이미지 url")
                .createdAt(LocalDateTime.now())
                .categoriesName(List.of(category1.getTitle(),category2.getTitle()))
                .build();
        DataProductResponse.findHeartDataProducts test2 = DataProductResponse.findHeartDataProducts.builder()
                .productId(2l)
                .title("test2")
                .price(2000l)
                .description("test2 설명입니다.")
                .imageUrl("이미지 url")
                .createdAt(LocalDateTime.now())
                .categoriesName(List.of(category1.getTitle()))
                .build();

        List<DataProductResponse.findHeartDataProducts> response = List.of(test1, test2);

        Heart heart1 = Heart.builder().user(user).dataProduct(dataProduct1).build();
        Heart heart2 = Heart.builder().user(user).dataProduct(dataProduct2).build();


        given(userService.findHeartDataProducts()).willReturn(response);

        ResultActions result = this.mockMvc.perform(
                get("/v1/users/hearts")
                        .header("Authorization", "Basic dXNlcjpzZWNyZXQ=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // when then
        result.andExpect(status().isOk())
                .andDo(
                        restDocs.document(
                                requestHeaders(
                                        headerWithName("Authorization").description("accessToken")),
                                responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("결과코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과메시지"),
                                        fieldWithPath("data[].productId").type(JsonFieldType.NUMBER).description("상품 아이디"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("상품 명"),
                                        fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("상품 가격"),
                                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("상품 설명"),
                                        fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING).description("상품 이미지 url"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("상품 생성일자"),
                                        fieldWithPath("data[].categoriesName").type(JsonFieldType.ARRAY).description("상품 아이디")
                                        )
                        )
                )
        ;
    }
}