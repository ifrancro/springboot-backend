package com.example.demo.controllers;

import com.example.demo.common.ApiResponse;
import com.example.demo.dtos.teacher.TeacherRequest;
import com.example.demo.dtos.teacher.TeacherResponse;
import com.example.demo.services.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 👨‍🏫 TeacherController
 * -----------------------------------------------------
 * CRUD completo con respuestas estandarizadas.
 */
@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService service;

    @PostMapping
    public ResponseEntity<ApiResponse<TeacherResponse>> create(@Valid @RequestBody TeacherRequest request) {
        TeacherResponse created = service.create(request);
        return ResponseEntity.ok(ApiResponse.ok("Docente creado correctamente", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponse>> update(
            @PathVariable Long id, @Valid @RequestBody TeacherRequest request) {
        TeacherResponse updated = service.update(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Docente actualizado correctamente", updated));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Lista de docentes", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TeacherResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Docente encontrado", service.findById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Docente eliminado correctamente", null));
    }
}
