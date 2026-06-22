package com.project.doubtresolver.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaAIService {

    private final String OLLAMA_URL = "http://localhost:11434/api/generate";

    // Existing synchronous method — keep as is
    public String askQuestion(String question) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String modelName = "phi:latest";  

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("prompt", question);
            requestBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(OLLAMA_URL, requestEntity, Map.class);

            if (response.getBody() == null) {
                return "Error: Empty response from Ollama.";
            }

            Object responseText = response.getBody().get("response");
            if (responseText == null) {
                return "Error: No 'response' field returned by Ollama.";
            }

            return responseText.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // 🔥 NEW: Async method — does not block controller
    @Async
    public CompletableFuture<String> askOllamaAsync(String question) {
        String answer = askQuestion(question); // calls the existing method
        return CompletableFuture.completedFuture(answer);
    }
}