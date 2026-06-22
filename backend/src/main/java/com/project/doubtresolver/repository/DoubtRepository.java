package com.project.doubtresolver.repository;

import com.project.doubtresolver.model.Doubt;
import com.project.doubtresolver.dto.AnalyticsDTO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoubtRepository extends JpaRepository<Doubt, Long> {

    // Find exact same question
    Doubt findByQuestionIgnoreCaseAndSubjectIgnoreCase(String question, String subject);

    // Analytics query
    @Query("SELECT new com.project.doubtresolver.dto.AnalyticsDTO(d.subject, d.question, COALESCE(SUM(d.askedCount),0)) " +
           "FROM Doubt d " +
           "GROUP BY d.subject, d.question " +
           "ORDER BY d.subject ASC, SUM(d.askedCount) DESC")
    List<AnalyticsDTO> getAnalytics();
}