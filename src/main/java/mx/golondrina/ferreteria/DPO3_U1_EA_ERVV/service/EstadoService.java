package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.model.Estado;
import org.springframework.stereotype.Service;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.repository.EstadoRepository;

@Service
public class EstadoService {

    private static final int MAX_CLAVE = 4;
    private static final int MAX_TEXTO = 60;

    private final EstadoRepository repository;

    public EstadoService(EstadoRepository repository) {
        this.repository = repository;
    }

    /**
     * Valida los campos y, si son correctos, guarda el estado en el archivo de texto.
     *
     * @return el estado guardado (con los datos ya limpios)
     * @throws ValidacionException si algún campo está vacío o es inválido
     * @throws IOException si no se pudo escribir el archivo
     */
    public Estado registrar(String clave, String nombre, String capital)
            throws ValidacionException, IOException {

        String claveLimpia = limpiar(clave).toUpperCase(Locale.ROOT);
        String nombreLimpio = limpiar(nombre);
        String capitalLimpia = limpiar(capital);

        List<String> vacios = new ArrayList<>();
        if (claveLimpia.isEmpty()) {
            vacios.add("Clave");
        }
        if (nombreLimpio.isEmpty()) {
            vacios.add("Nombre del estado");
        }
        if (capitalLimpia.isEmpty()) {
            vacios.add("Capital");
        }
        if (!vacios.isEmpty()) {
            throw new ValidacionException("Los siguientes campos no pueden estar vacíos:\n• "
                    + String.join("\n• ", vacios));
        }

        validarTexto("Clave", claveLimpia, MAX_CLAVE);
        validarTexto("Nombre del estado", nombreLimpio, MAX_TEXTO);
        validarTexto("Capital", capitalLimpia, MAX_TEXTO);

        for (Estado existente : repository.listar()) {
            if (existente.clave().equalsIgnoreCase(claveLimpia)) {
                throw new ValidacionException("Ya existe un estado con la clave " + claveLimpia + ".");
            }
            if (existente.nombre().equalsIgnoreCase(nombreLimpio)) {
                throw new ValidacionException("El estado " + nombreLimpio + " ya está registrado.");
            }
        }

        Estado estado = new Estado(claveLimpia, nombreLimpio, capitalLimpia);
        repository.guardar(estado);
        return estado;
    }

    public List<Estado> listar() throws IOException {
        return repository.listar();
    }

    public Path getArchivo() {
        return repository.getArchivo();
    }

    private static String limpiar(String texto) {
        return texto == null ? "" : texto.trim().replaceAll("\\s+", " ");
    }

    private static void validarTexto(String campo, String valor, int maximo) throws ValidacionException {
        if (valor.length() > maximo) {
            throw new ValidacionException("El campo " + campo + " admite máximo " + maximo + " caracteres.");
        }
        if (valor.contains(Estado.SEPARADOR)) {
            throw new ValidacionException("El campo " + campo + " no puede contener el carácter " + Estado.SEPARADOR);
        }
    }
}