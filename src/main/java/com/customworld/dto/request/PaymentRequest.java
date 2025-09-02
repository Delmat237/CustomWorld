package com.customworld.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import com.customworld.enums.ChannelOption;
@Data
public class PaymentRequest {
    @NotNull
    private Integer amount;
    private String description;
    private String customerName;
    private String customerSurname;
    private String customerPhoneNumber;
    private String customerEmail;
    private String channelOption;
    private String customerAddress;
    private String customerCity;
    private String customerZipCode;
}