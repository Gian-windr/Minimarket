package com.donmanuelito.minimarket.dto;

public record LoginResponse(
        String token,
        String tipo,
        String username,
        String rol,
        String nombreEmpleado) {

    public static LoginResponse bearer(String token, String username, String rol, String nombreEmpleado) {
        return new LoginResponse(token, "Bearer", username, rol, nombreEmpleado);
    }
}
