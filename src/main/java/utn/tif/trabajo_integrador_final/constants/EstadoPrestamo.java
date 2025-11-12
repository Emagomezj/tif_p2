package utn.tif.trabajo_integrador_final.constants;

public enum EstadoPrestamo {
    ACTIVO("activo"),
    DEVUELTO("devuelto"),
    VENCIDO("vencido");
    private final String value;
    private EstadoPrestamo(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public static EstadoPrestamo fromString(String text) {
        for (EstadoPrestamo e : EstadoPrestamo.values()) {
            if (e.value.equalsIgnoreCase(text)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado desconocido: " + text);
    }
}