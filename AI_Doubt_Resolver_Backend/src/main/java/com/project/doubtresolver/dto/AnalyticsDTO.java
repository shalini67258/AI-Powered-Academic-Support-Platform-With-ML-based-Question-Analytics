package com.project.doubtresolver.dto;

public class AnalyticsDTO {

    private String subject;
    private String topic;
    private Long totalQuestions;

    public AnalyticsDTO(String subject, String topic, Long totalQuestions) {
        this.subject = subject;
        this.topic = topic;
        this.totalQuestions = totalQuestions;
    }

    public String getSubject() {
        return subject;
    }

    public String getTopic() {
        return topic;
    }

    public Long getTotalQuestions() {
        return totalQuestions;
    }
}