package com.example.demo.repositories;

import com.example.demo.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 👨‍🏫 TeacherRepository
 * -----------------------------------------------------
 * Repositorio de acceso a datos para la entidad Teacher.
 */
@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    // ✅ Verifica duplicados por email
    boolean existsByEmail(String email);
}
