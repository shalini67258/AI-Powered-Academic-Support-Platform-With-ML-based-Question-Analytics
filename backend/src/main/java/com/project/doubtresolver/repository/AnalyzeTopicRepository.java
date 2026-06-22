package com.project.doubtresolver.repository;

import com.project.doubtresolver.model.AnalyzeTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalyzeTopicRepository extends JpaRepository<AnalyzeTopic, Long> {
    Optional<AnalyzeTopic> findBySubjectAndTopic(String subject, String topic);
}