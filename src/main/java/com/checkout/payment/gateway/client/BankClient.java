package com.checkout.payment.gateway.client;

import com.checkout.payment.gateway.model.BankRequest;
import com.checkout.payment.gateway.model.BankResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class BankClient {

  private static final String BANK_URL = "http://localhost:8080/payments";

  private final RestTemplate restTemplate;

  public BankClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public BankResponse processPayment(BankRequest bankRequest){
    return restTemplate.exchange(
            BANK_URL,
            HttpMethod.POST,
            new HttpEntity<>(bankRequest),
            BankResponse.class)
        .getBody();
  }
}
