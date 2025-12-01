package com.example.demo.services;

import com.example.demo.dtos.student.StudentRequest;
import com.example.demo.dtos.student.StudentResponse;

import java.util.List;

/**
 * 🎓 StudentService
 * -----------------------------------------------------
 * Define las operaciones de negocio sobre la entidad Student.
 */
public interface StudentService {
    StudentResponse create(StudentRequest request);
    StudentResponse update(Long id, StudentRequest request);
    void delete(Long id);
    StudentResponse findById(Long id);
    List<StudentResponse> findAll();
}
