package com.example.demo.controllers;

import com.example.demo.common.ApiResponse;
import com.example.demo.dtos.evaluation.EvaluationRequest;
import com.example.demo.dtos.evaluation.EvaluationResponse;
import com.example.demo.services.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📋 EvaluationController
 * -----------------------------------------------------
 * CRUD completo con respuestas estandarizadas.
 */
@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService service;

    @PostMapping
    public ResponseEntity<ApiResponse<EvaluationResponse>> create(@Valid @RequestBody EvaluationRequest request) {
        EvaluationResponse created = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Evaluación creada correctamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluationResponse>> update(
            @PathVariable Long id, @Valid @RequestBody EvaluationRequest request) {
        EvaluationResponse updated = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Evaluación actualizada correctamente", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de evaluaciones", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EvaluationResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Evaluación encontrada", service.findById(id)));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<EvaluationResponse>>> findByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.ok("Evaluaciones del curso", service.findByCourseId(courseId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Evaluación eliminada correctamente", null));
    }
}
