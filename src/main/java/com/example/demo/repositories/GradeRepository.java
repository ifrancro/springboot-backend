package com.example.demo.repositories;

import com.example.demo.entities.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 📊 GradeRepository
 * -----------------------------------------------------
 * Repositorio de acceso a datos para la entidad Grade.
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // ✅ Obtiene todas las notas de un estudiante
    List<Grade> findByStudent_Id(Long studentId);

    // ✅ Obtiene la nota de una evaluación específica
    Optional<Grade> findByEvaluation_Id(Long evaluationId);

    // ✅ Verifica si existe una nota para un estudiante y evaluación específica
    boolean existsByStudent_IdAndEvaluation_Id(Long studentId, Long evaluationId);

    // ✅ Verifica si una evaluación tiene calificaciones asociadas
    boolean existsByEvaluation_Id(Long evaluationId);
}
