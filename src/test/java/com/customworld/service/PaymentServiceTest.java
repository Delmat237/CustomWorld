package com.customworld.service;

import com.customworld.dto.request.PaymentRequest;
import com.customworld.dto.response.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentServiceTest {

    @Test
    void initiatePaymentUsesWebClientAndMapsSuccessResponse() {
        AtomicReference<URI> requestedUri = new AtomicReference<>();
        AtomicReference<String> authorizationHeader = new AtomicReference<>();
        ExchangeFunction exchangeFunction = request -> {
            requestedUri.set(request.url());
            authorizationHeader.set(request.headers().getFirst(HttpHeaders.AUTHORIZATION));
            String body = """
                    {"status":"success","message":"created","code":201,"authorization_url":"https://pay.example/checkout"}
                    """;
            return Mono.just(ClientResponse.create(HttpStatus.CREATED)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body(body)
                    .build());
        };
        PaymentService service = paymentService(exchangeFunction);

        PaymentResponse response = service.initiatePayment(paymentRequest());

        assertThat(requestedUri).hasValue(URI.create("https://notchpay.test/payments"));
        assertThat(authorizationHeader).hasValue("Bearer test-api-key");
        assertThat(response.getCode()).isEqualTo(201);
        assertThat(response.getAuthorization_url()).isEqualTo("https://pay.example/checkout");
    }

    @Test
    void initiatePaymentMapsClientErrorResponse() {
        ExchangeFunction exchangeFunction = request -> Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body("{\"message\":\"bad request\"}")
                .build());
        PaymentService service = paymentService(exchangeFunction);

        PaymentResponse response = service.initiatePayment(paymentRequest());

        assertThat(response.getCode()).isEqualTo(400);
        assertThat(response.getStatus()).isEqualTo("Bad Request");
        assertThat(response.getMessage()).contains("bad request");
    }

    private static PaymentService paymentService(ExchangeFunction exchangeFunction) {
        PaymentService service = new PaymentService(WebClient.builder().exchangeFunction(exchangeFunction));
        ReflectionTestUtils.setField(service, "apiKey", "Bearer test-api-key");
        ReflectionTestUtils.setField(service, "notchpayBaseUrl", "https://notchpay.test");
        ReflectionTestUtils.setField(service, "notchpayCallBackUrl", "https://app.test/api/payments/notify");
        return service;
    }

    private static PaymentRequest paymentRequest() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(10_000);
        request.setDescription("Order #1");
        request.setCustomerName("Jane");
        request.setCustomerSurname("Doe");
        request.setCustomerEmail("jane@example.com");
        request.setCustomerPhoneNumber("+237600000000");
        request.setReference("ORDER-1");
        return request;
    }
}
