package com.example.GANerate.service.user;

import com.amazonaws.services.s3.AmazonS3;
import com.example.GANerate.config.SecurityUtils;
import com.example.GANerate.config.jwt.TokenProvider;
import com.example.GANerate.config.redis.RedisUtil;
import com.example.GANerate.domain.*;
import com.example.GANerate.enumuration.OrderStatus;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.DataProductRepository;
import com.example.GANerate.repository.HeartRepository;
import com.example.GANerate.repository.UserRepository;
import com.example.GANerate.request.user.UserRequest;
import com.example.GANerate.response.ZipFileResponse;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import com.example.GANerate.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.example.GANerate.enumuration.Result.NOT_FOUND_USER;
import static com.example.GANerate.enumuration.Result.INVALID_EMAIL;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisUtil redisUtil;
    private final DataProductRepository dataProductRepository;
    private final AmazonS3 amazonS3;
    private final HeartRepository heartRepository;

//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;


    //회원가입
    @Transactional
    public UserResponse.signup signup(UserRequest.signup request){
        //아이디 중복 검사
        validateDuplicatedUserEmail(request.getEmail());

        // 이메일 인증 검사
        if(request.isEmailAuth()!=true){
            throw new CustomException(Result.UN_AUTHENTICATION_EMAIL);
        }

        //저장
        User user = userRepository.save(createEntityUserFromDto(request));

        return UserResponse.signup.response(user);
    }

    //로그인
    @Transactional(readOnly = true)
    public UserResponse.signin signin(UserRequest.signin request){

        // 일치하는 userId 없음
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(INVALID_EMAIL));

        // password 틀림
        if(!passwordEncoder.matches(request.getUserPw(), user.getUserPw())){
            throw new CustomException(Result.INVALID_PASSWORD);
        }

        //앞에서 exception 안나면 access token 발행
        String accessToken = tokenProvider.createToken(user.getId(), getAuthentication(request.getEmail(), request.getUserPw()));
        String refreshToken = tokenProvider.createRefreshToken(user.getId(), getAuthentication(request.getEmail(), request.getUserPw()));
        return UserResponse.signin.builder()
                .email(request.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // access Token 재발급
    @Transactional
    public UserResponse.reissue reissue(UserRequest.reissue request){

        String accessToken = tokenProvider.reissue(request);

        return UserResponse.reissue.builder()
                .accessToken(accessToken)
                .build();
    }

    //logout
    @Transactional
    public UserResponse.logout logout(UserRequest.logout request){
        // 1. Access Token 검증
        if (!tokenProvider.validateToken(request.getAccessToken())) {
            throw new CustomException(Result.BAD_REQUEST);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = tokenProvider.getAuthentication(request.getAccessToken());

        // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisUtil.getData(authentication.getName()) != null) {
            // Refresh Token 삭제
            redisUtil.deleteData(authentication.getName());
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = tokenProvider.getExpiration(request.getAccessToken());
        redisUtil.setDataExpire(request.getAccessToken(), "logout", expiration);

        return UserResponse.logout.builder()
                .userId(Long.valueOf(authentication.getName()))
                .build();
    }

    // 좋아요 한 데이터 조회
    @Transactional
    public List<DataProductResponse.findDataProducts> findHeartDataProducts(){
        User user = getCurrentUser();
        List<DataProduct> dataProducts = heartRepository.findDataProductsByUser(user).orElseThrow(() -> new CustomException(Result.NOT_FOUND_HEART));

        List<DataProductResponse.findDataProducts> response = new ArrayList<>();

        // 공통 포맷 만들기
        for(DataProduct dataProduct:dataProducts){
            ExampleImage exampleImage = dataProduct.getExampleImages().get(0);
            String imageUrl = exampleImage.getImageUrl();

            List<ProductCategory> productCategories = dataProduct.getProductCategories();
            List<String> categoryTitles = new ArrayList<>();
            List<Long> categoryIds = new ArrayList<>();
            for(ProductCategory productCategory : productCategories){
                Category category = productCategory.getCategory();
                String categoryTitle = category.getTitle();
                Long categoryId = category.getId();
                categoryTitles.add(categoryTitle);
                categoryIds.add(categoryId);
            }

            DataProductResponse.findDataProducts findHeartDataProducts = DataProductResponse.findDataProducts
                    .builder()
                    .dataProductId(dataProduct.getId())
                    .buyCnt(dataProduct.getBuyCnt())
                    .title(dataProduct.getTitle())
                    .price(dataProduct.getPrice())
                    .description(dataProduct.getDescription())
                    .createdAt(dataProduct.getCreatedAt())
                    .imageUrl(imageUrl)
                    .categoryIds(categoryIds)
                    .categoryNames(categoryTitles)
                    .build();
            response.add(findHeartDataProducts);
        }
        return response;
    }

    // 주품한 상품 조회(다운로드 가능)
    @Transactional(readOnly = true)
    public List<DataProductResponse.orderDataProducts> findOrderDataProduct(){
        User user = getCurrentUser();
        List<Order> orders = user.getOrders();

        List<DataProductResponse.orderDataProducts> orderDone = new ArrayList<>(); //다운로드 가능하거나 아직 다운은 못하지만 구매한 상품을 조회(취소 상품은 x)
        for (Order order : orders) {
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                DataProduct dataProduct = orderItem.getDataProduct();

                List<ExampleImage> exampleImages = dataProduct.getExampleImages();
                ExampleImage exampleImage = exampleImages.get(0);
                String imageUrl = exampleImage.getImageUrl();

                List<ProductCategory> productCategories = dataProduct.getProductCategories();
                List<String> categoriesName = new ArrayList<>();
                for (ProductCategory productCategory : productCategories) {
                    String title = productCategory.getCategory().getTitle();
                    categoriesName.add(title);
                }

                DataProductResponse.orderDataProducts orderDataProducts = DataProductResponse.orderDataProducts.builder()
                        .id(dataProduct.getId())
                        .title(dataProduct.getTitle())
                        .price(dataProduct.getPrice())
                        .imageUrl(imageUrl)
                        .createdAt(order.getCreatedAt())
                        .categoriesName(categoriesName)
                        .orderStatus(order.getStatus()) //이게 DONE 이면 다운로드 버튼 활성화
                        .build();

                orderDone.add(orderDataProducts);
            }
        }
        return orderDone;
    }

    // 구매한 상품중 다운로드 가능한 상품 다운로드
    @Transactional
    public ZipFileResponse.downloadZip downloadDataProduct(Long dataProductId) throws IOException {
        User user = getCurrentUser();
        List<Order> orders = user.getOrders();
        DataProduct dataProduct = dataProductRepository.findById(dataProductId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_DATA_PRODUCT));
        boolean validate = false;

        // 회원이 주문한 상품중 취소되지 않은 상품 중 요청으로 들어온 데이터 상품과 일치하는 상품이 있는지 대조 검증
        for (Order order : orders) {
            if (order.getStatus()!=OrderStatus.CANCEL){
                List<OrderItem> orderItems = order.getOrderItems();
                for (OrderItem orderItem : orderItems) {
                    if (orderItem.getDataProduct() == dataProduct) {
                        log.info(orderItem.getDataProduct().getTitle());
                        validate = true;
                    }
                }
            }
        }

        // 있으면, url과 이름을 조회 해서 프론트로 리턴 그리고 다운로드 수 증가
        if (validate == true) {
            ZipFile zipFile = dataProduct.getZipFile();
            String originalFileName = zipFile.getOriginalFileName();
            String downloadUrl = zipFile.getUploadUrl();

            dataProduct.addDownloadCnt();

            return ZipFileResponse.downloadZip.builder().originalZipName(originalFileName).s3Url(downloadUrl).build();
        }else{
            throw new CustomException(Result.NOT_BUY_PRODUCT);
        }
    }

    private void validateDuplicatedUserEmail(String userEmail) {
        Boolean existsByNickName = userRepository.existsByEmail(userEmail);
        if (existsByNickName) {
            throw new CustomException(Result.USER_EMAIL_DUPLICATED);
        }
    }


    private Authentication getAuthentication(String email, String password) {
        //Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    //회원 가입시 권한을 ROLE_USER로 추가하는 DTO
    private User createEntityUserFromDto(UserRequest.signup request) {
        return User.builder()
                .email(request.getEmail())
                .userPw(passwordEncoder.encode(request.getUserPw()))
                .name(request.getName())
                .phoneNum(request.getPhoneNum())
                .authorities(getAuthorities())
                .build();
    }

    private static Set<Authority> getAuthorities() {
        return Collections.singleton(Authority.builder()
                .authorityName("ROLE_USER")
                .build());
    }

    public UserResponse.user findOne(Long id) {

        User user = userRepository.findById(id).get();
        return UserResponse.user.builder().id(user.getId()).build();
    }

    public Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    public User getCurrentUser() {
        return userRepository
                .findById(SecurityUtils.getCurrentUserId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }

}
