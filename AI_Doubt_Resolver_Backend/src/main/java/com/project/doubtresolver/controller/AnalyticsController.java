package com.project.doubtresolver.controller;

import com.project.doubtresolver.repository.DoubtRepository;
import com.project.doubtresolver.repository.AnalyzeTopicRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private DoubtRepository doubtRepository;

    @Autowired
    private AnalyzeTopicRepository analyzeTopicRepository;

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAnalytics() {

        // delete analytics summary
        analyzeTopicRepository.deleteAll();

        // delete stored doubts
        doubtRepository.deleteAll();

        return ResponseEntity.ok("Analytics cleared successfully");
    }
}