package com.example.exam_portal.service;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.exam_portal.domain.PaymentRequest;



@Service
public class PaymentService {
    @Value("${momo.accessKey}")
    private String accessKey;

    @Value("${momo.secretKey}")
    private String secretKey;

    @Value("${momo.partnerCode}")
    private String partnerCode;

    @Value("${momo.redirectUrl}")
    private String redirectUrl;

    @Value("${momo.ipnUrl}")
    private String ipnUrl;

    @Value("${momo.requestType}")
    private String requestType;

    public String createPayment(PaymentRequest paymentRequest) throws Exception {
        // Create the raw signature
        String rawSignature = "accessKey=" + accessKey +
        "&amount=" + paymentRequest.getAmount() +
        "&extraData=" + paymentRequest.getExtraData() +
        "&ipnUrl=" + ipnUrl +
        "&orderId=" + paymentRequest.getOrderId() +
        "&orderInfo=" + paymentRequest.getOrderInfo() +
        "&partnerCode=" + partnerCode +
        "&redirectUrl=" + redirectUrl +
        "&requestId=" + paymentRequest.getRequestId() +
        "&requestType=" + requestType;

        System.out.println("Raw Signature: " + rawSignature);

        // Generate signature using HMAC SHA256
        String signature = hmacSHA256(secretKey, rawSignature);

        System.out.println("Generated Signature: " + signature);
        // Prepare JSON request body
        String requestBody = String.format(
                "{\"partnerCode\":\"%s\",\"partnerName\":\"Test\",\"storeId\":\"MomoTestStore\",\"requestId\":\"%s\",\"amount\":\"%s\",\"orderId\":\"%s\",\"orderInfo\":\"%s\",\"redirectUrl\":\"%s\",\"ipnUrl\":\"%s\",\"lang\":\"vi\",\"requestType\":\"%s\",\"autoCapture\":true,\"extraData\":\"%s\",\"orderGroupId\":\"\",\"signature\":\"%s\"}",
                partnerCode, paymentRequest.getRequestId(), paymentRequest.getAmount(), paymentRequest.getOrderId(),
                paymentRequest.getOrderInfo(), redirectUrl, ipnUrl, requestType, paymentRequest.getExtraData(),
                signature);

        // Call MoMo API
        return sendPaymentRequest(requestBody);
    }

    public String queryTransactionStatus(String orderId, String requestId) throws Exception {
        // Tạo raw signature
        String rawSignature = "accessKey=" + accessKey +
                "&orderId=" + orderId +
                "&partnerCode=" + partnerCode +
                "&requestId=" + requestId;

        // Tạo chữ ký HMAC SHA256
        String signature = hmacSHA256(secretKey, rawSignature);

        // Tạo request body
        String requestBody = String.format(
                "{\"partnerCode\":\"%s\",\"requestId\":\"%s\",\"orderId\":\"%s\",\"lang\":\"vi\",\"signature\":\"%s\"}",
                partnerCode, requestId, orderId, signature);

        // Gửi request tới MoMo
        String url = "https://test-payment.momo.vn/v2/gateway/api/query";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // Trả về phản hồi dưới dạng JSON
        return response.getBody();
    }

    private String sendPaymentRequest(String requestBody) {
        String url = "https://test-payment.momo.vn/v2/gateway/api/create";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }

    private String hmacSHA256(String secretKey, String data) throws Exception {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);

            byte[] bytes = sha256_HMAC.doFinal(data.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : bytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | java.security.InvalidKeyException e) {// javax.crypto.NoSuchPaddingException
            throw new Exception("Error generating HMAC signature", e);
        }
    }
}
