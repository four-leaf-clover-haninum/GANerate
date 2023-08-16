package com.example.GANerate.support;

import com.example.GANerate.config.SecurityConfig;
import com.example.GANerate.config.jwt.JwtAccessDeniedHandler;
import com.example.GANerate.config.jwt.JwtAuthenticationEntryPoint;
import com.example.GANerate.config.jwt.TokenProvider;
import com.example.GANerate.config.redis.RedisUtil;
import com.example.GANerate.controller.categoryController.CategoryController;
import com.example.GANerate.controller.dataProductController.DataProductController;
import com.example.GANerate.controller.heartController.HeartController;
import com.example.GANerate.controller.orderController.OrderController;
import com.example.GANerate.controller.userController.UserController;
import com.example.GANerate.repository.*;
import com.example.GANerate.service.category.CategoryService;
import com.example.GANerate.service.dataProduct.DataProductSearchService;
import com.example.GANerate.service.dataProduct.DataProductService;
import com.example.GANerate.service.heart.HeartService;
import com.example.GANerate.service.order.OrderService;
import com.example.GANerate.service.user.EmailService;
import com.example.GANerate.service.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@Disabled
@WebMvcTest(controllers = {
        UserController.class,
        DataProductController.class,
        CategoryController.class,
        HeartController.class,
        OrderController.class
}, excludeAutoConfiguration = SecurityConfig.class)
public abstract class ControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MockMvc mockMvc;

    //@McokBean으로 서비스, 레포지토리는 넣어줘야함.

    @MockBean
    protected UserService userService;

    @MockBean
    protected EmailService emailService;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected DataProductRepository dataProductRepository;

    @MockBean
    protected DataProductService dataProductService;

    @MockBean
    protected CategoryRepository categoryRepository;

    @MockBean
    protected CategoryService categoryService;

    @MockBean
    protected HeartService heartService;

    @MockBean
    protected HeartRepository heartRepository;

    @MockBean
    protected DataProductSearchService dataProductSearchService;

    @MockBean
    protected OrderService orderService;

    @MockBean
    protected OrderRepository orderRepository;

    // 아래의 4개는 spring security에서 빈으로 주입해야되는 것들인데, @WebMvcTest는 @Component, @Service, @Repository 를 빈으로 스캔하지 않으므로 @MockBean으로 별도 주입해야함.

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    protected JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    protected JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @MockBean
    protected RedisUtil redisUtil;

    protected String createJson(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }
}

