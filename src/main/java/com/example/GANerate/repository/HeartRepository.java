package com.example.GANerate.repository;

import com.example.GANerate.domain.DataProduct;
import com.example.GANerate.domain.Heart;
import com.example.GANerate.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {

    Heart findByUserAndDataProduct(User user, DataProduct dataProduct);

    @Query("SELECT H.dataProduct FROM Heart H WHERE H.user = :user")
    Optional<List<DataProduct>> findDataProductsByUser(User user);
}
