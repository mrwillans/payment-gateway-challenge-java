package com.checkout.payment.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.YearMonth;

public class PostPaymentRequest implements Serializable {

  @NotBlank(message = "Card number is required")
  @Size(min = 14, max = 19, message = "Card number must be between 14-19 characters long")
  @Pattern(regexp = "^[0-9]+$", message = "Card number must only contain numeric characters")
  private String cardNumber;

  @NotNull(message = "Expiry month is required")
  @Min(value = 1, message = "Expiry month must be between 1-12")
  @Max(value = 12, message = "Expiry month must be between 1-12")
  @JsonProperty("expiry_month")
  private int expiryMonth;

  @NotNull(message = "Expiry year is required")
  @JsonProperty("expiry_year")
  private int expiryYear;

  @NotBlank(message = "Currency is required")
  @Size(min = 3, max = 3, message = "Currency must be 3 characters")
  @Pattern(regexp = "^(USD|EUR|GBP)$", message = "Currency must be one of the 3 allowed ISO codes (USD, EUR, GBP)")
  private String currency;

  @NotNull(message = "Amount is required")
  @Digits(integer = 12, fraction = 0, message = "Amount must be an integer representing minor units")
  @Positive(message = "Amount must be greater than 0")
  private int amount;

  @NotBlank(message = "CVV is required")
  @Size(min = 3, max = 4, message = "CVV must be 3-4 characters long")
  @Pattern(regexp = "^[0-9]+$", message = "CVV must only contain numeric characters")
  private String cvv;

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public int getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(int expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public int getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(int expiryYear) {
    this.expiryYear = expiryYear;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  public String getLastFourDigits(){
    return cardNumber.substring(cardNumber.length() -4);
  }

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%d/%d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumber=" + cardNumber +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }

  @AssertTrue(message = "The expiry date must be in the future")
  public boolean isExpiryDateInFuture() {
    try {
      YearMonth expiry = YearMonth.of(expiryYear, expiryMonth);
      return expiry.isAfter(YearMonth.now());
    } catch (Exception e) {
      return false;
    }
  }
}
