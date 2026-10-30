package net.montaki.accountservice.web.rest;

import net.montaki.accountservice.service.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handle(BusinessException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}