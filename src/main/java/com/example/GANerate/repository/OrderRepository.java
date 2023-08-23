package com.example.GANerate.repository;

import com.example.GANerate.domain.DataProduct;
import com.example.GANerate.domain.Order;
import com.example.GANerate.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.user = :user")
    Optional<List<Order>> findOrdersByUser(User user);


}
