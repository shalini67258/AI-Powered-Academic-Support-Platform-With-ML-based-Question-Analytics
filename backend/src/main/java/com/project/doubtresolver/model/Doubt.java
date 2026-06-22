package com.project.doubtresolver.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Doubt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String aiAnswer;

    @Column(columnDefinition = "TEXT")
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String resourceLink;

    @Column(name = "asked_count")
    private Integer askedCount = 1;   // ✅ Changed from int to Integer

    private int resourcePage;

    public Doubt() {}

    public Doubt(String question, String aiAnswer, String subject,
                 String resourceLink, int resourcePage) {
        this.question = question;
        this.aiAnswer = aiAnswer;
        this.subject = subject;
        this.resourceLink = resourceLink;
        this.resourcePage = resourcePage;
    }

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public String getQuestion() { 
        return question; 
    }

    public void setQuestion(String question) { 
        this.question = question; 
    }

    public String getAiAnswer() { 
        return aiAnswer; 
    }

    public void setAiAnswer(String aiAnswer) { 
        this.aiAnswer = aiAnswer; 
    }

    public String getSubject() { 
        return subject; 
    }

    public void setSubject(String subject) { 
        this.subject = subject; 
    }

    public String getResourceLink() { 
        return resourceLink; 
    }

    public void setResourceLink(String resourceLink) { 
        this.resourceLink = resourceLink; 
    }

    public int getResourcePage() { 
        return resourcePage; 
    }

    public void setResourcePage(int resourcePage) { 
        this.resourcePage = resourcePage; 
    }

    public Integer getAskedCount() {   // ✅ Updated getter
        return askedCount;
    }

    public void setAskedCount(Integer askedCount) {   // ✅ Updated setter
        this.askedCount = askedCount;
    }
}