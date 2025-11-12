package utn.tif.trabajo_integrador_final.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class Hasher {

    private final PasswordEncoder encoder;

    @Autowired
    public Hasher(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public String hash(String password) {
        return encoder.encode(password);
    }

    public boolean checkHash(String password, String hash) {
        return encoder.matches(password, hash);
    }
}