package com.example.demo.services;

import com.example.demo.dtos.grade.GradeRequest;
import com.example.demo.dtos.grade.GradeResponse;

import java.util.List;

/**
 * 📊 GradeService
 * -----------------------------------------------------
 * Define las operaciones de negocio sobre la entidad Grade.
 */
public interface GradeService {
    GradeResponse create(GradeRequest request);
    GradeResponse update(Long id, GradeRequest request);
    void delete(Long id);
    GradeResponse findById(Long id);
    List<GradeResponse> findAll();
    List<GradeResponse> findByStudentId(Long studentId);
}
