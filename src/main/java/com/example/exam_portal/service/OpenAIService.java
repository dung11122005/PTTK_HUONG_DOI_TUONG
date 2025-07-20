package com.example.exam_portal.service;

import org.springframework.stereotype.Service;


@Service
public class OpenAIService {
    // @Value("${openai.api.key}")
    // private String openaiApiKey;

    // private final WebClient webClient;

    // public OpenAIService() {
    //     this.webClient = WebClient.builder()
    //             .baseUrl("https://api.openai.com/v1")
    //             .defaultHeader("Authorization", "Bearer " + openaiApiKey)
    //             .build();
    // }

    // public Mono<String> ask(String userMessage) {
    //     Map<String, Object> requestBody = new HashMap<>();
    //     requestBody.put("model", "gpt-3.5-turbo");
    //     requestBody.put("messages", List.of(
    //             Map.of("role", "user", "content", userMessage)
    //     ));

    //     return webClient.post()
    //             .uri("/chat/completions")
    //             .header("Content-Type", "application/json")
    //             .bodyValue(requestBody)
    //             .retrieve()
    //             .bodyToMono(Map.class)
    //             .map(response -> {
    //                 List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
    //                 if (choices != null && !choices.isEmpty()) {
    //                     Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
    //                     return (String) message.get("content");
    //                 }
    //                 return "Xin lỗi, tôi không thể trả lời lúc này.";
    //             });
    // }
}
