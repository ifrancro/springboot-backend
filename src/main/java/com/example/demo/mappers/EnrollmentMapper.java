package com.example.demo.mappers;

import com.example.demo.dtos.enrollment.EnrollmentRequest;
import com.example.demo.dtos.enrollment.EnrollmentResponse;
import com.example.demo.entities.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.Date;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EnrollmentMapper {

    default Enrollment toEntity(EnrollmentRequest dto) {
        if (dto == null) return null;
        Enrollment entity = new Enrollment();
        entity.setRegistrationDate(new Date());
        entity.setRegistrationState("ACTIVE");
        return entity;
    }

    default EnrollmentResponse toResponse(Enrollment entity) {
        if (entity == null) return null;
        EnrollmentResponse dto = new EnrollmentResponse();
        dto.setId(entity.getId());
        dto.setRegistrationDate(entity.getRegistrationDate());
        dto.setRegistrationState(entity.getRegistrationState());

        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentName(entity.getStudent().getName() + " " + entity.getStudent().getLastName());
        }

        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
            dto.setCourseName(entity.getCourse().getName());
        }
        return dto;
    }

    default void updateEntityFromRequest(EnrollmentRequest dto, Enrollment entity) {
        if (dto == null || entity == null) return;
        // ⚠️ SOLO se permite actualizar el estado de la inscripción
        // NO se actualiza student, course, ni registration_date (mantiene valores originales)
        if (dto.getRegistrationState() != null) {
            entity.setRegistrationState(dto.getRegistrationState());
        }
    }
}
