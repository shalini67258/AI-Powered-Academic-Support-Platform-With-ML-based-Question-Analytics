package com.project.doubtresolver.controller;

import com.project.doubtresolver.dto.QuestionDTO;
import com.project.doubtresolver.dto.AnalyticsDTO;
import com.project.doubtresolver.model.Doubt;
import com.project.doubtresolver.repository.DoubtRepository;
import com.project.doubtresolver.service.OllamaAIService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doubts")
@CrossOrigin(origins = "*")
public class DoubtController {

    private final OllamaAIService ollamaAIService;
    private final DoubtRepository doubtRepository;

    public DoubtController(OllamaAIService ollamaAIService,
                           DoubtRepository doubtRepository) {
        this.ollamaAIService = ollamaAIService;
        this.doubtRepository = doubtRepository;
    }

    // ML Similarity check
    private boolean isSimilarQuestion(String question, String existingQuestion) {

        try {

            java.net.URL url = new java.net.URL("http://127.0.0.1:5000/check");
            java.net.HttpURLConnection conn =
                    (java.net.HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInput =
                    "{\"question1\":\"" + question + "\",\"question2\":\"" + existingQuestion + "\"}";

            try (java.io.OutputStream os = conn.getOutputStream()) {

                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);

            }

            java.io.BufferedReader br =
                    new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getInputStream(), "utf-8"));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {

                response.append(line.trim());

            }

            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();

            Map<String, Object> result =
                    mapper.readValue(response.toString(), Map.class);

            return (Boolean) result.get("similar");

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }
    }

    // ASK QUESTION API
    @PostMapping("/ask")
    public Doubt askQuestion(@RequestBody QuestionDTO questionDTO) {

        String question = questionDTO.getQuestion().trim().toLowerCase();
        String subject = questionDTO.getSubject().trim().toLowerCase();

        if (question.isEmpty()) {
            throw new RuntimeException("Question cannot be empty");
        }

        // EXACT MATCH CHECK
        Doubt exact =
                doubtRepository.findByQuestionIgnoreCaseAndSubjectIgnoreCase(
                        question, subject);

        if (exact != null) {

            exact.setAskedCount(
                    exact.getAskedCount() == null ? 1 : exact.getAskedCount() + 1
            );

            System.out.println("Exact question found → count updated");

            return doubtRepository.save(exact);

        }

        // ML SIMILARITY CHECK
        Iterable<Doubt> doubts = doubtRepository.findAll();

        for (Doubt d : doubts) {

    if (d.getSubject().equalsIgnoreCase(subject)) {

        String existingQuestion = d.getQuestion().toLowerCase();

        // 1️⃣ Keyword check first
        if (existingQuestion.contains(question) || question.contains(existingQuestion)) {

            d.setAskedCount(
                    d.getAskedCount() == null ? 1 : d.getAskedCount() + 1
            );

            System.out.println("Keyword Similar Question Found → count updated");

            return doubtRepository.save(d);
        }

        // 2️⃣ ML similarity check
        boolean similar = isSimilarQuestion(question, existingQuestion);

        if (similar) {

            d.setAskedCount(
                    d.getAskedCount() == null ? 1 : d.getAskedCount() + 1
            );

            System.out.println("ML Similar Question Found → count updated");

            return doubtRepository.save(d);
        }

    }

}

        // NEW QUESTION → CALL AI
        String aiAnswer = ollamaAIService.askQuestion(question);

        Doubt doubt = new Doubt();

        doubt.setQuestion(question);
        doubt.setSubject(subject);
        doubt.setAiAnswer(aiAnswer);
        doubt.setAskedCount(1);

        System.out.println("New question → Ollama called");

        return doubtRepository.save(doubt);
    }

    // GET ALL DOUBTS
    @GetMapping("/all")
    public Iterable<Doubt> getAllDoubts() {

        return doubtRepository.findAll();

    }

    // ANALYTICS API
    @GetMapping("/analytics")
    public List<AnalyticsDTO> getAnalytics() {

        return doubtRepository.getAnalytics();

    }

}