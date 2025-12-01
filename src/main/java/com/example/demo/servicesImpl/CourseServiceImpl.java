package com.example.demo.servicesImpl;

import com.example.demo.dtos.course.CourseRequest;
import com.example.demo.dtos.course.CourseResponse;
import com.example.demo.entities.Course;
import com.example.demo.entities.Teacher;
import com.example.demo.exceptions.BusinessRuleException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.CourseMapper;
import com.example.demo.repositories.CourseRepository;
import com.example.demo.repositories.TeacherRepository;
import com.example.demo.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 📚 CourseServiceImpl
 * -----------------------------------------------------
 * Servicio empresarial para gestión de cursos.
 * Aplica reglas de negocio, validaciones y conversión DTO ↔ Entity.
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;
    private final TeacherRepository teacherRepository;
    private final CourseMapper mapper;

    @Override
    public CourseResponse create(CourseRequest request) {
        if (repository.existsByName(request.getName())) {
            throw new BusinessRuleException("Ya existe un curso con el nombre: " + request.getName());
        }

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + request.getTeacherId()));

        Course entity = mapper.toEntity(request);
        entity.setTeacher(teacher);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public CourseResponse update(Long id, CourseRequest request) {
        Course course = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        if (!course.getName().equals(request.getName()) &&
                repository.existsByName(request.getName())) {
            throw new BusinessRuleException("Ya existe otro curso con el nombre: " + request.getName());
        }

        // ✅ Validación de Capacidad: No permitir reducir por debajo de inscritos actuales
        if (request.getCapacity() != null && request.getCapacity() < course.getCapacity()) {
            long inscritosActivos = course.getEnrollments() != null 
                ? course.getEnrollments().stream()
                    .filter(e -> "ACTIVE".equals(e.getRegistrationState()))
                    .count()
                : 0;
            
            if (request.getCapacity() < inscritosActivos) {
                throw new BusinessRuleException(
                    String.format("No puedes reducir la capacidad a %d. Hay %d estudiantes inscritos actualmente.",
                        request.getCapacity(), inscritosActivos)
                );
            }
        }

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + request.getTeacherId()));

        // 🔗 Guardar estado anterior del curso para detectar cambios
        String previousStatus = course.getStatus();
        
        mapper.updateEntityFromRequest(request, course);
        course.setTeacher(teacher);
        
        // 🔻 Cascada automática 1: Si el curso cambia a CLOSED o ARCHIVED
        if (("CLOSED".equals(request.getStatus()) || "ARCHIVED".equals(request.getStatus())) 
            && !request.getStatus().equals(previousStatus)) {
            
            // Desactivar todos los enrollments ACTIVE del curso
            if (course.getEnrollments() != null) {
                course.getEnrollments().forEach(enrollment -> {
                    if ("ACTIVE".equals(enrollment.getRegistrationState())) {
                        enrollment.setRegistrationState("INACTIVE");
                    }
                });
            }
            
            // Cerrar todas las evaluaciones del curso
            if (course.getEvaluations() != null) {
                course.getEvaluations().forEach(evaluation -> {
                    if (!"CLOSED".equals(evaluation.getState())) {
                        evaluation.setState("CLOSED");
                    }
                });
            }
        }
        
        // 🔺 Cascada automática 2: Si el curso se reabre (CLOSED/ARCHIVED → OPEN)
        if ("OPEN".equals(request.getStatus()) 
            && ("CLOSED".equals(previousStatus) || "ARCHIVED".equals(previousStatus))) {
            
            // Reactivar enrollments si es seguro
            if (course.getEnrollments() != null) {
                course.getEnrollments().forEach(enrollment -> {
                    // Solo reactivar si:
                    // 1. El enrollment está INACTIVE (no RETIRED/CANCELLED)
                    // 2. El estudiante está activo
                    // 3. El curso tiene capacidad disponible
                    if ("INACTIVE".equals(enrollment.getRegistrationState())
                        && enrollment.getStudent() != null
                        && enrollment.getStudent().isActive()) {
                        
                        // Validar capacidad del curso
                        long activeEnrollments = course.getEnrollments().stream()
                            .filter(e -> "ACTIVE".equals(e.getRegistrationState()))
                            .count();
                        
                        if (course.getCapacity() == null || activeEnrollments < course.getCapacity()) {
                            enrollment.setRegistrationState("ACTIVE");
                        }
                    }
                });
            }
        }
        
        return mapper.toResponse(repository.save(course));
    }

    @Override
    public List<CourseResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CourseResponse findById(Long id) {
        Course course = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));
        return mapper.toResponse(course);
    }

    @Override
    public List<CourseResponse> findByTeacherId(Long teacherId) {
        teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + teacherId));
        
        return repository.findByTeacher_Id(teacherId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Course course = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + id));

        // ✅ Verificar si tiene enrollments (matrículas)
        boolean hasEnrollments = course.getEnrollments() != null && !course.getEnrollments().isEmpty();

        if (hasEnrollments) {
            // 🔴 BLOQUEAR eliminación: el curso tiene historial académico
            throw new BusinessRuleException(
                "No se puede eliminar el curso porque tiene estudiantes matriculados. " +
                "Este curso forma parte del historial académico y debe ser preservado."
            );
        }

        // ✅ Verificar si tiene evaluaciones creadas
        boolean hasEvaluations = course.getEvaluations() != null && !course.getEvaluations().isEmpty();

        if (hasEvaluations) {
            // 🔴 BLOQUEAR eliminación: el curso tiene evaluaciones
            throw new BusinessRuleException(
                "No se puede eliminar el curso porque tiene evaluaciones creadas. " +
                "Elimine primero las evaluaciones asociadas."
            );
        }

        // ✅ Hard Delete: eliminar físicamente (curso sin matrículas ni evaluaciones)
        repository.delete(course);
    }
}
