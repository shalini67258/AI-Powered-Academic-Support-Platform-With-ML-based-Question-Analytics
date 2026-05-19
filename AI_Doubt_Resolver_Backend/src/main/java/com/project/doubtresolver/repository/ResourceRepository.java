package com.project.doubtresolver.repository;

import com.project.doubtresolver.model.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findBySubjectContainingIgnoreCase(String subject);
}