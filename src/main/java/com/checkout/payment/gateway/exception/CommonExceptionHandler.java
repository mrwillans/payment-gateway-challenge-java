package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.ErrorResponse;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleException(EventProcessingException ex) {
    LOG.error("Exception happened", ex);
    return new ResponseEntity<>(new ErrorResponse("Page not found"),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(HttpServerErrorException.ServiceUnavailable.class)
  public ResponseEntity<Object> handleExternalServiceUnavailable(
      HttpServerErrorException.ServiceUnavailable ex) {
    LOG.error("Exception happened", ex);
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<PostPaymentResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    LOG.error("Exception happened", ex);
    PostPaymentResponse postPaymentResponse = new PostPaymentResponse();
    postPaymentResponse.setStatus(PaymentStatus.REJECTED);
    return new ResponseEntity<>(postPaymentResponse, HttpStatus.BAD_REQUEST);
  }
}
