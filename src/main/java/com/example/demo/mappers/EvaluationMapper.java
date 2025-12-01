package com.example.demo.mappers;

import com.example.demo.dtos.evaluation.EvaluationRequest;
import com.example.demo.dtos.evaluation.EvaluationResponse;
import com.example.demo.entities.Evaluation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EvaluationMapper {

    default Evaluation toEntity(EvaluationRequest dto) {
        if (dto == null) return null;
        Evaluation entity = new Evaluation();
        entity.setName(dto.getName());
        entity.setEvaluationType(dto.getEvaluationType());
        entity.setDescription(dto.getDescription());
        entity.setState(dto.getState() != null ? dto.getState() : "DRAFT");
        return entity;
    }

    default EvaluationResponse toResponse(Evaluation entity) {
        if (entity == null) return null;
        EvaluationResponse dto = new EvaluationResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEvaluationType(entity.getEvaluationType());
        dto.setDescription(entity.getDescription());
        dto.setState(entity.getState());

        if (entity.getCourse() != null) {
            dto.setCourseId(entity.getCourse().getId());
            dto.setCourseName(entity.getCourse().getName());
        }
        return dto;
    }

    default void updateEntityFromRequest(EvaluationRequest dto, Evaluation entity) {
        if (dto == null || entity == null) return;
        if (dto.getName() != null && !dto.getName().isBlank())
            entity.setName(dto.getName());
        if (dto.getEvaluationType() != null) entity.setEvaluationType(dto.getEvaluationType());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getState() != null) entity.setState(dto.getState());
    }
}
