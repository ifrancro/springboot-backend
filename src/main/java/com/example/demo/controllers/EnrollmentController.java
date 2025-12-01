package com.example.demo.controllers;

import com.example.demo.common.ApiResponse;
import com.example.demo.dtos.enrollment.EnrollmentRequest;
import com.example.demo.dtos.enrollment.EnrollmentResponse;
import com.example.demo.services.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 📝 EnrollmentController
 * -----------------------------------------------------
 * Gestiona relaciones entre estudiantes y cursos (Many-To-Many)
 * con respuestas estandarizadas y validación de negocio.
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getAllEnrollments() {
        List<EnrollmentResponse> enrollments = service.findAllEnrollments();
        return ResponseEntity.ok(ApiResponse.ok("Todas las inscripciones", enrollments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> findById(@PathVariable Long id) {
        EnrollmentResponse response = service.findById(id);
        return ResponseEntity.ok(ApiResponse.ok("Inscripción encontrada", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentResponse>> create(
            @Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = service.enrollStudent(request);
        return ResponseEntity.ok(ApiResponse.ok("Estudiante inscrito correctamente", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> update(
            @PathVariable Long id, @Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse response = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Inscripción actualizada correctamente", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Inscripción eliminada correctamente", null));
    }

    /**
     * ✅ Obtiene todas las inscripciones de un estudiante
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> findEnrollmentsByStudent(
            @PathVariable Long studentId) {

        List<EnrollmentResponse> list = service.findEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.ok("Inscripciones del estudiante", list));
    }

    /**
     * ✅ Obtiene todas las inscripciones de un curso
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> findEnrollmentsByCourse(
            @PathVariable Long courseId) {

        List<EnrollmentResponse> list = service.findEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.ok("Inscripciones del curso", list));
    }
}
