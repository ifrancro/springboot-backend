package com.example.demo.mappers;

import com.example.demo.dtos.course.CourseRequest;
import com.example.demo.dtos.course.CourseResponse;
import com.example.demo.entities.Course;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {

    default Course toEntity(CourseRequest dto) {
        if (dto == null) return null;
        Course entity = new Course();
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCapacity(dto.getCapacity());
        return entity;
    }

    default CourseResponse toResponse(Course entity) {
        if (entity == null) return null;
        CourseResponse dto = new CourseResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCapacity(entity.getCapacity());
        dto.setStatus(entity.getStatus()); // MAPEAR estado del curso

        // MAPEAR TEACHER ID Y NAME
        if (entity.getTeacher() != null) {
            dto.setTeacherId(entity.getTeacher().getId());
            dto.setTeacherName(entity.getTeacher().getName() + " " + entity.getTeacher().getLastName());
        }

        // MAPEAR ENROLLED STUDENTS
        if (entity.getEnrollments() != null) {
            dto.setEnrolledStudents(
                    entity.getEnrollments().stream()
                            .filter(e -> "ACTIVE".equals(e.getRegistrationState()))
                            .map(e -> e.getStudent().getName() + " " + e.getStudent().getLastName())
                            .collect(Collectors.toList())
            );
        } else {
            dto.setEnrolledStudents(new ArrayList<>());
        }
        return dto;
    }

    default List<CourseResponse> toResponseList(List<Course> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    default void updateEntityFromRequest(CourseRequest dto, Course entity) {
        if (dto == null || entity == null) return;
        if (dto.getName() != null && !dto.getName().isBlank())
            entity.setName(dto.getName());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getCapacity() != null) entity.setCapacity(dto.getCapacity());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus()); // Permitir cambiar estado
    }
}