package com.example.GANerate.repository;

import com.example.GANerate.domain.ExampleImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleImageRepository extends JpaRepository<ExampleImage, Long> {
    ExampleImage findByImageUrl(String imageUrl);
}
