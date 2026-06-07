package com.cloud_tecnoligical.nyxora_erp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Manejo global de excepciones para la API REST reactiva (WebFlux).
 *
 * Adaptado del GlobalExceptionHandler v3 (MVC) del equipo: se conserva el mismo
 * contrato de respuesta (ApiResponse status/message/error/data) y el mismo mapeo
 * de estados, pero:
 *   - usa @RestControllerAdvice (no @ControllerAdvice + vista),
 *   - las validaciones de body llegan como WebExchangeBindException (no MethodArgumentNotValidException),
 *   - BusinessException responde JSON (no una vista MVC).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(GlobalException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getStatus().value(), ex.getMessage(), true, null);
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        ApiResponse<Object> response = new ApiResponse<>(ex.getStatus().value(), ex.getMessage(), true, null);
        return new ResponseEntity<>(response, ex.getStatus());
    }

    // Validación de @Valid sobre el body en WebFlux
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(WebExchangeBindException ex) {
        String errorMessage = ex.getFieldErrors().stream().findFirst()
                .map(FieldError::getDefaultMessage).orElse("Error de validación.");
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage, true, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<Object>> handleNumberFormatException(NumberFormatException ex) {
        logger.error("NumberFormatException: ", ex);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Formato numérico incorrecto",
                true, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<ApiResponse<Object>> handleJsonMappingException(JsonMappingException ex) {
        logger.error("JsonMappingException: ", ex);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Error al procesar los datos",
                true, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Estados propios del framework (404, 405, 415...): respetar su código (no convertir a 409)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Object>> handleResponseStatus(ResponseStatusException ex) {
        String msg = ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString();
        return ResponseEntity.status(ex.getStatusCode())
            .body(new ApiResponse<>(ex.getStatusCode().value(), msg, true, null));
    }

    // Cualquier RuntimeException no específica
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        logger.error("RuntimeException no controlada", ex);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.CONFLICT.value(), ex.getMessage(), true, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // Cualquier otra excepción
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex) {
        logger.error("Excepción no controlada", ex);
        ApiResponse<Object> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(),
                true, null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
