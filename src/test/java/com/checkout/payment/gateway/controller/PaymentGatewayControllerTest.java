package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse payment = new PostPaymentResponse();
    payment.setId(UUID.randomUUID());
    payment.setAmount(10);
    payment.setCurrency("USD");
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2024);
    payment.setCardNumberLastFour("4321");

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

  @Test
  void whenPostPaymentShouldReturnPaymentsResponseSuccess() throws Exception {
    PostPaymentRequest postPaymentRequest = new PostPaymentRequest();
    postPaymentRequest.setAmount(100);
    postPaymentRequest.setCurrency("GBP");
    postPaymentRequest.setCvv("123");
    postPaymentRequest.setCardNumber("2222405343248877");
    postPaymentRequest.setExpiryMonth(12);
    postPaymentRequest.setExpiryYear(2027);

    mvc.perform(MockMvcRequestBuilders.post("/payment/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postPaymentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.AUTHORIZED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(postPaymentRequest.getLastFourDigits()))
        .andExpect(jsonPath("$.expiryMonth").value(postPaymentRequest.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(postPaymentRequest.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(postPaymentRequest.getCurrency()))
        .andExpect(jsonPath("$.amount").value(postPaymentRequest.getAmount()));
  }

  @Test
  void whenPostPaymentShouldReturnPaymentsResponseFailure() throws Exception {
    PostPaymentRequest postPaymentRequest = new PostPaymentRequest();
    postPaymentRequest.setAmount(100);
    postPaymentRequest.setCurrency("GBP");
    postPaymentRequest.setCvv("123");
    postPaymentRequest.setCardNumber("2222405343248872");
    postPaymentRequest.setExpiryMonth(12);
    postPaymentRequest.setExpiryYear(2027);

    mvc.perform(MockMvcRequestBuilders.post("/payment/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postPaymentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(PaymentStatus.DECLINED.getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(postPaymentRequest.getLastFourDigits()))
        .andExpect(jsonPath("$.expiryMonth").value(postPaymentRequest.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(postPaymentRequest.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(postPaymentRequest.getCurrency()))
        .andExpect(jsonPath("$.amount").value(postPaymentRequest.getAmount()));
  }

  @Test
  void whenPostPaymentShouldReturnPaymentsResponseError() throws Exception {
    PostPaymentRequest postPaymentRequest = new PostPaymentRequest();
    postPaymentRequest.setAmount(100);
    postPaymentRequest.setCurrency("GBP");
    postPaymentRequest.setCvv("123");
    postPaymentRequest.setCardNumber("2222405343248870");
    postPaymentRequest.setExpiryMonth(12);
    postPaymentRequest.setExpiryYear(2027);

    mvc.perform(MockMvcRequestBuilders.post("/payment/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postPaymentRequest)))
        .andExpect(status().isServiceUnavailable());

  }

  @Test
  void whenPostPaymentShouldReturnPaymentRejectedWhenBadRequest() throws Exception {
    PostPaymentRequest postPaymentRequest = new PostPaymentRequest();
    postPaymentRequest.setAmount(-1);
    postPaymentRequest.setCurrency("AUD");
    postPaymentRequest.setCvv("123");
    postPaymentRequest.setCardNumber("2222405343248870");
    postPaymentRequest.setExpiryMonth(12);
    postPaymentRequest.setExpiryYear(2027);

    mvc.perform(MockMvcRequestBuilders.post("/payment/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postPaymentRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(PaymentStatus.REJECTED.getName()));

  }
}
