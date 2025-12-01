package com.example.demo.services;

import com.example.demo.dtos.enrollment.EnrollmentRequest;
import com.example.demo.dtos.enrollment.EnrollmentResponse;

import java.util.List;

/**
 * 📝 EnrollmentService
 * -----------------------------------------------------
 * Define las operaciones de negocio sobre la relación Many-To-Many
 * entre estudiantes y cursos.
 */
public interface EnrollmentService {

    /**
     * ✅ Inscribe un estudiante en un curso.
     * Lanza BusinessRuleException si ya existe la inscripción.
     */
    EnrollmentResponse enrollStudent(EnrollmentRequest request);

    /**
     * ✅ Da de baja a un estudiante de un curso.
     * Lanza BusinessRuleException si no existe la inscripción.
     */
    EnrollmentResponse unenrollStudent(EnrollmentRequest request);

    /**
     * ✅ Lista todas las inscripciones de un estudiante.
     */
    List<EnrollmentResponse> findEnrollmentsByStudent(Long studentId);

    /**
     * ✅ Lista todas las inscripciones de un curso.
     */
    List<EnrollmentResponse> findEnrollmentsByCourse(Long courseId);

    /**
     * ✅ Lista todas las inscripciones del sistema.
     */
    List<EnrollmentResponse> findAllEnrollments();

    /**
     * ✅ Busca una inscripción por ID.
     */
    EnrollmentResponse findById(Long id);

    /**
     * ✅ Actualiza una inscripción existente.
     */
    EnrollmentResponse update(Long id, EnrollmentRequest request);

    /**
     * ✅ Elimina una inscripción por ID.
     */
    void delete(Long id);
}
