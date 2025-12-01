package com.example.demo.mappers;

import com.example.demo.dtos.grade.GradeRequest;
import com.example.demo.dtos.grade.GradeResponse;
import com.example.demo.entities.Grade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GradeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "evaluation", ignore = true)
    Grade toEntity(GradeRequest request);

    @Mapping(source = "student.id", target = "studentId")
    @Mapping(target = "studentName", expression = "java(grade.getStudent().getName() + \" \" + grade.getStudent().getLastName())")
    @Mapping(source = "evaluation.id", target = "evaluationId")
    @Mapping(source = "evaluation.name", target = "evaluationName")
    @Mapping(source = "evaluation.course.name", target = "courseName")
    GradeResponse toResponse(Grade grade);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "evaluation", ignore = true)
    @Mapping(target = "passed", ignore = true) // Se calcula automáticamente en el servicio
    void updateEntityFromRequest(GradeRequest request, @MappingTarget Grade grade);
}
