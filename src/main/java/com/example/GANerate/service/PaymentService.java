package com.example.GANerate.service;

import com.example.GANerate.domain.*;
import com.example.GANerate.enumuration.OrderStatus;
import com.example.GANerate.enumuration.Result;
import com.example.GANerate.exception.CustomException;
import com.example.GANerate.repository.DataProductRepository;
import com.example.GANerate.repository.OrderItemRepository;
import com.example.GANerate.repository.OrderRepository;
import com.example.GANerate.repository.PaymentRepository;
import com.example.GANerate.request.payment.PaymentRequest;
import com.example.GANerate.response.payment.PaymentResponse;
import com.example.GANerate.service.dataProduct.DataProductService;
import com.example.GANerate.service.user.UserService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import lombok.RequiredArgsConstructor;
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
public class PaymentService {

    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final DataProductRepository dataProductRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    /**
     * 은행이름에 따른 코드들을 반환해줌<br>
     * KG이니시스 기준.
     * @param bankName
     * @return
     */
    public String code(String bankName) {
        String code="";
        if(bankName.equals("우리은행")||bankName.equals("우리")) code="20";
        else if(bankName.equals("국민은행")||bankName.equals("국민")) code="04";
        return code;
    }

    /**
     * 현재 결제번호에 해당하는 정보를 갖고와서 반환해줌.
     */
    @Transactional
    public Payment paymentLookupService(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_PAYMENT_INFO));
        return payment;
    }

    /**
     * 아임포트 서버쪽 결제내역과 DB에 물건가격을 비교하는 서비스. <br>
     * 다름 -> 예외 발생시키고 GlobalExceptionHandler쪽에서 예외처리 <br>
     * 같음 -> 결제정보를 DB에 저장(PaymentsInfo 테이블)
     * @param irsp (아임포트쪽 결제 내역 조회 정보)
     */
    @Transactional
    public void verifyIamportService(IamportResponse<com.siot.IamportRestClient.response.Payment> irsp, int amount, Long dataProductId) {
        DataProduct dataProduct = dataProductRepository.findById(dataProductId).orElseThrow(() -> new CustomException(Result.NOT_FOUND_PAYMENT_INFO));

        //실제로 결제된 금액과 아임포트 서버쪽 결제내역 금액과 같은지 확인
        //이때 가격은 BigDecimal이란 데이터 타입으로 주로 금융쪽에서 정확한 값표현을 위해씀.
        //int형으로 비교해주기 위해 형변환 필요.
        if(irsp.getResponse().getAmount().intValue()!=amount)
            throw new CustomException(Result.UN_CORRECT_PRICE);

        //DB에서 물건가격과 실제 결제금액이 일치하는지 확인하기. 만약 다르면 예외 발생시키기.
        if(amount!=dataProduct.getPrice()) // 타입 안맞으수도 있음
            throw new CustomException(Result.UN_CORRECT_PRICE);

        //아임포트에서 서버쪽 결제내역과 DB의 결제 내역 금액이 같으면 DB에 결제 정보를 삽입.
        User user = userService.getCurrentUser();
        List<Order> orders = user.getOrders();

        Order order = Order.builder()
                .orderStatus(OrderStatus.DONE).build();
        orderRepository.save(order);
        order.setUser(user);

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .dataProduct(dataProduct).build();
        orderItemRepository.save(orderItem);
        orderItem.setOrder(order);
        orderItem.setDataProduct(dataProduct);

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
                .build();

        paymentRepository.save(payment);
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
