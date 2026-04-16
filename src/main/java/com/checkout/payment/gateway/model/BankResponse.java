package com.checkout.payment.gateway.model;

import java.util.UUID;

public record BankResponse(boolean authorized, UUID authorizationCode) {

}
