package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.model.Estado;

/**
 * Persistencia del Catálogo de Estados en un archivo de texto.
 * Cada estado ocupa una línea con el formato: CLAVE|NOMBRE|CAPITAL
 */
@Repository
public class EstadoRepository {

    private final Path archivo;

    public EstadoRepository(@Value("${catalogo.estados.archivo:estados.txt}") String rutaArchivo) {
        this.archivo = Paths.get(rutaArchivo).toAbsolutePath();
    }

    /** Agrega el estado al final del archivo; lo crea si no existe. */
    public void guardar(Estado estado) throws IOException {
        Path carpeta = archivo.getParent();
        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }
        Files.writeString(
                archivo,
                estado.aLineaTexto() + System.lineSeparator(),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND);
    }

    /** Lee todos los estados guardados; si el archivo no existe devuelve una lista vacía. */
    public List<Estado> listar() throws IOException {
        List<Estado> estados = new ArrayList<>();
        if (!Files.exists(archivo)) {
            return estados;
        }
        for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
            if (!linea.isBlank()) {
                estados.add(Estado.desdeLineaTexto(linea));
            }
        }
        return estados;
    }

    public Path getArchivo() {
        return archivo;
    }
}