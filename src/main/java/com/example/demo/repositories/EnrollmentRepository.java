package com.example.demo.repositories;

import com.example.demo.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 📝 EnrollmentRepository
 * -----------------------------------------------------
 * Repositorio para la relación ManyToMany entre Student y Course.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // ✅ Verifica si existe una inscripción para un estudiante y curso específico
    boolean existsByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    // ✅ Obtiene todas las inscripciones de un estudiante
    List<Enrollment> findByStudent_Id(Long studentId);

    // ✅ Obtiene todas las inscripciones de un curso
    List<Enrollment> findByCourse_Id(Long courseId);
}
