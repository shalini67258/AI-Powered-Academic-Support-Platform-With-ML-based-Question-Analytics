package com.project.doubtresolver.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.doubtresolver.model.Resource;
import com.project.doubtresolver.repository.ResourceRepository;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "*") // allow all origins for testing
public class ResourceController {

    @Autowired
    private ResourceRepository repository;

    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/upload")
    public Map<String, String> uploadResource(
            @RequestParam("subject") String subject,
            @RequestParam("topic") String topic,
            @RequestParam("file") MultipartFile file
    ) {
        System.out.println("[DEBUG] Upload called for file: " + file.getOriginalFilename());
        System.out.println("[DEBUG] Content Type: " + file.getContentType());
        try {
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = UPLOAD_DIR + fileName;

            // Debug: print paths
            System.out.println("[DEBUG] Saving file to: " + filePath);

            Files.write(Paths.get(filePath), file.getBytes());

            Resource resource = new Resource();
            resource.setSubject(subject);
            resource.setTopic(topic);
            resource.setFileName(fileName);
            resource.setFilePath("/api/resources/file/" + fileName);

            repository.save(resource);

            System.out.println("[DEBUG] Resource saved to DB: " + resource.getFileName());

            return Map.of(
                    "status", "success",
                    "fileName", fileName,
                    "message", "Resource uploaded successfully!"
            );

        } catch (IOException e) {
            e.printStackTrace();
            return Map.of(
                    "status", "error",
                    "message", "Failed to upload resource: " + e.getMessage()
            );
        }
    }

    @GetMapping
    public List<Resource> getAllResources() {
        return repository.findAll();
    }

    @GetMapping("/search")
    public List<Resource> searchBySubject(@RequestParam String subject) {
        return repository.findBySubjectContainingIgnoreCase(subject);
    }

    @GetMapping("/file/{fileName:.+}")
    public ResponseEntity<org.springframework.core.io.Resource> getFile(@PathVariable String fileName) {
        try {
            java.nio.file.Path path = Paths.get(UPLOAD_DIR).resolve(fileName);
            UrlResource resource = new UrlResource(path.toUri());

            System.out.println("[DEBUG] Serving file: " + fileName);

            if (!resource.exists() || !resource.isReadable()) {
                System.out.println("[DEBUG] File not found or unreadable: " + fileName);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .header(HttpHeaders.CONTENT_TYPE,"application/pdf")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public String deleteResource(@PathVariable Long id) {
        return repository.findById(id).map(res -> {
            File file = new File(UPLOAD_DIR + res.getFileName());
            if (file.exists()) {
                file.delete();
                System.out.println("[DEBUG] Deleted file: " + file.getName());
            }
            repository.delete(res);
            return "Resource deleted successfully!";
        }).orElse("Resource not found!");
    }
}