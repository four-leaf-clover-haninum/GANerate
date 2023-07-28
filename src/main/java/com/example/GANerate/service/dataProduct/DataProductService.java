package com.example.GANerate.service.dataProduct;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.GANerate.domain.*;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.*;
import com.example.GANerate.request.dateProduct.DataProductRequest;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


@Service
@RequiredArgsConstructor
@Slf4j
public class DataProductService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final DataProductRepository dataProductRepository;
    private final ZipFileRepository zipFileRepository;
    private final ExampleImageRepository exampleImageRepository;
    private final CategoryRepository categoryRepository;
    private final Product_CategoryRepository product_categoryRepository;
    private final UserRepository userRepository;

    // 전체 데이터 상품 조회
    @Transactional(readOnly = true)
    public Page<DataProductResponse.findDataProducts> findDataProducts(Pageable pageable){
        Page<DataProduct> findAllProduct = dataProductRepository.findAllBy(pageable);
        return findAllProduct.map(dataProduct -> {
            DataProduct findDataProduct = dataProductRepository.findById(dataProduct.getId()).get();
            List<ProductCategory> productCategories = findDataProduct.getProductCategories();
            List<String> categoriesName = new ArrayList<>();
            for (ProductCategory product_category : productCategories) {
                categoriesName.add(product_category.getCategory().getTitle());
            }
            return DataProductResponse.findDataProducts.builder()
                    .id(dataProduct.getId())
                    .buyCnt(dataProduct.getBuyCnt())
                    .title(dataProduct.getTitle())
                    .price(dataProduct.getPrice())
                    .description(dataProduct.getDescription())
                    .imageUrl(dataProduct.getExampleImages().get(0).getImageUrl()) //썸네일 이미지
                    .createdAt(dataProduct.getCreatedAt())
                    .categoriesName(categoriesName)  // categories 필드 추가
                    .build();
            });
    }

    //카테고리별 데이터 조회
    public Page<DataProductResponse.findDataProducts> findCategoryDataProducts(Pageable pageable, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).get();
        List<ProductCategory> product_categories = product_categoryRepository.findAllByCategory(category);
        Page<DataProduct> dataProducts = dataProductRepository.findAllByProductCategoriesIn(product_categories, pageable);

        return dataProducts.map(this::mapToDataProductResponse);
    }

    private DataProductResponse.findDataProducts mapToDataProductResponse(DataProduct dataProduct) {
        return DataProductResponse.findDataProducts.builder()
                .id(dataProduct.getId())
                .buyCnt(dataProduct.getBuyCnt())
                .title(dataProduct.getTitle())
                .price(dataProduct.getPrice())
                .description(dataProduct.getDescription())
                .imageUrl(dataProduct.getExampleImages().get(0).getImageUrl())
                .createdAt(dataProduct.getCreatedAt())
                .categoryId(dataProduct.getProductCategories()
                        .stream()
                        .map(productCategory -> productCategory.getCategory().getId())
                        .collect(Collectors.toList()))
                .categoriesName(dataProduct.getProductCategories()
                        .stream()
                        .map(productCategory -> productCategory.getCategory().getTitle())
                        .collect(Collectors.toList()))
                .build();
    }

    // 단일 데이터 제품 상세조회
    @Transactional(readOnly = true)
    public DataProductResponse.findDataProduct findDataProduct(Long dataProductId){
        DataProduct dataProduct = dataProductRepository.findById(dataProductId).get();

        List<ProductCategory> product_categories = dataProduct.getProductCategories();
        List<String> categoriesName = new ArrayList<>();
        for (ProductCategory productCategory : product_categories) {
            categoriesName.add(productCategory.getCategory().getTitle());
        }
        List<ExampleImage> exampleImages = dataProduct.getExampleImages();
        List<String> imageUrls = new ArrayList<>();

        for (ExampleImage exampleImage : exampleImages) {
            imageUrls.add(exampleImage.getImageUrl());
        }

        DataProductResponse.findDataProduct dto = DataProductResponse.findDataProduct.builder()
                .id(dataProductId)
                .buyCnt(dataProduct.getBuyCnt())
                .dataSize(dataProduct.getDataSize())
                .title(dataProduct.getTitle())
                .price(dataProduct.getPrice())
                .description(dataProduct.getDescription())
                .categoriseName(categoriesName)
                .imageUrl(imageUrls)
                .zipfileName(dataProduct.getZipFile().getOriginalFileName())
                .zipfileSize(dataProduct.getZipFile().getSize())
                .createdAt(dataProduct.getCreatedAt())
                .build();

        return dto;
    }

    // 플라스크와 비동기 통신해야한다. 이건 데이터 생성 요청 폼 데이터 생성인 경우
    @Transactional
    public DataProductResponse.createDataProduct createProduct(Long userId, DataProductRequest.createProduct request, MultipartFile zipfile) throws IOException {
        DataProduct dataProduct = DataProduct.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .buyCnt(0L)
                .price(request.getPrice())
                .dataSize(request.getDataSize())
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

        DataProductResponse.createDataProduct dataProductResponse = DataProductResponse.createDataProduct.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .build();

        return dataProductResponse;
    }

    //데이터 판매 제품 생성
    @Transactional
    public DataProductResponse.saleDataProduct saleDataProduct(
            @AuthenticationPrincipal Long userId, @RequestPart MultipartFile zipfile,
            @RequestPart(value = "files") List<MultipartFile> exampleFiles, @RequestPart DataProductRequest.saleProduct request
    ){
        Long fileCount = countFilesInZip(zipfile);

        DataProduct dataProduct = DataProduct.builder()
                .title(request.getTitle())
                .buyCnt(0l)
                .dataSize(fileCount)
                .price(request.getPrice())
                .description(request.getDescription())
                .build();
        dataProductRepository.save(dataProduct);

        List<Long> categoryIds = request.getCategoryIds();

        for (Long categoryId: categoryIds){
            Category category = categoryRepository.findById(categoryId).get();
            ProductCategory product_category = ProductCategory.builder()
                    .dataProduct(dataProduct)
                    .category(category)
                    .build();

            product_category.setCategory(category);
            product_category.setDataProduct(dataProduct);
        }

        //회원과의 연관관계 설정
        User user = userRepository.findById(userId).get();
        dataProduct.setUser(user);

        List<String> zipInfo = uploadFile(zipfile);

        ZipFile zipFile = ZipFile.builder()
                .originalFileName(zipInfo.get(0))
                .uploadFileName(zipInfo.get(1))
                .uploadUrl(zipInfo.get(2))
                .size((int) zipfile.getSize())
                .build();
        dataProduct.setZipFile(zipFile); //연관관계 설정
        zipFileRepository.save(zipFile);

        for (MultipartFile exampleFile : exampleFiles) {
            List<String> exampleImagesInfo = uploadFile(exampleFile);

            ExampleImage exampleImage = ExampleImage.builder()
                    .originalFileName(exampleImagesInfo.get(0))
                    .uploadFileName(exampleImagesInfo.get(1))
                    .imageUrl(exampleImagesInfo.get(2))
                    .build();
            exampleImage.setDataProduct(dataProduct);
            exampleImageRepository.save(exampleImage);
        }

        //카테고리 설정하자.
        // 중간 테이블 객체 생성해서, 각각 카테고리, 데이텃ㅇ품 넣어주고, 데이ㅓㅌ 상품쪽에서 프로덕트 카테고리 리스트안에 넣어주면 끝??

        DataProductResponse.saleDataProduct saleDataProduct = DataProductResponse.saleDataProduct
                .builder()
                .id(dataProduct.getId())
                .build();

        return saleDataProduct;
    }



//    // 조건 검색
//    @Transactional(readOnly = true)
//    public Page<DataProductResponse.findDataProducts> findDataProductsFiltered(
//            DataProductRequest.filter request){
//
//        Pageable pageable = PageRequest.of(request.getPage(), 10, Sort.by("createdAt").descending());
//        String title = request.getTitle();
//        Long maxPrice = request.getMaxPrice();
//        Long minPrice = request.getMinPrice();
//        List<Long> categoriesId = request.getCategoriesId();
//
//        Page<DataProductResponse.findDataProducts> dataProductsFiltered = dataProductRepository.findDataProductsFiltered(title, maxPrice, minPrice, categoriesId, pageable);
//
//        return null;
//    }

    private List<String> uploadFile(MultipartFile multipartFile) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String uploadFileName = originalFilename + String.valueOf(System.currentTimeMillis());
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());
            amazonS3.putObject(bucket, uploadFileName, multipartFile.getInputStream(), metadata);
            String uploadUrl = amazonS3.getUrl(bucket, uploadFileName).toString();
            List<String> list = new ArrayList<>();
            list.add(originalFilename);
            list.add(uploadFileName);
            list.add(uploadUrl);
            return list;
        }catch (Exception e){
            throw new CustomException(Result.FAIL_UPLOAD_FILE);
        }
    }


    //zip 파일안에 몇개의 이미지가 있는지 확인.
    private Long countFilesInZip(MultipartFile zipfile) {
        Long count = 0l;
        try (ZipInputStream zipInputStream = new ZipInputStream(zipfile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                // 파일 엔트리인 경우에만 개수를 증가시킴
                if (!entry.isDirectory()) {
                    // 파일 엔트리의 이름을 소문자로 변환하여 확장자를 추출
                    String fileName = entry.getName().toLowerCase();
                    if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
                        count++;
                    } else {
                        // png 또는 jpg가 아닌 파일이 포함된 경우 에러 처리
                        throw new CustomException(Result.INVALID_FILE);
                    }
                    zipInputStream.closeEntry();
                }
            }
            return count;
        } catch (IOException e) {
            throw new CustomException(Result.FAIL);
        }
    }
}
