package com.example.GANerate.service.dataProduct;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.GANerate.domain.Category;
import com.example.GANerate.domain.DataProduct;
import com.example.GANerate.domain.Product_Category;
import com.example.GANerate.domain.ZipFile;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.CategoryRepository;
import com.example.GANerate.repository.DataProductRepository;
import com.example.GANerate.repository.ZipFileRepository;
import com.example.GANerate.request.dateProduct.DataProductRequest;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DataProductService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final DataProductRepository dataProductRepository;
    private final ZipFileRepository zipFileRepository;
    private final CategoryRepository categoryRepository;

    // 전체 데이터 상품 조회
    @Transactional(readOnly = true)
    public Page<DataProductResponse.findAllCreateAt> findAllDataProduct(Pageable pageable){
        Page<DataProduct> findAllProduct = dataProductRepository.findAllBy(pageable);
        return findAllProduct.map(dataProduct -> {
            DataProduct findDataProduct = dataProductRepository.findById(dataProduct.getId()).get();
            List<Product_Category> product_categories = findDataProduct.getProduct_categories();
            List<String> categoriesName = new ArrayList<>();
            for (Product_Category product_category : product_categories) {
                categoriesName.add(product_category.getCategory().getTitle());
            }
            return DataProductResponse.findAllCreateAt.builder()
                    .id(dataProduct.getId())
                    .downloadCnt(dataProduct.getBuyCnt())
                    .title(dataProduct.getTitle())
                    .price(dataProduct.getPrice())
                    .description(dataProduct.getDescription())
                    .imageUrl(dataProduct.getImageUrl())
                    .createdAt(dataProduct.getCreatedAt())
                    .categoriesName(categoriesName)  // categories 필드 추가
                    .build();
        });
    }

    // 플라스크와 비동기 통신해야한다.
    @Transactional
    public DataProductResponse.createProduct createProduct(Long userId, DataProductRequest.createProduct request, MultipartFile zipfile) throws IOException {
        DataProduct dataProduct = DataProduct.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .buyCnt(0L)
                .price(request.getPrice())
                .dataSize(request.getDataSize())
                .product_categories(request.getProduct_categories())
                .build();
        dataProductRepository.save(dataProduct);

        try{
            String originalFileName = zipfile.getOriginalFilename();
            String uploadFileName = originalFileName + String.valueOf(System.currentTimeMillis());
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(zipfile.getSize());
            metadata.setContentType(zipfile.getContentType());

            amazonS3.putObject(bucket, uploadFileName, zipfile.getInputStream(), metadata);

            // 업로드 url 가져오기
            String uploadUrl = amazonS3.getUrl(bucket, uploadFileName).toString();

            ZipFile zipFile = ZipFile.builder()
                    .originalFileName(originalFileName)
                    .uploadFileName(uploadFileName)
                    .uploadUrl(uploadUrl)
                    .size((int) zipfile.getSize())
                    .build();

            zipFileRepository.save(zipFile);
        }catch (CustomException e){

        }

        DataProductResponse.createProduct dataProductResponse = DataProductResponse.createProduct.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();

        return dataProductResponse;
    }
}
