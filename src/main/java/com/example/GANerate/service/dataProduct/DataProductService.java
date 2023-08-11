package com.example.GANerate.service.dataProduct;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.GANerate.domain.*;
import com.example.GANerate.enumuration.OrderStatus;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.*;
import com.example.GANerate.request.ZipFileRequest;
import com.example.GANerate.request.dateProduct.DataProductRequest;
import com.example.GANerate.response.ZipFileResponse;
import com.example.GANerate.response.dateProduct.DataProductResponse;
import com.example.GANerate.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final ProductCategoryRepository productCategoryRepository;
    private final UserService userService;
    private final RestTemplate restTemplate; // 근데 이렇게 빈으로 넣으면 인스턴스가 하나만 셍성돼서 다른 사영자들 동시 요청들어올대 처리 가능?
    private final OrderRepository orderRepository;


    // 전체 데이터 상품 조회
    @Transactional(readOnly = true)
    public Page<DataProductResponse.findDataProducts> findDataProducts(Pageable pageable){
        Page<DataProduct> findAllProduct = dataProductRepository.findAllBy(pageable);
        return findAllProduct.map(dataProduct -> {
            Optional<DataProduct> findDataProducts = dataProductRepository.findById(dataProduct.getId());
            if(findDataProducts.isPresent()){
                DataProduct findDataProduct = findDataProducts.get();
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
            }else {
                throw new CustomException(Result.NOT_FOUND_DATA_PRODUCT);
            }
        });
    }

    //카테고리별 데이터 조회
    @Transactional(readOnly = true)
    public Page<DataProductResponse.findDataProducts> findCategoryDataProducts(Pageable pageable, Long categoryId) {

        Optional<Category> findCategory = categoryRepository.findById(categoryId);
        if (findCategory.isPresent()){
            Category category = findCategory.get();
            List<ProductCategory> product_categories = productCategoryRepository.findAllByCategory(category);
            Page<DataProduct> dataProducts = dataProductRepository.findAllByProductCategoriesIn(product_categories, pageable);
            return dataProducts.map(this::mapToDataProductResponse);
        }else {
            throw new CustomException(Result.NON_EXIST_CATEGORY);
        }
    }


    // 단일 데이터 제품 상세조회(data.sql로 넣은 애들은 조회 안됨.왜냐면 연관관계 메서드 설정을 안함 따라서 상품 올리고 그거 조회)
    @Transactional(readOnly = true)
    public DataProductResponse.findDataProduct findDataProduct(Long dataProductId){
        DataProduct dataProduct = dataProductRepository.findById(dataProductId).orElseThrow(
                () -> new CustomException(Result.NOT_FOUND_DATA_PRODUCT));

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
                .zipfileSize(dataProduct.getZipFile().getSizeGb())
                .createdAt(dataProduct.getCreatedAt())
                .build();

        return dto;
    }

    // 이 요청은 비동기 처리해야됨. 결제 완료후 프론트에서 요청해야함. 추가로 user가 생성한 데이터 상품이나 구매한 데이터 상품으로 연관관계 설정
    // order 생성(주문 로직단에서 무조건 생성해서 이 메서드로 넘겨줘야함.) -> dataproduct 생성 -> orderItem 생성 -> zip 생성 -> examplimage 생성
    @Transactional
    public DataProductResponse.createDataProduct createDataProduct(DataProductRequest.createProduct request, MultipartFile zipFile) throws IOException {

        User user = userService.getCurrentUser();

        // 결제 로직 구성후 변경(이건 결제 파트에서 해야함. 그래야 해당 메서드를 통해 ORDER->DONE이 됨)
        Long orderId = request.getOrderId();
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_ORDER));


        //전달받은 zip을 업로드 하고, 그걸 db에 저장하고, 플라스크로 그 객체 id를 전달
        // DataProduct 생성
        DataProduct dataProduct = DataProduct.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .buyCnt(0L)
                .price(request.getDataSize()*1000) // 가격은 별도 로직 필요
                .dataSize(request.getDataSize())
                .build();
        dataProductRepository.save(dataProduct);

        // 결제 로직 구성후 변경
//        OrderItem orderItem = OrderItem.builder()
//                .order(order)
//                .dataProduct(dataProduct)
//                .build();
//        orderItem.setOrder(order);


        // 카테고리 가져오기
        List<Long> categoryIds = request.getCategoryIds();
        List<Category> categories = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CustomException(Result.NON_EXIST_CATEGORY));
            categories.add(category);
        }

        for (Category category : categories) {
            ProductCategory productCategory = ProductCategory.builder().category(category).dataProduct(dataProduct).build();
            productCategoryRepository.save(productCategory);
            // 연관관계 설정
            dataProduct.addProductCategory(productCategory);
            category.addProductCategory(productCategory);
        }

        //전달받은 zip 내부에는 이미지만 있는지 검증
        isImageInZip(zipFile);

        // s3에 업로드
        List<String> fileInfo = uploadFile(zipFile);

        String originalFileName = fileInfo.get(0);
        String uploadFileName = fileInfo.get(1);
        String uploadUrl = fileInfo.get(2);

        // 플라스크로 전달할 정보: 오리지날 이름, 업로드 유알엘, 생성 개수
        Long createDataSize = request.getDataSize();
        // 플라스크로 전달
        try{
            String url = "http://127.0.0.1:5000/ganerate"; //추후 ec2꺼로 변경
            // 요청으로 생성할 데이터 수와 예시 zipid 전달
            ZipFileRequest.ganerateZip requestToFlask = ZipFileRequest.ganerateZip.builder().uploadUrl(uploadUrl).originalFileName(originalFileName).uploadFileName(uploadFileName).createDataSize(createDataSize).build(); // 파일 사이즈 전달

            log.info("=======");
            // 응답으로 생성된 데이터 수와 실제 zipid 전달
            ZipFileResponse.ganeratedZip ganeratedZip = restTemplate.postForObject(url, requestToFlask, ZipFileResponse.ganeratedZip.class);

            ZipFile uploadZipFile = ZipFile.builder()
                    .uploadFileName(ganeratedZip.getUploadFileName())
                    .uploadUrl(ganeratedZip.getUploadUrl())
                    .originalFileName(ganeratedZip.getOriginalFileName())
                    .sizeGb(ganeratedZip.getSizeGb())
                    .build();

            order.setStatus(OrderStatus.DONE);

            zipFileRepository.save(uploadZipFile);
            dataProduct.setZipFile(uploadZipFile);
            dataProduct.setUser(user);

            // 추가로 zip File 로 부터 이미지 3장 뽑아와서 example 이미지로 만들고 연관관계


            // return 값으로 뭘 줘야될까? 데이터 생성되었다 다운로드 창에서 확인해라 정도만 주면될듯? 굳이 데이터 상품 정보를?
            return DataProductResponse.createDataProduct.builder().build();
        } catch (RestClientException e) {
            throw new CustomException(Result.FAIL_CREATE_DATA);
        }
    }

    // 데이터 zip 업로드
    @Transactional
    public DataProductResponse.saleDataProductZip saleDataProductZip(MultipartFile zipFile){
        Long fileCount = countFilesInZip(zipFile);

        List<String> zipInfo = uploadFile(zipFile);

        ZipFile zip = ZipFile.builder()
                .originalFileName(zipInfo.get(0))
                .uploadFileName(zipInfo.get(1))
                .uploadUrl(zipInfo.get(2))
                .sizeGb((double) zipFile.getSize()/(1024*1024*1024)) //gb로
                .isExamZip(false)
                .build();

        zipFileRepository.save(zip);

         return DataProductResponse.saleDataProductZip.builder()
                 .zipFileUrl(zipInfo.get(2)).dataSize(fileCount).build();
    }

    // 데이터 예시 이미지 업로드
    @Transactional
    public List<DataProductResponse.saleDataProductImages> saleDataProductImages(List<MultipartFile> exampleImages){
        List<DataProductResponse.saleDataProductImages> response = new ArrayList<>();
        for (MultipartFile exampleImageFile : exampleImages) {

            //이미지 파일인지 유효성 검사
            try {
                boolean imageFile = isImageFile(exampleImageFile);
            } catch (CustomException | IOException e) {
                throw new CustomException(Result.INCORRECT_FILE_TYPE_Only_JPG_JPEG_PNG);
            }

            List<String> exampleImagesInfo = uploadFile(exampleImageFile);

            ExampleImage exampleImage = ExampleImage.builder()
                    .originalFileName(exampleImagesInfo.get(0))
                    .uploadFileName(exampleImagesInfo.get(1))
                    .imageUrl(exampleImagesInfo.get(2))
                    .build();
            exampleImageRepository.save(exampleImage);

            DataProductResponse.saleDataProductImages lmg = DataProductResponse.saleDataProductImages.builder()
                    .imageUrl(exampleImagesInfo.get(2)).build();
            response.add(lmg);
        }
        return response;
    }

    //데이터 판매 폼 업로드
    @Transactional
    public DataProductResponse.saleDataProduct saleDataProductForm(DataProductRequest.saleProduct request){

        User user = userService.getCurrentUser();
        DataProduct dataProduct = DataProduct.builder()
                .title(request.getTitle())
                .buyCnt(0L)
                .dataSize(request.getDataSize())
                .price(request.getPrice())
                .description(request.getDescription())
                .build();
        dataProductRepository.save(dataProduct);
        dataProduct.setUser(user);

        for (Long categoryId: request.getCategoryIds()){
            Category category = categoryRepository.findById(categoryId).get();
            ProductCategory product_category = ProductCategory.builder()
                    .dataProduct(dataProduct)
                    .category(category)
                    .build();

            dataProduct.addProductCategory(product_category);
            category.addProductCategory(product_category);
        }

        ZipFile zipFile = zipFileRepository.findByUploadUrl(request.getZipFileUrl());
        dataProduct.setZipFile(zipFile); //연관관계 설정

        for (String exampleImageUrl: request.getImageUrls()) {


            ExampleImage exampleImage = exampleImageRepository.findByImageUrl(exampleImageUrl);
            exampleImage.setDataProduct(dataProduct);
        }

        DataProductResponse.saleDataProduct saleDataProduct = DataProductResponse.saleDataProduct
                .builder()
                .id(dataProduct.getId())
                .build();

        return saleDataProduct;
    }

    // 데이터 상품 top3 조회
    @Transactional
    public List<DataProductResponse.findDataProducts> findTop3Download(){
        List<DataProduct> dataProducts;
        try {
            dataProducts = dataProductRepository.findTop3ByOrderByBuyCntDesc();
            // dataProducts를 사용하는 로직
        }catch (CustomException e){
            throw new CustomException(Result.NOT_FOUND_DATA_PRODUCT);
        }
        List<DataProductResponse.findDataProducts> findTop3DataProducts = new ArrayList<>();
        for(DataProduct dataProduct : dataProducts){
            List<ProductCategory> productCategories = dataProduct.getProductCategories();
            List<Long> categoryIds = new ArrayList<>();
            List<String> categoryTitles = new ArrayList<>();
            for(ProductCategory productCategory : productCategories){
                Category category = productCategory.getCategory();
                categoryIds.add(category.getId());
                categoryTitles.add(category.getTitle());
            }

            DataProductResponse.findDataProducts response = DataProductResponse.findDataProducts.builder()
                    .id(dataProduct.getId())
                    .title(dataProduct.getTitle())
                    .description(dataProduct.getDescription())
                    .price(dataProduct.getPrice())
                    .buyCnt(dataProduct.getBuyCnt())
                    .imageUrl(dataProduct.getExampleImages().get(0).getImageUrl())
                    .categoryId(categoryIds)
                    .categoriesName(categoryTitles)
                    .createdAt(dataProduct.getCreatedAt())
                    .build();

            findTop3DataProducts.add(response);
        }
        return findTop3DataProducts;
    }

    // s3에 업로드
    private List<String> uploadFile(MultipartFile multipartFile) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            int index = originalFilename.lastIndexOf(".");
            String fileName = originalFilename.substring(0,index);
            String ext = originalFilename.substring(index + 1); //확장자

            String uploadFileName = fileName + String.valueOf(System.currentTimeMillis())+"."+ext;
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

    public boolean isImageFile(MultipartFile file) throws IOException {
        // 파일이 비어있는지 확인합니다.
        if (file.isEmpty()) {
            return false;
        }

        // 파일이 유효한 이미지 확장자를 가지고 있는지 확인합니다.
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && !originalFilename.matches(".+\\.(jpg|jpeg|png)$")) {
            return false;
        }

        // 모든 검사가 통과하면 파일을 이미지로 간주합니다.
        return true;
    }



    // zip 파일안에 몇개의 이미지가 있는지 확인하고, 파일이 하나도 없는 경우 에러 처리
//    private Long countFilesInZip(MultipartFile zipfile) {
//        Long count = 0L;
//        boolean hasImage = false;
//
//        try (ZipInputStream zipInputStream = new ZipInputStream(zipfile.getInputStream())) {
//            ZipEntry zipEntry = zipInputStream.getNextEntry();
//            while (zipEntry != null) {
//                // 파일 엔트리인 경우에만 개수를 증가시킴
//                if (!zipEntry.isDirectory()) {
//                    // 파일 엔트리의 이름을 소문자로 변환하여 확장자를 추출
//                    String fileName = zipEntry.getName().toLowerCase();
//
//                    if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
//                        count++;
//                        log.info(String.valueOf(count));
//                        hasImage = true; // 이미지 파일이 하나라도 있음을 표시
//                    } else {
//                        // png 또는 jpg가 아닌 파일이 포함된 경우 에러 처리
//                        throw new CustomException(Result.INVALID_FILE);
//                    }
//                }
//
//                // 다음 엔트리로 이동
//                zipEntry = zipInputStream.getNextEntry();
//            }
//
//            // 이미지 파일이 하나도 없는 경우 에러 처리
//            if (!hasImage) {
//                throw new CustomException(Result.NO_IMAGE_FILE);
//            }
//
//            return count;
//        } catch (IOException e) {
//            throw new CustomException(Result.FAIL);
//        }
//    }
    // zip 안에 이미지 파일인지 확인하는 방법
    public void isImageInZip(MultipartFile zipFile) {
        // 빈 zip인지 확인
        try {
            if (zipFile.isEmpty()) {
                throw new CustomException(Result.NO_IMAGE_FILE);
            }

            byte[] zipData = zipFile.getBytes();

            containsOnlyImages(zipData);


        } catch (IOException e) {
            throw new CustomException(Result.INCORRECT_FILE_TYPE_Only_JPG_JPEG_PNG);
        }
    }

    private boolean containsOnlyImages(byte[] zipData) {
        try (ZipInputStream zipStream = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = zipStream.getNextEntry()) != null) {
                if (!isImage(entry.getName())) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            throw new CustomException(Result.NO_IMAGE_FILE);
        }
    }

    private boolean isImage(String filename) {
        // 이미지 확장자 리스트
        String[] imageExtensions = {".jpg", ".jpeg", ".png"};

        for (String ext : imageExtensions) {
            if (filename.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    // zip 안에 이미지 파일 몇개인지 세는 작업
    public Long countFilesInZip(MultipartFile zipFile) {
        Long fileCount = 0L;
        boolean hasImageFile = false;

        try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.getInputStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    fileCount++;
                    // Check if the file has a valid image extension
                    String fileName = entry.getName().toLowerCase();
                    if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                        hasImageFile = true;
                    } else {
                        throw new CustomException(Result.INVALID_FILE);
                    }
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (fileCount == 0) {
            throw new CustomException(Result.FAIL);
        }
        return fileCount/2; //왜 파일 갯수가 2배 되어서 나오는지 모르겠음;;;
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
}
