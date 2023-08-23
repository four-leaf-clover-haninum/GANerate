package com.example.GANerate.controller;

import com.example.GANerate.domain.Payment;
import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.payment.PaymentResponse;
import com.example.GANerate.service.PaymentService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@Slf4j
public class PaymentController {
    //토큰 발급을 위해 아임포트에서 제공해주는 rest api 사용.(gradle로 의존성 추가)
    private final IamportClient iamportClient;

    //생성자로 rest api key와 secret을 입력해서 토큰 바로생성.
    public PaymentController(@Value("${pgmodule.app-id}") String apiKey,
                             @Value("${pgmodule.secret-key}") String apiSecret) {
        this.iamportClient = new IamportClient(apiKey, apiSecret);
    }

    @Autowired
    private PaymentService paymentService;

    /**
     * 결제검증을 위한 메서드<br>
     * map에는 imp_uid, amount, actionBoardNo 이 키값으로 넘어옴.
     */
    @PostMapping("/v1/payments/verifyIamport")
    public CustomResponseEntity<PaymentResponse.CreatePayment> verifyIamport(@RequestBody Map<String,String> map) throws IamportResponseException, IOException{

        String impUid = map.get("imp_uid");//실제 결제금액 조회위한 아임포트 서버쪽에서 id
        Long dataProductId = Long.parseLong(map.get("dataProductId")); //DB에서 물건 가격 조회를 위한 번호
        Integer amount = Integer.parseInt(map.get("amount"));//실제로 유저가 결제한 금액

        //아임포트 서버쪽에 결제된 정보 조회.
        return CustomResponseEntity.success(paymentService.verifyIamportService(impUid, amount, dataProductId, iamportClient));
    }
}
