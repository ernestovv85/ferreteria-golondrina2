package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.model.Estado;

@Repository
public class EstadoRepository {

    private final Path archivo;

    public EstadoRepository(@Value("${catalogo.estados.archivo:estados.txt}") String rutaArchivo) {
        this.archivo = Paths.get(rutaArchivo).toAbsolutePath();
    }

    /** Agrega el estado al final del archivo; lo crea si no existe. */
    public void guardar(Estado estado) throws IOException {
        crearCarpeta();
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

    public boolean actualizar(String claveOriginal, Estado estadoNuevo) throws IOException {
        List<Estado> estados = listar();
        for (int i = 0; i < estados.size(); i++) {
            if (estados.get(i).clave().equalsIgnoreCase(claveOriginal)) {
                estados.set(i, estadoNuevo);
                reescribir(estados);
                return true;
            }
        }
        return false;
    }

    public boolean eliminar(String clave) throws IOException {
        List<Estado> estados = listar();
        boolean eliminado = estados.removeIf(e -> e.clave().equalsIgnoreCase(clave));
        if (eliminado) {
            reescribir(estados);
        }
        return eliminado;
    }

    public Path getArchivo() {
        return archivo;
    }

    private void reescribir(List<Estado> estados) throws IOException {
        crearCarpeta();
        List<String> lineas = new ArrayList<>();
        for (Estado estado : estados) {
            lineas.add(estado.aLineaTexto());
        }

        Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        Files.write(temporal, lineas, StandardCharsets.UTF_8);
        try {
            Files.move(temporal, archivo,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void crearCarpeta() throws IOException {
        Path carpeta = archivo.getParent();
        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }
    }
}