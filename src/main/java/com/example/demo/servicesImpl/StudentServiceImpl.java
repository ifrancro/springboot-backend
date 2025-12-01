package com.example.demo.servicesImpl;

import com.example.demo.dtos.student.StudentRequest;
import com.example.demo.dtos.student.StudentResponse;
import com.example.demo.entities.Student;
import com.example.demo.exceptions.BusinessRuleException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.StudentMapper;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;

    @Override
    public StudentResponse create(StudentRequest request) {
        // Validar email único (ahora es obligatorio)
        if (repository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Ya existe un estudiante con el email: " + request.getEmail());
        }

        Student entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        // Validar email único si cambió (ahora es obligatorio)
        if (!request.getEmail().equals(student.getEmail()) 
            && repository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Ya existe otro estudiante con el email: " + request.getEmail());
        }

        // 🔗 Cascada automática: Detectar cambio de estado del estudiante
        boolean wasActive = student.isActive();
        mapper.updateEntityFromRequest(request, student);
        boolean isNowActive = student.isActive();

        // 🔻 Si cambió de ACTIVO a INACTIVO: desactivar todos sus enrollments
        if (wasActive && !isNowActive && student.getEnrollments() != null) {
            student.getEnrollments().forEach(enrollment -> {
                if ("ACTIVE".equals(enrollment.getRegistrationState())) {
                    enrollment.setRegistrationState("INACTIVE");
                }
            });
        }
        
        // 🔺 Si cambió de INACTIVO a ACTIVO: reactivar enrollments si es seguro
        if (!wasActive && isNowActive && student.getEnrollments() != null) {
            student.getEnrollments().forEach(enrollment -> {
                // Solo reactivar si:
                // 1. El enrollment está INACTIVE (no RETIRED/CANCELLED)
                // 2. El curso está OPEN (acepta inscripciones)
                if ("INACTIVE".equals(enrollment.getRegistrationState()) 
                    && enrollment.getCourse() != null 
                    && "OPEN".equals(enrollment.getCourse().getStatus())) {
                    enrollment.setRegistrationState("ACTIVE");
                }
            });
        }

        return mapper.toResponse(repository.save(student));
    }

    @Override
    public List<StudentResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public StudentResponse findById(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));
        return mapper.toResponse(student);
    }

    @Override
    public void delete(Long id) {
        Student student = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + id));

        // ✅ Verificar si tiene relaciones (grades o enrollments)
        boolean hasGrades = student.getGrades() != null && !student.getGrades().isEmpty();
        boolean hasEnrollments = student.getEnrollments() != null && !student.getEnrollments().isEmpty();

        if (hasGrades || hasEnrollments) {
            // 🟡 Soft Delete: inhabilitar estudiante
            student.setActive(false);
            repository.save(student);

            // 🔗 Cascada: marcar enrollments como INACTIVE
            if (hasEnrollments) {
                student.getEnrollments().forEach(enrollment -> {
                    enrollment.setRegistrationState("INACTIVE");
                });
                repository.save(student);
            }
        } else {
            // 🔴 Hard Delete: eliminar físicamente (estudiante sin historial)
            repository.delete(student);
        }
    }
}
