package com.example.demo.servicesImpl;

import com.example.demo.dtos.teacher.TeacherRequest;
import com.example.demo.dtos.teacher.TeacherResponse;
import com.example.demo.entities.Teacher;
import com.example.demo.exceptions.BusinessRuleException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.TeacherMapper;
import com.example.demo.repositories.TeacherRepository;
import com.example.demo.services.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 👨‍🏫 TeacherServiceImpl
 * -----------------------------------------------------
 * Servicio empresarial para gestión de docentes.
 * Aplica reglas de negocio, validaciones y conversión DTO ↔ Entity.
 */
@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;
    private final TeacherMapper mapper;

    @Override
    public TeacherResponse create(TeacherRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Ya existe un docente con el email: " + request.getEmail());
        }

        Teacher entity = mapper.toEntity(request);
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public TeacherResponse update(Long id, TeacherRequest request) {
        Teacher teacher = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + id));

        if (!teacher.getEmail().equals(request.getEmail()) &&
                repository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("Ya existe otro docente con el email: " + request.getEmail());
        }

        mapper.updateEntityFromRequest(request, teacher);
        return mapper.toResponse(repository.save(teacher));
    }

    @Override
    public List<TeacherResponse> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public TeacherResponse findById(Long id) {
        Teacher teacher = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + id));
        return mapper.toResponse(teacher);
    }

    @Override
    public void delete(Long id) {
        Teacher teacher = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente no encontrado con ID: " + id));

        // ✅ Verificar si tiene cursos asignados
        boolean hasCourses = teacher.getCourses() != null && !teacher.getCourses().isEmpty();

        if (hasCourses) {
            // 🟡 Soft Delete: solo inhabilitar
            teacher.setActive(false);
            repository.save(teacher);
        } else {
            // 🔴 Hard Delete: eliminar físicamente (docente creado por error sin cursos)
            repository.delete(teacher);
        }
    }
}
