package com.example.exam_portal.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.exam_portal.util.ResponseParser;


@Component
public class GeminiClient {
    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";

    public String ask(String prompt) {
        String json = buildRequest(prompt);
        String response = sendRequest(json);
        return ResponseParser.extractText(response);
    }

    public String askRaw(String prompt) {
        String json = buildRequest(prompt);
        return sendRequest(json);
    }

    private String buildRequest(String prompt) {
        return """
        {
          "contents": [
            {
              "parts": [
                { "text": "%s" }
              ]
            }
          ]
        }
        """.formatted(prompt);
    }

    private String sendRequest(String json) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}
