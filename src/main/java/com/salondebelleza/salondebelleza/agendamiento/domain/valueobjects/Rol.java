package com.salondebelleza.salondebelleza.agendamiento.domain.valueobjects;

public enum Rol {
    OWNER,
    RECEPTIONIST;

    // Método helper para verificar si un String coincide con un Rol válido
    public static boolean isValid(String rol) {
        try {
            Rol.valueOf(rol);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}
