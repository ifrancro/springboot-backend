package com.example.demo.services;

import com.example.demo.dtos.teacher.TeacherRequest;
import com.example.demo.dtos.teacher.TeacherResponse;

import java.util.List;

/**
 * 👨‍🏫 TeacherService
 * -----------------------------------------------------
 * Define las operaciones de negocio sobre la entidad Teacher.
 */
public interface TeacherService {
    TeacherResponse create(TeacherRequest request);
    TeacherResponse update(Long id, TeacherRequest request);
    void delete(Long id);
    TeacherResponse findById(Long id);
    List<TeacherResponse> findAll();
}
