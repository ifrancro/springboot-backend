package com.example.demo.servicesImpl;

import com.example.demo.dtos.grade.GradeRequest;
import com.example.demo.dtos.grade.GradeResponse;
import com.example.demo.entities.Evaluation;
import com.example.demo.entities.Grade;
import com.example.demo.entities.Student;
import com.example.demo.exceptions.BusinessRuleException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.GradeMapper;
import com.example.demo.repositories.EvaluationRepository;
import com.example.demo.repositories.GradeRepository;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.repositories.EnrollmentRepository;
import com.example.demo.services.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 📊 GradeServiceImpl
 * -----------------------------------------------------
 * Servicio empresarial para gestión de notas.
 * Aplica reglas de negocio, validaciones y conversión DTO ↔ Entity.
 */
@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository repository;
    private final StudentRepository studentRepository;
    private final EvaluationRepository evaluationRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeMapper mapper;

    @Override
    public GradeResponse create(GradeRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + request.getStudentId()));

        Evaluation evaluation = evaluationRepository.findById(request.getEvaluationId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + request.getEvaluationId()));

        // ✅ Verificar que el estudiante esté inscrito en el curso de la evaluación
        Long courseId = evaluation.getCourse().getId();
        boolean enrolled = enrollmentRepository.existsByStudent_IdAndCourse_Id(request.getStudentId(), courseId);
        if (!enrolled) {
            throw new BusinessRuleException("El estudiante no está inscrito en el curso de esta evaluación");
        }

        if (repository.existsByStudent_IdAndEvaluation_Id(request.getStudentId(), request.getEvaluationId())) {
            throw new BusinessRuleException("Ya existe una nota para este estudiante en esta evaluación");
        }

        Grade entity = mapper.toEntity(request);
        entity.setStudent(student);
        entity.setEvaluation(evaluation);

        // ✅ Calcular automáticamente si aprobó
        if (entity.getValueDecimal() != null) {
            entity.setPassed(entity.getValueDecimal() >= 51.0);
        }

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public GradeResponse update(Long id, GradeRequest request) {
        Grade grade = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada con ID: " + id));

        // ⚠️ Validación de Integridad: NO permitir cambiar el estudiante o evaluación de una nota existente
        if (!grade.getStudent().getId().equals(request.getStudentId())) {
            throw new BusinessRuleException(
                "No se puede cambiar el estudiante de una nota existente. " +
                "Elimina esta nota y crea una nueva para el estudiante correcto."
            );
        }

        if (!grade.getEvaluation().getId().equals(request.getEvaluationId())) {
            throw new BusinessRuleException(
                "No se puede cambiar la evaluación de una nota existente. " +
                "Elimina esta nota y crea una nueva para la evaluación correcta."
            );
        }

        // No es necesario buscar student y evaluation porque ya validamos que no cambian
        // Solo actualizamos valueDecimal y feedback mediante el mapper
        mapper.updateEntityFromRequest(request, grade);

        // ✅ Recalcular aprobado/reprobado automáticamente
        if (grade.getValueDecimal() != null) {
            grade.setPassed(grade.getValueDecimal() >= 51.0);
        }
        return mapper.toResponse(repository.save(grade));
    }

    @Override
    public List<GradeResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public GradeResponse findById(Long id) {
        Grade grade = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada con ID: " + id));
        return mapper.toResponse(grade);
    }

    @Override
    public List<GradeResponse> findByStudentId(Long studentId) {
        studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + studentId));

        return repository.findByStudent_Id(studentId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Grade grade = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota no encontrada con ID: " + id));
        repository.delete(grade);
    }
}
