package com.example.GANerate.repository;

import com.example.GANerate.domain.DataProduct;
import com.example.GANerate.domain.Heart;
import com.example.GANerate.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRepository extends JpaRepository<Heart, Long> {

    Heart findByUserAndDataProduct(User user, DataProduct dataProduct);
}
