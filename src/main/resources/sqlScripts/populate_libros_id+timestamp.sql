USE tif_db;

-- Procedimiento para generar libros aleatorios
DELIMITER $$

CREATE PROCEDURE InsertarLibrosMasivos()
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE book_id VARCHAR(60);
    DECLARE book_isbn VARCHAR(17);
    DECLARE book_titulo VARCHAR(150);
    DECLARE book_autor VARCHAR(120);
    DECLARE book_editorial VARCHAR(100);
    DECLARE book_anio INT;
    DECLARE book_dewey VARCHAR(20);
    DECLARE book_estanteria VARCHAR(20);
    DECLARE book_idioma VARCHAR(30);
    DECLARE book_existencias INT;
    DECLARE book_disponibles INT;
    DECLARE current_ts VARCHAR(14);

    -- Arrays de datos para generar contenido realista
    DECLARE editoriales TEXT DEFAULT 'Penguin Random House,HarperCollins,Simon & Schuster,Macmillan,Hachette,Penguin,Anagrama,Alfaguara,Tusquets,Planeta,Seix Barral,Destino,Grijalbo';
    DECLARE idiomas TEXT DEFAULT 'Español,English,Français,Deutsch,Italiano,Portugués,Chinese,Japanese';
    DECLARE clasificaciones TEXT DEFAULT '000,100,200,300,400,500,600,700,800,900,020,150,330,510,780,810,940';
    DECLARE estanterias TEXT DEFAULT 'A1,A2,A3,B1,B2,B3,C1,C2,C3,D1,D2,D3,E1,E2,E3';

    DECLARE editorial_count INT;
    DECLARE idioma_count INT;
    DECLARE clasif_count INT;
    DECLARE estanteria_count INT;

    SET editorial_count = LENGTH(editoriales) - LENGTH(REPLACE(editoriales, ',', '')) + 1;
    SET idioma_count = LENGTH(idiomas) - LENGTH(REPLACE(idiomas, ',', '')) + 1;
    SET clasif_count = LENGTH(clasificaciones) - LENGTH(REPLACE(clasificaciones, ',', '')) + 1;
    SET estanteria_count = LENGTH(estanterias) - LENGTH(REPLACE(estanterias, ',', '')) + 1;

    WHILE i < 10000 DO
        -- Generar timestamp actual (YYYYMMDDHHMMSS)
        SET current_ts = DATE_FORMAT(NOW(), '%Y%m%d%H%i%S');

        -- Generar ID único (UUID + "_" + timestamp)
        SET book_id = CONCAT(UUID(), '_', current_ts);

        -- Generar ISBN único
        SET book_isbn = CONCAT(
            '978-',
            LPAD(FLOOR(1 + RAND() * 99999), 5, '0'),
            '-',
            LPAD(FLOOR(1 + RAND() * 999), 3, '0'),
            '-',
            FLOOR(1 + RAND() * 9)
        );

        -- Generar título realista
        SET book_titulo = CONCAT(
            ELT(1 + FLOOR(RAND() * 20),
                'El misterio de', 'La historia de', 'Crónica de', 'Los secretos de',
                'Viaje al', 'El arte de', 'Ciencia y', 'Filosofía del',
                'Poemas de', 'Tratado sobre', 'Manual de', 'Guía completa de',
                'Aventuras en', 'El mundo de', 'Reflexiones sobre', 'Teoría de',
                'Práctica del', 'Estudio sobre', 'Introducción a', 'Avances en'
            ),
            ' ',
            ELT(1 + FLOOR(RAND() * 25),
                'la vida', 'el tiempo', 'la naturaleza', 'la mente humana',
                'la sociedad', 'la tecnología', 'el universo', 'las estrellas',
                'la literatura', 'el arte moderno', 'la física cuántica',
                'la programación', 'la inteligencia artificial', 'la música',
                'la filosofía oriental', 'la historia antigua', 'las matemáticas',
                'la biología molecular', 'la economía global', 'la psicología',
                'la arquitectura', 'la ingeniería', 'la medicina', 'el derecho',
                'la educación'
            ),
            ' ',
            ELT(1 + FLOOR(RAND() * 10),
                'contemporánea', 'avanzada', 'para principiantes', 'esencial',
                'completa', 'moderna', 'clásica', 'revolucionaria', 'práctica',
                'teórica'
            )
        );

        -- Generar autor
        SET book_autor = CONCAT(
            ELT(1 + FLOOR(RAND() * 20),
                'Gabriel García', 'Isabel Allende', 'Mario Vargas', 'Julio Cortázar',
                'Jorge Luis', 'Pablo Neruda', 'Carlos Fuentes', 'Octavio Paz',
                'Ernest Hemingway', 'George Orwell', 'Jane Austen', 'Charles Dickens',
                'Stephen King', 'J.K. Rowling', 'Haruki Murakami', 'Umberto Eco',
                'Albert Camus', 'Jean-Paul Sartre', 'Friedrich Nietzsche', 'Immanuel Kant'
            ),
            ' ',
            ELT(1 + FLOOR(RAND() * 20),
                'Márquez', 'Llosa', 'Borges', 'Lorca', 'Quevedo', 'Cervantes',
                'Hemingway', 'Orwell', 'Austen', 'Dickens', 'King', 'Rowling',
                'Murakami', 'Eco', 'Camus', 'Sartre', 'Nietzsche', 'Kant',
                'Russell', 'Hawking'
            )
        );

        -- Seleccionar editorial aleatoria
        SET book_editorial = SUBSTRING_INDEX(SUBSTRING_INDEX(editoriales, ',', 1 + FLOOR(RAND() * editorial_count)), ',', -1);

        -- Año entre 1950 y 2024
        SET book_anio = 1950 + FLOOR(RAND() * 75);

        -- Clasificación Dewey
        SET book_dewey = SUBSTRING_INDEX(SUBSTRING_INDEX(clasificaciones, ',', 1 + FLOOR(RAND() * clasif_count)), ',', -1);

        -- Estantería
        SET book_estanteria = SUBSTRING_INDEX(SUBSTRING_INDEX(estanterias, ',', 1 + FLOOR(RAND() * estanteria_count)), ',', -1);

        -- Idioma
        SET book_idioma = SUBSTRING_INDEX(SUBSTRING_INDEX(idiomas, ',', 1 + FLOOR(RAND() * idioma_count)), ',', -1);

        -- Existencias y disponibles (1-10 copias)
        SET book_existencias = 1 + FLOOR(RAND() * 10);
        SET book_disponibles = book_existencias - FLOOR(RAND() * 3);
        IF book_disponibles < 0 THEN
            SET book_disponibles = 0;
END IF;

        -- Insertar el libro
INSERT INTO libros (
    id, isbn, titulo, autor, editorial, anio_edicion,
    clasificacion_dewey, estanteria, idioma, existencias, disponibles
) VALUES (
             book_id, book_isbn, book_titulo, book_autor, book_editorial, book_anio,
             book_dewey, book_estanteria, book_idioma, book_existencias, book_disponibles
         );

SET i = i + 1;

        -- Mostrar progreso cada 1000 registros
        IF i % 1000 = 0 THEN
SELECT CONCAT('Insertados ', i, ' libros...') AS progreso;
END IF;
END WHILE;

SELECT 'Inserción completada: 10,000 libros agregados' AS resultado;
END$$

DELIMITER ;

-- Ejecutar el procedimiento
CALL InsertarLibrosMasivos();

-- Verificar la inserción
SELECT COUNT(*) AS total_libros FROM libros;
SELECT * FROM libros LIMIT 10;

-- Estadísticas de los datos insertados
SELECT
    COUNT(*) AS total_libros,
    COUNT(DISTINCT isbn) AS isbn_unicos,
    COUNT(DISTINCT autor) AS autores_unicos,
    COUNT(DISTINCT editorial) AS editoriales_unicas,
    AVG(existencias) AS promedio_existencias,
    SUM(disponibles) AS total_disponibles
FROM libros;
