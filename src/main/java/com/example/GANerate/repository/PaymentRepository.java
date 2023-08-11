package com.example.GANerate.repository;

import com.example.GANerate.domain.Payment;
import com.example.GANerate.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByUser(User user);

    Optional<Payment> findByIdAndUser(Long paymentId, User user);
}
