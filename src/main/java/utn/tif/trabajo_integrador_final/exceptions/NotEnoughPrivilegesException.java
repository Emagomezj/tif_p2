package utn.tif.trabajo_integrador_final.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotEnoughPrivilegesException extends RuntimeException {
    private final List<String> requiredPrivileges;
    private final List<String> userPrivileges;

    public NotEnoughPrivilegesException(String message) {
        this(message, null, null);
    }

    public NotEnoughPrivilegesException(String message, List<String> requiredPrivileges,
                                        List<String> userPrivileges) {
        super(message);
        this.requiredPrivileges = requiredPrivileges != null ?
                new ArrayList<>(requiredPrivileges) : new ArrayList<>();
        this.userPrivileges = userPrivileges != null ?
                new ArrayList<>(userPrivileges) : new ArrayList<>();
    }

    public List<String> getRequiredPrivileges() {
        return Collections.unmodifiableList(requiredPrivileges);
    }

    public List<String> getUserPrivileges() {
        return Collections.unmodifiableList(userPrivileges);
    }

    @Override
    public String toString() {
        String base = super.toString();
        if (!requiredPrivileges.isEmpty()) {
            base += " | Required privileges: " + requiredPrivileges;
        }
        if (!userPrivileges.isEmpty()) {
            base += " | User privileges: " + userPrivileges;
        }
        return base;
    }
}
