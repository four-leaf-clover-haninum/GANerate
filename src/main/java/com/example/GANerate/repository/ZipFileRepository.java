package com.example.GANerate.repository;

import com.example.GANerate.domain.ZipFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZipFileRepository extends JpaRepository<ZipFile, Long> {
    ZipFile findByUploadUrl(String uploadUrl);
}
