package com.example.demo.mappers;

import com.example.demo.dtos.student.StudentRequest;
import com.example.demo.dtos.student.StudentResponse;
import com.example.demo.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentMapper {

    default Student toEntity(StudentRequest dto) {
        if (dto == null) return null;
        Student entity = new Student();
        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
        entity.setPhone(dto.getPhone());
        return entity;
    }

    default StudentResponse toResponse(Student entity) {
        if (entity == null) return null;
        StudentResponse dto = new StudentResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setActive(entity.isActive()); // ✅ MAPEAR estado activo/inactivo

        // ✅ MAPEAR ENROLLMENTS (cursos inscritos)
        if (entity.getEnrollments() != null) {
            dto.setEnrolledCourses(
                    entity.getEnrollments().stream()
                            .filter(e -> "ACTIVE".equals(e.getRegistrationState()))
                            .map(e -> e.getCourse().getName())
                            .collect(Collectors.toList())
            );
        } else {
            dto.setEnrolledCourses(new ArrayList<>());
        }
        return dto;
    }

    default List<StudentResponse> toResponseList(List<Student> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    default void updateEntityFromRequest(StudentRequest dto, Student entity) {
        if (dto == null || entity == null) return;
        if (dto.getName() != null && !dto.getName().isBlank())
            entity.setName(dto.getName());
        if (dto.getLastName() != null && !dto.getLastName().isBlank())
            entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null && !dto.getEmail().isBlank())
            entity.setEmail(dto.getEmail().trim());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());
        if (dto.getActive() != null) entity.setActive(dto.getActive()); // Permitir reactivación
    }
}
