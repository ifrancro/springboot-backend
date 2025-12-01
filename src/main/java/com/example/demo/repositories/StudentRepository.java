package com.example.demo.repositories;

import com.example.demo.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 🎓 StudentRepository
 * -----------------------------------------------------
 * Repositorio de acceso a datos para la entidad Student.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // ✅ Verifica duplicados por email
    boolean existsByEmail(String email);
}
