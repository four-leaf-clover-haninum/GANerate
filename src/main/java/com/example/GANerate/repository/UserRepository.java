package com.example.GANerate.repository;

import com.example.GANerate.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String userEmail);
    @EntityGraph(attributePaths = "authorities") // email로 user 조회할때 authorities도 같이 조회
    Optional<User> findOneWithAuthoritiesByEmail(String email);
}
