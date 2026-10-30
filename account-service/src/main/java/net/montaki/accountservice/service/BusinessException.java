package net.montaki.accountservice.service;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}