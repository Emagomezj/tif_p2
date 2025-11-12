package utn.tif.trabajo_integrador_final.utils;

import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Id_generator {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    public static String generarId(){

        String timestamp = LocalDateTime.now().format(formatter);
        String uuid = UUID.randomUUID().toString();
        return uuid +"_"+ timestamp;
    }
}
