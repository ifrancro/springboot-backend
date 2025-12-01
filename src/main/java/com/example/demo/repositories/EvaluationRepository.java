package com.example.demo.repositories;

import com.example.demo.entities.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 📋 EvaluationRepository
 * -----------------------------------------------------
 * Repositorio de acceso a datos para la entidad Evaluation.
 */
@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    // ✅ Obtiene todas las evaluaciones de un curso
    List<Evaluation> findByCourse_Id(Long courseId);

    // ✅ Verifica si existe una evaluación con el mismo nombre en un curso
    boolean existsByNameAndCourse_Id(String name, Long courseId);
}
