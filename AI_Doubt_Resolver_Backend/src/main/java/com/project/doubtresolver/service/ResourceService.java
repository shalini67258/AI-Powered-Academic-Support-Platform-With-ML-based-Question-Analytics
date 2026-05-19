package com.project.doubtresolver.service;

import com.project.doubtresolver.model.Resource;
import com.project.doubtresolver.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ResourceService {

    private static final String UPLOAD_DIR = "uploads/";

    @Autowired
    private ResourceRepository resourceRepository;

    // Upload and save resource
    public Resource uploadResource(String subject, String topic, MultipartFile file) throws IOException {

        // Create uploads folder if not exists
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Create unique file name
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        // Save file
        Files.write(filePath, file.getBytes());

        // Save DB record
        Resource resource = new Resource();
        resource.setSubject(subject);
        resource.setTopic(topic);
        resource.setFileName(fileName);
        resource.setFilePath(filePath.toString());

        return resourceRepository.save(resource);
    }

    // Get all resources
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    // Search by subject
    public List<Resource> searchBySubject(String subject) {
        return resourceRepository.findBySubjectContainingIgnoreCase(subject);
    }
}