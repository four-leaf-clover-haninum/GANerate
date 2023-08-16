package com.example.GANerate.service.dataProduct;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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

    List<String> allowedExtensions = List.of("jpg", "jpeg", "png");

    // 전체 데이터 상품 조회
    @Transactional(readOnly = true)
    public Page<DataProductResponse.findDataProducts> findDataProducts(Pageable pageable){
        try {
            Page<DataProduct> allProducts = dataProductRepository.findAllBy(pageable);
            return allProducts.map(this::convertToFindDataProductsResponse);
        }catch (Exception e){
            throw new CustomException(Result.NOT_FOUND_DATA_PRODUCT);
        }
    }

    //카테고리별 데이터 조회
    @Transactional(readOnly = true)
    public Page<DataProductResponse.findDataProducts> findCategoryDataProducts(Pageable pageable, Long categoryId) {

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_CATEGORY));
        List<ProductCategory> productCategories = category.getProductCategories();

        try {
            Page<DataProduct> dataProducts = dataProductRepository.findAllByProductCategoriesIn(productCategories, pageable);
            return dataProducts.map(this::convertToFindDataProductsResponse);
        }catch (Exception e){
            throw new CustomException(Result.NOT_FOUND_DATA_PRODUCT);
        }
    }

    // 데이터 상품 상세 조회
    @Transactional(readOnly = true)
    public DataProductResponse.findDataProduct findDataProduct(Long dataProductId){
        DataProduct dataProduct = dataProductRepository.findById(dataProductId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_DATA_PRODUCT));

        List<ProductCategory> productCategories = dataProduct.getProductCategories();
        List<String> categoryNames = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        extractCategoryDetails(productCategories, categoryNames,categoryIds);

        List<ExampleImage> exampleImages = dataProduct.getExampleImages();
        List<String> imageUrls = new ArrayList<>();
        for (ExampleImage exampleImage : exampleImages) {
            imageUrls.add(exampleImage.getImageUrl());
        }

        DataProductResponse.findDataProduct response = createFindDataProduct(dataProductId, dataProduct, categoryNames, categoryIds, imageUrls);
        return response;
    }

    // 이 요청은 비동기 처리해야됨. 결제 완료후 프론트에서 요청해야함. 추가로 user가 생성한 데이터 상품이나 구매한 데이터 상품으로 연관관계 설정
    // order 생성(주문 로직단에서 무조건 생성해서 이 메서드로 넘겨줘야함.) -> dataproduct 생성 -> orderItem 생성 -> zip 생성 -> examplimage 생성
    // 데이터 생성 요청(GANerate)
    @Transactional
    public DataProductResponse.createDataProduct createDataProduct(DataProductRequest.createProduct request, MultipartFile zipFile) throws Exception {

        User user = userService.getCurrentUser();

        // 결제 로직 구성후 변경(이건 결제 파트에서 해야함. 그래야 해당 메서드를 통해 ORDER->DONE이 됨)
//        Long orderId = request.getOrderId();
//        Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_ORDER));


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
        //전달받은 zip 내부에는 이미지만 있는지 검증 및 예시 생성하기 위한 샘플 이미지 갯수 세기
        processZipFile(zipFile);

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

//            order.setStatus(OrderStatus.DONE);

            zipFileRepository.save(uploadZipFile);
            dataProduct.setZipFile(uploadZipFile);
            dataProduct.setUser(user);

            // 추가로 zip File 로 부터 이미지 3장 뽑아와서 example 이미지로 만들고 연관관계
            S3Object object = amazonS3.getObject(bucket, uploadZipFile.getUploadFileName());
            try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(object.getObjectContent()))) {
                ZipEntry entry;
                int imageCount = 0;

                // 압축해제 3개의 이미지만 뽑아옴(예시 이미지로 사용하려고)
                while ((entry = zis.getNextEntry()) != null && imageCount < 3) {

                    String originalImageName = entry.getName();
                    int index = originalImageName.lastIndexOf(".");
                    String fileName = originalImageName.substring(0,index); //이름
                    String fileExtension = getFileExtension(originalImageName); // 확장자

                    // mac에서는 zip 압축시 해당 이름으로 시작하는 메타데이터가 같이 있기 때문에 건너 뛰어줘야한다, 실제 서버에서는 필요없는 코드 왜냐면 데이터 업로드하는 시점에서 이미 걸러짐
                    if (fileName.startsWith("__MACOSX/") || fileName.startsWith("._")) {
                        continue;
                    }

                    if (fileExtension.endsWith("jpg") || fileExtension.endsWith("jpeg") || fileExtension.endsWith("png")) {
                        // 3. Select image files
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, len);
                        }
                        byte[] imageBytes = outputStream.toByteArray();

                        // 4. Upload image to S3
                        InputStream imageStream = new ByteArrayInputStream(imageBytes);
                        String uploadExamImageName = fileName + String.valueOf(System.currentTimeMillis())+"."+fileExtension;
                        log.info(uploadExamImageName);
                        amazonS3.putObject(bucket, uploadExamImageName, imageStream, new ObjectMetadata());
                        imageCount++;
                        String uploadImageUrl = amazonS3.getUrl(bucket, uploadFileName).toString();

                        ExampleImage exampleImage = ExampleImage.builder()
                                .imageUrl(uploadImageUrl)
                                .uploadFileName(uploadExamImageName)
                                .originalFileName(originalImageName)
                                .build();

                        exampleImage.setDataProduct(dataProduct);
                    }
                }

            }catch (Exception e) {
                throw new CustomException(Result.FAIL_LOAD_EXAMIMAGE);
            }

            // return 값으로 뭘 줘야될까? 데이터 생성되었다 다운로드 창에서 확인해라 정도만 주면될듯? 굳이 데이터 상품 정보를?
            return DataProductResponse.createDataProduct.builder().build();
        } catch (RestClientException e) {
            throw new CustomException(Result.FAIL_CREATE_DATA);
        }
    }

    /*
    여기서 부터는 데이터 판매 로직 (데이터 상품을 판매하고자 하는 유저가 보유한 데이터 zip파일 업로드, 예시 이미지 업로드, 판매 폼 작성)
    굳이 한번에 데이터 업로드 및 폼 작성을 처리하지 않고 나눠서 하는 이유는 ~~
     */
    // 데이터 zip 업로드
    @Transactional
    public DataProductResponse.saleDataProductZip saleDataProductZip(MultipartFile zipFile) throws Exception {

        // 판매 데이터 zip내부에 이미지 파일 개수 세기
        Long fileCount = processZipFile(zipFile);
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
    public List<DataProductResponse.saleDataProductImages> saleDataProductImages(List<MultipartFile> exampleImages) throws IOException {
        List<DataProductResponse.saleDataProductImages> response = new ArrayList<>();
        for (MultipartFile exampleImageFile : exampleImages) {

            //이미지 파일인지 유효성 검사
            if(!isImageFile(exampleImageFile)){
                throw new CustomException(Result.INCORRECT_FILE_TYPE_Only_JPG_JPEG_PNG);
            }

            List<String> exampleImagesInfo = uploadFile(exampleImageFile);

            ExampleImage exampleImage = ExampleImage.builder()
                    .originalFileName(exampleImagesInfo.get(0))
                    .uploadFileName(exampleImagesInfo.get(1))
                    .imageUrl(exampleImagesInfo.get(2))
                    .build();
            exampleImageRepository.save(exampleImage);

            DataProductResponse.saleDataProductImages responseDto = DataProductResponse.saleDataProductImages.builder()
                    .imageUrl(exampleImagesInfo.get(2)).build();
            response.add(responseDto);
        }
        return response;
    }

    //데이터 판매 폼 업로드하기 전에 결제 하고
    @Transactional
    public DataProductResponse.saleDataProduct saleDataProductForm(DataProductRequest.saleProduct request){

        User user = userService.getCurrentUser();

        // 데이터 상품 생성
        DataProduct dataProduct = DataProduct.builder()
                .title(request.getTitle())
                .buyCnt(0L)
                .dataSize(request.getDataSize()) // 로컬스토리지에 있는 zip 내의 데이터 갯수를 전달
                .price(request.getPrice())
                .description(request.getDescription())
                .build();
        dataProductRepository.save(dataProduct);
        dataProduct.setUser(user);

        // 상품카테고리 테이블 객체 생성
        for (Long categoryId: request.getCategoryIds()){
            Category category = categoryRepository.findById(categoryId).get();
            ProductCategory product_category = ProductCategory.builder()
                    .dataProduct(dataProduct)
                    .category(category)
                    .build();

            dataProduct.addProductCategory(product_category);
            category.addProductCategory(product_category);
        }

        // zipfile 연관관계 설정
        try {
            ZipFile zipFile = zipFileRepository.findByUploadUrl(request.getZipFileUrl());
            dataProduct.setZipFile(zipFile); //연관관계 설정
        }catch (Exception e){
            throw new CustomException(Result.NOT_FOUND_ZIPFILE);
        }

        // 예시 이미지 연관관계 설정
        for (String exampleImageUrl: request.getImageUrls()) {
            try {
                ExampleImage exampleImage = exampleImageRepository.findByImageUrl(exampleImageUrl);
                exampleImage.setDataProduct(dataProduct);
            }catch (Exception e){
                throw new CustomException(Result.NOT_FOUND_EXAMIMAGE);
            }
        }

        // 생성된 데이터 상품 키값 반환
        DataProductResponse.saleDataProduct saleDataProduct = DataProductResponse.saleDataProduct
                .builder()
                .id(dataProduct.getId())
                .build();

        return saleDataProduct;
    }
    /*
    여기까지가 데이터 판매
     */

    // 데이터 상품 top3 조회
    @Transactional(readOnly = true)
    public List<DataProductResponse.findDataProducts> findTop3Download(){
        try {
            List<DataProduct> dataProducts = dataProductRepository.findTop3ByOrderByBuyCntDesc();
            List<DataProductResponse.findDataProducts> findTop3DataProducts = new ArrayList<>();
            for (DataProduct dataProduct : dataProducts) {
                DataProductResponse.findDataProducts findDataProducts = convertToFindDataProductsResponse(dataProduct);
                findTop3DataProducts.add(findDataProducts);
            }
            return findTop3DataProducts;
        }catch (Exception e){
            throw new CustomException(Result.NOT_FOUND_DATA_PRODUCT);
        }
    }

    // s3에 업로드
    private List<String> uploadFile(MultipartFile multipartFile) {
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            int index = originalFilename.lastIndexOf(".");

            String fileName = originalFilename.substring(0,index); //이름
            String ext = originalFilename.substring(index + 1); //확장자

            String uploadFileName = fileName + String.valueOf(System.currentTimeMillis())+"."+ext;

            // s3 업로드
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());
            amazonS3.putObject(bucket, uploadFileName, multipartFile.getInputStream(), metadata);

            // s3로 부터 다운로드 url 반환
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

    // 예시 이미지 업로드시 예시 이미지가 이미지 파일인지 검증
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

    // zip 내부의 파일이 이미지인지 확인하고, 갯수를 세는 로직
    public Long processZipFile(MultipartFile multipartFile) throws Exception {
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png");
        Long imageCount = 0L;

        try (ZipInputStream zipInputStream = new ZipInputStream(multipartFile.getInputStream())) {
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                String fileExtension = getFileExtension(fileName);

                // mac에서는 zip 압축시 해당 이름으로 시작하는 메타데이터가 같이 있기 때문에 건너 뛰어줘야한다,
                if (fileName.startsWith("__MACOSX/") || fileName.startsWith("._")) {
                    continue;
                }

                if (allowedExtensions.contains(fileExtension)) {
                    imageCount++;
                } else {
                    throw new CustomException(Result.INCORRECT_FILE_TYPE_Only_JPG_JPEG_PNG);
                }
            }
        }
        if (imageCount==0){
            throw new CustomException(Result.NO_IMAGE_FILE);
        }
        return imageCount;
    }

    // 파일이름에서 확장자 분리
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    // DataProduct를  findDataProducts response로 변환하는 메서드
    public DataProductResponse.findDataProducts convertToFindDataProductsResponse(DataProduct dataProduct) {
        List<String> categoryNames = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        extractCategoryDetails(dataProduct.getProductCategories(), categoryNames, categoryIds);

        return DataProductResponse.findDataProducts.builder()
                .dataProductId(dataProduct.getId())
                .buyCnt(dataProduct.getBuyCnt())
                .title(dataProduct.getTitle())
                .price(dataProduct.getPrice())
                .description(dataProduct.getDescription())
                .imageUrl(dataProduct.getExampleImages().get(0).getImageUrl()) // Thumbnail image
                .createdAt(dataProduct.getCreatedAt())
                .categoryNames(categoryNames)
                .categoryIds(categoryIds)
                .build();
    }

    // DataProduct에 연관된 카테고리 정보 추출
    private void extractCategoryDetails(List<ProductCategory> productCategories, List<String> categoryNames, List<Long> categoryIds) {
        for (ProductCategory productCategory : productCategories) {
            Category category = productCategory.getCategory();
            categoryNames.add(category.getTitle());
            categoryIds.add(category.getId());
        }
    }

    // findDataProduct 응답 생성
    private DataProductResponse.findDataProduct createFindDataProduct(Long dataProductId, DataProduct dataProduct, List<String> categoryNames, List<Long> categoryIds, List<String> imageUrls) {
        DataProductResponse.findDataProduct response = DataProductResponse.findDataProduct.builder()
                .dataProductId(dataProductId)
                .buyCnt(dataProduct.getBuyCnt())
                .dataSize(dataProduct.getDataSize())
                .title(dataProduct.getTitle())
                .price(dataProduct.getPrice())
                .description(dataProduct.getDescription())
                .categoryNames(categoryNames)
                .categoryIds(categoryIds)
                .imageUrl(imageUrls)
                .zipfileName(dataProduct.getZipFile().getOriginalFileName())
                .zipfileSize(dataProduct.getZipFile().getSizeGb())
                .createdAt(dataProduct.getCreatedAt())
                .build();
        return response;
    }
}
