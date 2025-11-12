package utn.tif.trabajo_integrador_final.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(String entityName, Object id) {
        super(String.format("%s con ID %s no encontrado", entityName, id));
    }

    public EntityNotFoundException(Class<?> entityClass, Object id) {
        super(String.format("Entidad %s con ID %s no encontrada",
                entityClass.getSimpleName(), id));
    }
}