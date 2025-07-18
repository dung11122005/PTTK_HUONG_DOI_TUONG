package com.example.exam_portal.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exam_portal.domain.dto.ChatRequest;
import com.example.exam_portal.domain.dto.ChatResponse;
import com.example.exam_portal.service.OpenAIService;

import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private OpenAIService openAIService;

    @PostMapping
    public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
        System.out.println("Nhận câu hỏi: " + request.getMessage());
        return openAIService.ask(request.getMessage())
                .map(ChatResponse::new);
    }

    @GetMapping
    public String test() {
        return "Vui lòng dùng phương thức POST để gửi câu hỏi!";
    }
}
