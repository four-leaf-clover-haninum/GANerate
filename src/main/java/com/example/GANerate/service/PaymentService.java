package com.example.GANerate.service;

import com.example.GANerate.config.timer.Timer;
import com.example.GANerate.domain.*;
import com.example.GANerate.enumuration.OrderStatus;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.DataProductRepository;
import com.example.GANerate.repository.OrderItemRepository;
import com.example.GANerate.repository.OrderRepository;
import com.example.GANerate.repository.PaymentRepository;
import com.example.GANerate.request.payment.PaymentRequest;
import com.example.GANerate.response.CustomResponseEntity;
import com.example.GANerate.response.payment.PaymentResponse;
import com.example.GANerate.service.dataProduct.DataProductService;
import com.example.GANerate.service.user.UserService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final DataProductRepository dataProductRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    /**
     * 현재 결제번호에 해당하는 정보를 갖고와서 반환해줌.
     */
//    @Transactional
//    public Payment paymentLookupService(Long paymentId) {
//        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_PAYMENT_INFO));
//        return payment;
//    }

    public IamportResponse<com.siot.IamportRestClient.response.Payment> paymentLookup(String impUid, IamportClient iamportClient) throws IamportResponseException, IOException{
        return iamportClient.paymentByImpUid(impUid);
    }

    @Transactional
    @Timer
    public PaymentResponse.CreatePayment verifyIamportService(String impUid, int amount, Long dataProductId, IamportClient iamportClient) throws IamportResponseException, IOException {
        IamportResponse<com.siot.IamportRestClient.response.Payment> irsp = paymentLookup(impUid, iamportClient);

        DataProduct dataProduct = dataProductRepository.findById(dataProductId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_PAYMENT_INFO));

        if (irsp.getResponse().getAmount().intValue() != amount)
            throw new CustomException(Result.UN_CORRECT_PRICE);

        if (amount != dataProduct.getPrice())
            throw new CustomException(Result.UN_CORRECT_PRICE);

        // 아임포트에서 서버쪽 결제내역과 DB의 결제 내역 금액이 같으면 DB에 결제 정보를 삽입.
        User user = userService.getCurrentUser();

        // 주문생성 및 연관관계 설정
        Order order = Order.builder()
                .orderStatus(OrderStatus.valueOf(OrderStatus.DONE.name())).build();
        order.setUser(user);
        orderRepository.save(order);

        // 결제 정보 생성 및 연관관계 설정
        Payment payment = Payment.builder()
                .payMethod(irsp.getResponse().getPayMethod())
                .impUid(irsp.getResponse().getImpUid())
                .merchantUid(irsp.getResponse().getMerchantUid())
                .amount(irsp.getResponse().getAmount().intValue())
                .buyerAddr(irsp.getResponse().getBuyerAddr())
                .buyerPostcode(irsp.getResponse().getBuyerPostcode())
                .user(user)
                .order(order)
                .status(PaymentStatus.PAID)
                .productTitle(dataProduct.getTitle())
                .createAt(LocalDateTime.now())
                .paidAt(LocalDateTime.now())
                .user(user)
                .build();
        paymentRepository.save(payment);


        // 주문상품 생성 및 연관관계 설정
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .dataProduct(dataProduct).build();
        orderItem.setOrder(order);
        orderItem.setDataProduct(dataProduct);
        orderItemRepository.save(orderItem);

        // 판매된 상품 가격의 일부를 판매자의 포인트로
        User seller = dataProduct.getUser();
        if (seller!=null){
            seller.addPoint((long) (dataProduct.getPrice()*0.01));
        }else {
        }

        return PaymentResponse.CreatePayment.builder()
                .paymentId(payment.getId())
                .merchantUid(irsp.getResponse().getMerchantUid())
                .productTitle(dataProduct.getTitle())
                .amount(irsp.getResponse().getAmount().intValue())
                .createAt(payment.getCreateAt())
                .userId(user.getId())
                .userName(user.getName())
                .orderId(order.getId())
                .productId(dataProductId)
                .build();
    }
    /**
     * 결제 취소할때 필요한 파라미터들을
     * CancelData에 셋업해주고 반환함.
     */
//    @Transactional
//    public CancelData cancelData(Map<String,String> map,
//                                 IamportResponse<Payment> lookUp,
//                                 PrincipalDetail principal, String code) throws RefundAmountIsDifferent {
//        //아임포트 서버에서 조회된 결제금액 != 환불(취소)될 금액 이면 예외발생
//        if(lookUp.getResponse().getAmount()!=new BigDecimal(map.get("checksum")))
//            throw new RefundAmountIsDifferent();
//
//        CancelData data = new CancelData(lookUp.getResponse().getImpUid(),true);
//        data.setReason(map.get("reason"));
//        data.setChecksum(new BigDecimal(map.get("checksum")));
//        data.setRefund_holder(map.get("refundHolder"));
//        data.setRefund_bank(code);
//        data.setRefund_account(principal.getBankName());
//        return data;
//    }
}
