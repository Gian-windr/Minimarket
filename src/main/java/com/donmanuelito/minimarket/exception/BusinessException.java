package com.donmanuelito.minimarket.exception;

/** Regla de negocio incumplida (stock insuficiente, caja ya abierta, etc.). */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}
