package com.example.demo.exceptions;

import com.example.demo.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * ⚠️ Validaciones con @Valid (campos requeridos, formatos, etc.)
     * Retorna: HTTP 400 (Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Error de validación en uno o más campos")
                        .data(fieldErrors)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    /**
     * 🚫 Recursos no encontrados (404)
     * Ejemplo: buscar un empleado o departamento inexistente
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(ex.getMessage()));
    }

    /**
     * ⚖️ Violación de reglas de negocio (409)
     * Ejemplo: duplicar un registro único, violar restricción lógica, etc.
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(BusinessRuleException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(ex.getMessage()));
    }

    /**
     * 🔗 Error de integridad referencial (FK constraint violation)
     * Retorna: HTTP 409 (Conflict)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "No se puede eliminar este registro porque tiene relaciones asociadas";
        
        // Detectar el tipo de relación específica
        String errorMsg = ex.getMessage().toLowerCase();
        if (errorMsg.contains("grades")) {
            message = "No se puede eliminar porque tiene calificaciones asociadas";
        } else if (errorMsg.contains("enrollments")) {
            message = "No se puede eliminar porque tiene matrículas asociadas";
        } else if (errorMsg.contains("courses")) {
            message = "No se puede eliminar porque tiene cursos asignados";
        } else if (errorMsg.contains("evaluations")) {
            message = "No se puede eliminar porque tiene evaluaciones asociadas";
        }
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(message));
    }

    /**
     * 💥 Errores genéricos no controlados (500)
     * Retorna: HTTP 500 (Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex, HttpServletRequest request) {
        ex.printStackTrace(); // 🔍 log útil en desarrollo; puede omitirse en producción
        
        String detailedMessage = ex.getMessage() != null && !ex.getMessage().isEmpty() 
            ? "Error interno del servidor: " + ex.getMessage()
            : "Error interno del servidor. Por favor, contacte al administrador.";
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<String>builder()
                        .success(false)
                        .message(detailedMessage)
                        .data(ex.getClass().getSimpleName())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
