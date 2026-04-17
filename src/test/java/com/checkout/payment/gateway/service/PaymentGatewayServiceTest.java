package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.client.BankClient;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.*;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

  @Mock
  private PaymentsRepository paymentsRepository;

  @Mock
  private BankClient bankClient;

  @InjectMocks
  private PaymentGatewayService paymentService;

  private PostPaymentRequest validRequest;

  @BeforeEach
  void setUp() {
    validRequest = new PostPaymentRequest();
    validRequest.setCardNumber("1234567890123456");
    validRequest.setCurrency("USD");
    validRequest.setAmount(1000);
    validRequest.setCvv("123");
    validRequest.setExpiryMonth(12);
    validRequest.setExpiryYear(2030);
  }

  @Test
  void getPaymentById() {
    UUID id = UUID.randomUUID();
    PostPaymentResponse mockResponse = new PostPaymentResponse();
    mockResponse.setId(id);

    when(paymentsRepository.get(id)).thenReturn(Optional.of(mockResponse));

    PostPaymentResponse result = paymentService.getPaymentById(id);

    assertNotNull(result);
    assertEquals(id, result.getId());
    verify(paymentsRepository).get(id);
  }

  @Test
  void getPaymentById_NotFound() {
    UUID id = UUID.randomUUID();
    when(paymentsRepository.get(id)).thenReturn(Optional.empty());

    assertThrows(EventProcessingException.class, () -> paymentService.getPaymentById(id));
  }

  @Test
  void processPayment_Success() {
    BankResponse bankResponse = mock(BankResponse.class);
    when(bankResponse.authorized()).thenReturn(true);
    when(bankClient.processPayment(any(BankRequest.class))).thenReturn(bankResponse);

    PostPaymentResponse result = paymentService.processPayment(validRequest);

    assertNotNull(result.getId());
    assertEquals(PaymentStatus.AUTHORIZED, result.getStatus());
    assertEquals("3456", result.getCardNumberLastFour());

    verify(bankClient).processPayment(any(BankRequest.class));
    verify(paymentsRepository, times(1)).add(any(PostPaymentResponse.class));
  }

  @Test
  void processPayment_Declined() {
    BankResponse bankResponse = mock(BankResponse.class);
    when(bankResponse.authorized()).thenReturn(false);
    when(bankClient.processPayment(any(BankRequest.class))).thenReturn(bankResponse);

    PostPaymentResponse result = paymentService.processPayment(validRequest);

    assertEquals(PaymentStatus.DECLINED, result.getStatus());
    verify(paymentsRepository).add(any(PostPaymentResponse.class));
    verify(bankClient).processPayment(any(BankRequest.class));
  }
}