package com.example.GANerate.service.heart;

import com.example.GANerate.config.SecurityUtils;
import com.example.GANerate.domain.DataProduct;
import com.example.GANerate.domain.Heart;
import com.example.GANerate.domain.User;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.DataProductRepository;
import com.example.GANerate.repository.HeartRepository;
import com.example.GANerate.repository.UserRepository;
import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.heart.HeartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.GANerate.enumuration.Result.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class HeartService {

    private final UserRepository userRepository;
    private final DataProductRepository dataProductRepository;
    private final HeartRepository heartRepository;

    @Transactional
    public HeartResponse like(Long dataProductId) {
        User user = getCurrentUser();
//        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_USER));
        DataProduct dataProduct = dataProductRepository.findById(dataProductId).orElseThrow(()-> new CustomException(Result.NOT_FOUND_DATA_PRODUCT));

        //이미 좋아요 했으면
        Heart findHeart = heartRepository.findByUserAndDataProduct(user, dataProduct);
        if (findHeart!=null){
            throw new CustomException(Result.DUPLICATED_HEART);
        }

        Heart heart = Heart.builder()
                .user(user)
                .dataProduct(dataProduct)
                .build();
        heartRepository.save(heart);

        heart.setUser(user);

        return HeartResponse.builder().heartId(heart.getId()).build();
    }

    @Transactional
    public void unlike(Long userId, Long dataProductId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        DataProduct dataProduct = dataProductRepository.findById(dataProductId).orElseThrow(()-> new CustomException(Result.NOT_FOUND_DATA_PRODUCT));

        // 삭제시 좋아요가 안되어 있으면 에러
        Heart heart = heartRepository.findByUserAndDataProduct(user, dataProduct);
        if(heart==null){
            throw new CustomException(Result.FAIL);
        }
        heartRepository.delete(heart);

    }

    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    private User getCurrentUser() {
        return userRepository
                .findById(getCurrentUserId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
    }
}
