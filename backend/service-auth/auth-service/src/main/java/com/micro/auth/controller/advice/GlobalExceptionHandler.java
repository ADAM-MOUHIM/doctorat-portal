package com.micro.auth.controller.advice;

import com.micro.auth.dto.ApiError;
import com.micro.auth.dto.FieldViolation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return badRequest(req, "Requ?te illisible ou JSON invalide.", ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<FieldViolation> violations = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            violations.add(new FieldViolation(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()));
        }
        return badRequest(req, "Erreur de validation.", ex, violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        List<FieldViolation> violations = ex.getConstraintViolations()
                .stream()
                .map(cv -> new FieldViolation(cv.getPropertyPath().toString(), cv.getMessage(), cv.getInvalidValue()))
                .toList();
        return badRequest(req, "Contrainte de validation viol?e.", ex, violations);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        return badRequest(req, ex.getMessage(), ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        return build(req, HttpStatus.UNAUTHORIZED, "Non authentifi?.", ex, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(req, HttpStatus.FORBIDDEN, "Acc?s interdit.", ex, null);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
        return build(req, HttpStatus.NOT_FOUND, ex.getMessage() != null ? ex.getMessage() : "Ressource introuvable.", ex, null);
    }

    @ExceptionHandler({ IllegalStateException.class, DataIntegrityViolationException.class })
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex, HttpServletRequest req) {
        return build(req, HttpStatus.CONFLICT, ex.getMessage(), ex, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error on {} {}: {}", req.getMethod(), req.getRequestURI(), ex.toString(), ex);
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur.", ex, null);
    }

    private ResponseEntity<ApiError> badRequest(HttpServletRequest req, String message, Exception ex) {
        return badRequest(req, message, ex, List.of());
    }

    private ResponseEntity<ApiError> badRequest(HttpServletRequest req, String message, Exception ex, List<FieldViolation> violations) {
        return build(req, HttpStatus.BAD_REQUEST, message, ex, violations);
    }

    private ResponseEntity<ApiError> build(HttpServletRequest req, HttpStatus status, String message, Exception ex, List<FieldViolation> violations) {
        String path = req.getRequestURI();
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                (message != null && !message.isBlank()) ? message : status.getReasonPhrase(),
                path,
                violations
        );
        if (status.is4xxClientError()) {
            log.warn("{} {} -> {} {} | {}", req.getMethod(), path, status.value(), status.getReasonPhrase(), body.message());
        } else {
            log.error("{} {} -> {} {} | {}", req.getMethod(), path, status.value(), status.getReasonPhrase(), body.message(), ex);
        }
        return ResponseEntity.status(status).body(body);
    }
}

