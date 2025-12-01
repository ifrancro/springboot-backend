package com.example.demo.services;

import com.example.demo.dtos.course.CourseRequest;
import com.example.demo.dtos.course.CourseResponse;

import java.util.List;

/**
 * 📚 CourseService
 * -----------------------------------------------------
 * Define las operaciones de negocio sobre la entidad Course.
 */
public interface CourseService {
    CourseResponse create(CourseRequest request);
    CourseResponse update(Long id, CourseRequest request);
    void delete(Long id);
    CourseResponse findById(Long id);
    List<CourseResponse> findAll();
    List<CourseResponse> findByTeacherId(Long teacherId);
}
