package com.example.exam_portal.controller.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/chat")
public class ChatController {
    // @Autowired
    // private OpenAIService openAIService;

    // @PostMapping
    // public Mono<ChatResponse> chat(@RequestBody ChatRequest request) {
    //     System.out.println("Nhận câu hỏi: " + request.getMessage());
    //     return openAIService.ask(request.getMessage())
    //             .map(ChatResponse::new);
    // }

    // @GetMapping
    // public String test() {
    //     return "Vui lòng dùng phương thức POST để gửi câu hỏi!";
    // }
}
