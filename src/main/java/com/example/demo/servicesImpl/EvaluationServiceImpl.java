package com.example.demo.servicesImpl;

import com.example.demo.dtos.evaluation.EvaluationRequest;
import com.example.demo.dtos.evaluation.EvaluationResponse;
import com.example.demo.entities.Course;
import com.example.demo.entities.Evaluation;
import com.example.demo.exceptions.BusinessRuleException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.EvaluationMapper;
import com.example.demo.repositories.CourseRepository;
import com.example.demo.repositories.EvaluationRepository;
import com.example.demo.services.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 📋 EvaluationServiceImpl
 * -----------------------------------------------------
 * Servicio empresarial para gestión de evaluaciones.
 * Aplica reglas de negocio, validaciones y conversión DTO ↔ Entity.
 */
@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository repository;
    private final CourseRepository courseRepository;
    private final EvaluationMapper mapper;
    private final com.example.demo.repositories.GradeRepository gradeRepository;

    @Override
    public EvaluationResponse create(EvaluationRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.getCourseId()));

        if (repository.existsByNameAndCourse_Id(request.getName(), request.getCourseId())) {
            throw new BusinessRuleException("Ya existe una evaluación con el nombre: " + request.getName() + " en este curso");
        }

        Evaluation entity = mapper.toEntity(request);
        entity.setCourse(course);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public EvaluationResponse update(Long id, EvaluationRequest request) {
        Evaluation evaluation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));

        // ⚠️ Validación de Integridad: Si ya tiene calificaciones, advertir sobre cambios críticos
        if (evaluation.getGrade() != null && 
            (!evaluation.getName().equals(request.getName()) || 
             !evaluation.getCourse().getId().equals(request.getCourseId()))) {
            throw new BusinessRuleException(
                "Esta evaluación ya tiene calificaciones asociadas. " +
                "No se puede cambiar el nombre o el curso. " +
                "Considera crear una nueva evaluación."
            );
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.getCourseId()));

        if (!evaluation.getName().equals(request.getName()) &&
                repository.existsByNameAndCourse_Id(request.getName(), request.getCourseId())) {
            throw new BusinessRuleException("Ya existe otra evaluación con el nombre: " + request.getName() + " en este curso");
        }

        mapper.updateEntityFromRequest(request, evaluation);
        evaluation.setCourse(course);
        return mapper.toResponse(repository.save(evaluation));
    }

    @Override
    public List<EvaluationResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public EvaluationResponse findById(Long id) {
        Evaluation evaluation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));
        return mapper.toResponse(evaluation);
    }

    @Override
    public List<EvaluationResponse> findByCourseId(Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + courseId));

        return repository.findByCourse_Id(courseId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Evaluation evaluation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación no encontrada con ID: " + id));
        
        // ✅ Validación preventiva: Verificar si tiene calificaciones asociadas usando el repositorio
        boolean hasGrades = gradeRepository.existsByEvaluation_Id(id);
        
        if (hasGrades) {
            throw new BusinessRuleException(
                "No se puede eliminar la evaluación '" + evaluation.getName() + "' porque tiene calificaciones asociadas. " +
                "Elimine primero las calificaciones (notas) de los estudiantes antes de eliminar la evaluación."
            );
        }
        
        repository.delete(evaluation);
    }
}
