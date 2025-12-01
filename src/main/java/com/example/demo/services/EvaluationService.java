package com.example.demo.services;

import com.example.demo.dtos.evaluation.EvaluationRequest;
import com.example.demo.dtos.evaluation.EvaluationResponse;

import java.util.List;

/**
 * 📋 EvaluationService
 * -----------------------------------------------------
 * Define las operaciones de negocio sobre la entidad Evaluation.
 */
public interface EvaluationService {
    EvaluationResponse create(EvaluationRequest request);
    EvaluationResponse update(Long id, EvaluationRequest request);
    void delete(Long id);
    EvaluationResponse findById(Long id);
    List<EvaluationResponse> findAll();
    List<EvaluationResponse> findByCourseId(Long courseId);
}
