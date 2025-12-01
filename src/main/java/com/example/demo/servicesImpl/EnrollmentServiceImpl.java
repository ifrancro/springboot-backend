package com.example.demo.servicesImpl;

import com.example.demo.dtos.enrollment.EnrollmentRequest;
import com.example.demo.dtos.enrollment.EnrollmentResponse;
import com.example.demo.entities.Course;
import com.example.demo.entities.Enrollment;
import com.example.demo.entities.Student;
import com.example.demo.exceptions.BusinessRuleException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.EnrollmentMapper;
import com.example.demo.repositories.CourseRepository;
import com.example.demo.repositories.EnrollmentRepository;
import com.example.demo.repositories.EvaluationRepository;
import com.example.demo.repositories.GradeRepository;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.services.EnrollmentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 📝 EnrollmentServiceImpl
 * -----------------------------------------------------
 * Implementa la lógica de negocio para la relación ManyToMany
 * entre estudiantes y cursos, garantizando integridad y consistencia.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EvaluationRepository evaluationRepository;
    private final GradeRepository gradeRepository;
    private final EnrollmentMapper mapper;

    /**
     * ✅ Obtiene TODAS las inscripciones del sistema
     */
    @Override
    public List<EnrollmentResponse> findAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * ✅ Inscribe un estudiante en un curso
     */
    @Override
    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + request.getStudentId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.getCourseId()));

        // ✅ Validar que el estudiante esté activo
        if (!student.isActive()) {
            throw new BusinessRuleException(
                "No se puede inscribir a un estudiante inactivo. " +
                "Primero debe activar al estudiante."
            );
        }

        // ✅ Validar capacidad del curso
        if (course.getCapacity() != null && course.getEnrollments() != null
                && course.getEnrollments().size() >= course.getCapacity()) {
            throw new BusinessRuleException("El curso ya alcanzó su capacidad máxima");
        }

        if (enrollmentRepository.existsByStudent_IdAndCourse_Id(request.getStudentId(), request.getCourseId())) {
            throw new BusinessRuleException("El estudiante ya está inscrito en este curso.");
        }

        Enrollment enrollment = mapper.toEntity(request);
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        return mapper.toResponse(enrollmentRepository.save(enrollment));
    }

    /**
     * ✅ Da de baja a un estudiante de un curso
     */
    @Override
    public EnrollmentResponse unenrollStudent(EnrollmentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + request.getStudentId()));

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + request.getCourseId()));

        List<Enrollment> enrollments = enrollmentRepository.findByStudent_Id(request.getStudentId());
        Enrollment enrollment = enrollments.stream()
                .filter(e -> e.getCourse().getId().equals(request.getCourseId()))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("El estudiante no está inscrito en este curso."));

        // ✅ Validación CRÍTICA: Verificar si el estudiante tiene calificaciones en este curso
        boolean hasGrades = evaluationRepository.findByCourse_Id(request.getCourseId()).stream()
                .anyMatch(evaluation -> gradeRepository.existsByStudent_IdAndEvaluation_Id(request.getStudentId(), evaluation.getId()));

        if (hasGrades) {
            throw new BusinessRuleException(
                "No se puede eliminar la inscripción porque el estudiante tiene calificaciones en este curso. " +
                "Elimine primero las calificaciones asociadas."
            );
        }

        enrollmentRepository.delete(enrollment);
        return mapper.toResponse(enrollment);
    }

    /**
     * ✅ Devuelve todas las inscripciones de un estudiante
     */
    @Override
    public List<EnrollmentResponse> findEnrollmentsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + studentId));

        return enrollmentRepository.findByStudent_Id(studentId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * ✅ Devuelve todas las inscripciones de un curso
     */
    @Override
    public List<EnrollmentResponse> findEnrollmentsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado con ID: " + courseId));

        return enrollmentRepository.findByCourse_Id(courseId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * ✅ Busca una inscripción por ID
     */
    @Override
    public EnrollmentResponse findById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + id));
        return mapper.toResponse(enrollment);
    }

    /**
     * ✅ Actualiza una inscripción existente
     */
    @Override
    public EnrollmentResponse update(Long id, EnrollmentRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + id));

        // ⚠️ REGLA DE INTEGRIDAD: NO permitir cambiar estudiante o curso en una inscripción
        // Si se equivocaron, deben eliminar y crear nueva
        if (!enrollment.getStudent().getId().equals(request.getStudentId())) {
            throw new BusinessRuleException(
                "No se puede cambiar el estudiante de una inscripción existente. " +
                "Elimina esta inscripción y crea una nueva con el estudiante correcto."
            );
        }

        if (!enrollment.getCourse().getId().equals(request.getCourseId())) {
            throw new BusinessRuleException(
                "No se puede cambiar el curso de una inscripción existente. " +
                "Elimina esta inscripción y crea una nueva con el curso correcto."
            );
        }

        // ✅ Validar que si se intenta activar el enrollment, el estudiante debe estar activo
        if ("ACTIVE".equals(request.getRegistrationState())) {
            if (!enrollment.getStudent().isActive()) {
                throw new BusinessRuleException(
                    "No se puede activar la inscripción porque el estudiante está inactivo. " +
                    "Primero debe activar al estudiante."
                );
            }
        }

        // Solo se permite actualizar el estado de la inscripción usando el mapper
        mapper.updateEntityFromRequest(request, enrollment);
        
        return mapper.toResponse(enrollmentRepository.save(enrollment));
    }

    /**
     * ✅ Elimina una inscripción por ID
     */
    @Override
    public void delete(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada con ID: " + id));

        // Validar si tiene calificaciones asociadas
        boolean hasGrades = evaluationRepository.findByCourse_Id(enrollment.getCourse().getId()).stream()
                .anyMatch(evaluation -> gradeRepository.existsByStudent_IdAndEvaluation_Id(
                    enrollment.getStudent().getId(), evaluation.getId()));

        if (hasGrades) {
            throw new BusinessRuleException(
                "No se puede eliminar la inscripción porque el estudiante tiene calificaciones en este curso"
            );
        }

        enrollmentRepository.delete(enrollment);
    }
}
