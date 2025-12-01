package com.example.demo.controllers;

import com.example.demo.common.ApiResponse;
import com.example.demo.dtos.grade.GradeRequest;
import com.example.demo.dtos.grade.GradeResponse;
import com.example.demo.services.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📊 GradeController
 * -----------------------------------------------------
 * CRUD completo con respuestas estandarizadas.
 */
@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService service;

    @PostMapping
    public ResponseEntity<ApiResponse<GradeResponse>> create(@Valid @RequestBody GradeRequest request) {
        GradeResponse created = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Nota creada correctamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GradeResponse>> update(
            @PathVariable Long id, @Valid @RequestBody GradeRequest request) {
        GradeResponse updated = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Nota actualizada correctamente", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GradeResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de notas", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GradeResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Nota encontrada", service.findById(id)));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> findByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok("Notas del estudiante", service.findByStudentId(studentId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Nota eliminada correctamente", null));
    }
}
