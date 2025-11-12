CREATE DATABASE IF NOT EXISTS tif_db;
USE tif_db;

CREATE TABLE libros (
    id VARCHAR(60) PRIMARY KEY,
    isbn VARCHAR(17) UNIQUE,
    titulo VARCHAR(150) NOT NULL,
    autor VARCHAR(120) NOT NULL,
    editorial VARCHAR(100),
    anio_edicion INT,
    clasificacion_dewey VARCHAR(20),
    estanteria VARCHAR(20),
    idioma VARCHAR(30) DEFAULT 'Español',
    existencias INT DEFAULT 0,
    disponibles INT DEFAULT 0,
    eliminado BOOLEAN DEFAULT FALSE
);

CREATE TABLE usuarios (
    id VARCHAR(60) PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    fecha_nac DATE NOT NULL,
    hash VARCHAR(200) NOT NULL,
    eliminado BOOLEAN DEFAULT FALSE,
    created_at DATE NOT NULL,
    modified_at DATE NOT NULL,
    pass_last_mod DATE NOT NULL
);

CREATE TABLE roles_usuario (
    user_id VARCHAR (60),
    rol VARCHAR(20),
    PRIMARY KEY(user_id, rol),
    FOREIGN KEY (user_id) REFERENCES usuarios(id)
    ON DELETE CASCADE
);

CREATE TABLE prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(60) NOT NULL,
    libro_id VARCHAR(60) NOT NULL,
    fecha_prestamo DATE NOT NULL,
    fecha_plazo DATE NOT NULL,
    fecha_devolucion DATE NULL,
    estado ENUM('activo', 'devuelto', 'vencido') DEFAULT 'activo',
    FOREIGN KEY (user_id) REFERENCES usuarios(id) ON DELETE CASCADE ON UPDATE CASCADE ,
    FOREIGN KEY (libro_id) REFERENCES libros(id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE INDEX idx_prestamos_activos ON prestamos(user_id, estado);
CREATE INDEX idx_libros_disponibles ON libros(disponibles, eliminado);