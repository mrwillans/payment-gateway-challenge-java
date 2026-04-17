package com.checkout.payment.gateway.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PostPostPaymentRequestValidationTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  private PostPaymentRequest createValidRequest() {
    PostPaymentRequest request = new PostPaymentRequest();
    request.setCardNumber("123456789012345");
    request.setExpiryMonth(12);
    request.setExpiryYear(2030);
    request.setCurrency("USD");
    request.setAmount(1050);
    request.setCvv("123");
    return request;
  }

  @Test
  void testValidRequest() {
    PostPaymentRequest request = createValidRequest();
    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    assertTrue(violations.isEmpty(), "Should have no validation errors");
  }

  @Test
  void testCardNumberValidations() {
    PostPaymentRequest request = createValidRequest();

    request.setCardNumber("123");
    assertViolation(request, "Card number must be between 14-19 characters long");

    request.setCardNumber("123456789012345678901");
    assertViolation(request, "Card number must be between 14-19 characters long");

    request.setCardNumber("12345678901234A");
    assertViolation(request, "Card number must only contain numeric characters");
  }

  @Test
  void testExpiryMonthValidations() {
    PostPaymentRequest request = createValidRequest();

    request.setExpiryMonth(0);
    assertViolation(request, "Expiry month must be between 1-12");

    request.setExpiryMonth(13);
    assertViolation(request, "Expiry month must be between 1-12");
  }

  @Test
  void testExpiryDateFutureValidation() {
    PostPaymentRequest request = createValidRequest();

    request.setExpiryYear(2020);
    assertViolation(request, "The expiry date must be in the future");

    request.setExpiryYear(2026);
    request.setExpiryMonth(1);
    assertViolation(request, "The expiry date must be in the future");
  }

  @Test
  void testCurrencyValidations() {
    PostPaymentRequest request = createValidRequest();

    request.setCurrency("AUD");
    assertViolation(request, "Currency must be one of the 3 allowed ISO codes");

    request.setCurrency("US");
    assertViolation(request, "Currency must be 3 characters");
  }

  @Test
  void testAmountValidations() {
    PostPaymentRequest request = createValidRequest();

    request.setAmount(-50);
    assertViolation(request, "Amount must be greater than 0");
  }

  @Test
  void testCvvValidations() {
    PostPaymentRequest request = createValidRequest();

    request.setCvv("12");
    assertViolation(request, "CVV must be 3-4 characters long");

    request.setCvv("123A");
    assertViolation(request, "CVV must only contain numeric characters");
  }

  private void assertViolation(PostPaymentRequest request, String expectedMessage) {
    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(request);
    boolean found = violations.stream()
        .anyMatch(v -> v.getMessage().contains(expectedMessage));
    assertTrue(found, "Expected validation message not found: " + expectedMessage);
  }
}