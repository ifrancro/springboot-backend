package com.example.demo.controllers;

import com.example.demo.common.ApiResponse;
import com.example.demo.dtos.course.CourseRequest;
import com.example.demo.dtos.course.CourseResponse;
import com.example.demo.services.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📚 CourseController
 * -----------------------------------------------------
 * CRUD completo con respuestas estandarizadas.
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService service;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> create(@Valid @RequestBody CourseRequest request) {
        CourseResponse created = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Curso creado correctamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> update(
            @PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        CourseResponse updated = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Curso actualizado correctamente", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de cursos", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Curso encontrado", service.findById(id)));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> findByTeacherId(@PathVariable Long teacherId) {
        return ResponseEntity.ok(ApiResponse.ok("Cursos del docente", service.findByTeacherId(teacherId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Curso eliminado correctamente", null));
    }
}
