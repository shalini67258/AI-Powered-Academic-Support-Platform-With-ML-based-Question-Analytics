package com.project.doubtresolver.service;

import com.project.doubtresolver.model.Doubt;
import com.project.doubtresolver.repository.DoubtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoubtService {

    @Autowired
    private DoubtRepository doubtRepository;

    @Autowired
    private OllamaAIService ollamaAIService;

    // Check similarity with Python ML microservice
    private boolean isSimilarQuestion(String question) {
        RestTemplate restTemplate = new RestTemplate();
        String mlUrl = "http://localhost:5000/check-similarity";

        Map<String, String> request = new HashMap<>();
        request.put("question", question);

        Map response = restTemplate.postForObject(mlUrl, request, Map.class);
        return Boolean.TRUE.equals(response.get("similar"));
    }

    // Generate AI answer and save in DB
    public Doubt generateAnswer(String question, String subject) {

        // Step 1: Check similarity with ML microservice
        if (isSimilarQuestion(question)) {

            // Step 2: Find existing doubt by question + subject
            Doubt existing = doubtRepository
                    .findByQuestionIgnoreCaseAndSubjectIgnoreCase(question, subject);

            if (existing != null) {
                // Step 3: Increment asked_count and save
                existing.setAskedCount(
                    existing.getAskedCount() == null ? 1 : existing.getAskedCount() + 1
                );
                return doubtRepository.save(existing);
            }
        }

        // Step 4: New question — generate AI answer and save
        String aiAnswer = ollamaAIService.askQuestion(question);
        Doubt doubt = new Doubt();
        doubt.setQuestion(question);
        doubt.setAiAnswer(aiAnswer);
        doubt.setSubject(subject);
        doubt.setAskedCount(1);

        return doubtRepository.save(doubt);
    }

    // Get all doubts
    public List<Doubt> getAllDoubts() {
        return doubtRepository.findAll();
    }
}