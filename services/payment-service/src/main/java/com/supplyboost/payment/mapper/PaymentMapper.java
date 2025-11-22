package com.supplyboost.payment.mapper;

import com.supplyboost.payment.dto.PaymentResponse;
import com.supplyboost.payment.model.Payment;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

  PaymentResponse toPaymentResponse(Payment payment);

  List<PaymentResponse> toPaymentResponses(List<Payment> payments);
}
