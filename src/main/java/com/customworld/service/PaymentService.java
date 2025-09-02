package com.customworld.service;

import com.customworld.dto.request.PaymentRequest;
import com.customworld.dto.response.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${notchpay.api-key}")
    private  String apiKey;

    @Value("${notchpay.api-url}")
    private  String notchpayBaseUrl;

    //@Value("${notchpay.callback-url}")
    private  String notchpayCallBackUrl="https://606387c15363.ngrok-free.app/api/payments/notify";

    private final RestTemplate restTemplate;

    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private class Customer {
        public String name;
        public String email;
        public String phone;
    }

    public PaymentResponse initiatePayment(PaymentRequest paymentRequest) {
        log.info("Initiating payment with request: {}", paymentRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization",   apiKey);

        Customer customer = new Customer();
        customer.name = paymentRequest.getCustomerName() + " " + paymentRequest.getCustomerSurname();
        customer.email = paymentRequest.getCustomerEmail();
        customer.phone = paymentRequest.getCustomerPhoneNumber();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", paymentRequest.getAmount());
        requestBody.put("currency", "XAF"); // Ajout du currency requis
        requestBody.put("customer", customer); 
        requestBody.put("description", paymentRequest.getDescription());
        requestBody.put("callback", notchpayCallBackUrl);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            PaymentResponse response = restTemplate.postForObject(
                notchpayBaseUrl + "/payments", // Endpoint correct
                entity,
                PaymentResponse.class
            );

            if (response.getCode()==201) {
                log.info("Payment initiated successfully: {}", response);
            } else {
                log.warn("No response from Notch Pay");
            }
            return response;
        } catch (HttpClientErrorException e) {
            log.error("Error initiating payment: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            PaymentResponse response = new PaymentResponse();
            response.setStatus(e.getStatusText());
            response.setMessage("Error initiating payment: " + e.getResponseBodyAsString());
            response.setCode(e.getStatusCode().value());
            return response;
        } catch (Exception e) {
            log.error("Error initiating payment: {}", e.getMessage(), e);
            return new PaymentResponse();
        }
    }
}
