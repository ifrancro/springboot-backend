package com.example.demo.mappers;

import com.example.demo.dtos.teacher.TeacherRequest;
import com.example.demo.dtos.teacher.TeacherResponse;
import com.example.demo.entities.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeacherMapper {

    default Teacher toEntity(TeacherRequest dto) {
        if (dto == null) return null;
        Teacher entity = new Teacher();
        entity.setName(dto.getName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
        entity.setSpecialty(dto.getSpecialty());
        return entity;
    }

    default TeacherResponse toResponse(Teacher entity) {
        if (entity == null) return null;
        TeacherResponse dto = new TeacherResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLastName(entity.getLastName());
        dto.setEmail(entity.getEmail());
        dto.setSpecialty(entity.getSpecialty());
        dto.setActive(entity.isActive()); // ✅ MAPEAR estado activo/inactivo

        // MAPEAR COURSES (cursos que imparte)
        if (entity.getCourses() != null) {
            dto.setCourseNames(
                    entity.getCourses().stream()
                            .map(course -> course.getName())
                            .collect(Collectors.toList())
            );
        } else {
            dto.setCourseNames(new ArrayList<>());
        }
        return dto;
    }

    default List<TeacherResponse> toResponseList(List<Teacher> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toResponse).collect(Collectors.toList());
    }

    default void updateEntityFromRequest(TeacherRequest dto, Teacher entity) {
        if (dto == null || entity == null) return;
        if (dto.getName() != null && !dto.getName().isBlank())
            entity.setName(dto.getName());
        if (dto.getLastName() != null && !dto.getLastName().isBlank())
            entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null && !dto.getEmail().isBlank())
            entity.setEmail(dto.getEmail().trim());
        if (dto.getSpecialty() != null) entity.setSpecialty(dto.getSpecialty());
        if (dto.getActive() != null) entity.setActive(dto.getActive()); // Permitir reactivación
    }
}