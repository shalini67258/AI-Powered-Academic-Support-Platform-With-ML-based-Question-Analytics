package com.project.doubtresolver.model;

import jakarta.persistence.*;

@Entity
@Table(name="analyze_topic")
public class AnalyzeTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String topic;
    private int countDoubts;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public int getCountDoubts() { return countDoubts; }
    public void setCountDoubts(int countDoubts) { this.countDoubts = countDoubts; }
}