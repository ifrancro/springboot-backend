package com.example.demo.repositories;

import com.example.demo.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 📚 CourseRepository
 * -----------------------------------------------------
 * Repositorio de acceso a datos para la entidad Course.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // ✅ Busca cursos por nombre (ignorando mayúsculas)
    List<Course> findByNameContainingIgnoreCase(String q);

    // ✅ Busca todos los cursos de un docente
    List<Course> findByTeacher_Id(Long teacherId);

    // ✅ Verifica duplicados por nombre
    boolean existsByName(String name);
}


