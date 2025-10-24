package com.micro.serviceauth.controller.advice;

import com.micro.serviceauth.dto.ApiError;
import com.micro.serviceauth.dto.FieldViolation;
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
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Centralise la gestion des erreurs applicatives et techniques en réponses JSON stables.
 * <p>
 * - Les erreurs 4xx remontent en WARN (problème requête/ métier),<br>
 * - Les erreurs 5xx remontent en ERROR (problème serveur).
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /* ========= 400 Bad Request ========= */

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return badRequest(req, "Requête illisible ou JSON invalide.", ex);
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
        return badRequest(req, "Contrainte de validation violée.", ex, violations);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        return badRequest(req, ex.getMessage(), ex);
    }

    /* ========= 401 / 403 ========= */

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        // En pratique, la plupart des 401 sont gérées par l'entry point de Spring Security.
        return build(req, HttpStatus.UNAUTHORIZED, "Non authentifié.", ex, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(req, HttpStatus.FORBIDDEN, "Accès interdit.", ex, null);
    }

    /* ========= 404 Not Found ========= */

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
        return build(req, HttpStatus.NOT_FOUND, ex.getMessage() != null ? ex.getMessage() : "Ressource introuvable.", ex, null);
    }

    /* ========= 409 Conflict ========= */

    @ExceptionHandler({ IllegalStateException.class, DataIntegrityViolationException.class })
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex, HttpServletRequest req) {
        // IllegalStateException = règle métier; DataIntegrityViolation = contrainte DB
        return build(req, HttpStatus.CONFLICT, ex.getMessage(), ex, null);
    }

    /* ========= 500 Internal Server Error ========= */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error on {} {}: {}", req.getMethod(), req.getRequestURI(), ex.toString(), ex);
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, "Erreur interne du serveur.", ex, null);
    }

    /* ========= Helpers ========= */

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
        // Log: WARN pour 4xx, ERROR pour 5xx
        if (status.is4xxClientError()) {
            log.warn("{} {} -> {} {} | {}", req.getMethod(), path, status.value(), status.getReasonPhrase(), body.message());
        } else {
            log.error("{} {} -> {} {} | {}", req.getMethod(), path, status.value(), status.getReasonPhrase(), body.message(), ex);
        }
        return ResponseEntity.status(status).body(body);
    }
}
