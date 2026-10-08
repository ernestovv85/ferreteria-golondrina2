package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.model.Estado;
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
     * @throws ValidacionException si algún campo está vacío, es inválido o está duplicado
     * @throws IOException si no se pudo escribir el archivo
     */
    public Estado registrar(String clave, String nombre, String capital)
            throws ValidacionException, IOException {

        Estado estado = validar(clave, nombre, capital);
        validarDuplicados(estado, null);
        repository.guardar(estado);
        return estado;
    }

    /**
     * Valida los nuevos datos del registro identificado por {@code claveOriginal}
     * y, si son correctos, lo actualiza en el archivo de texto.
     *
     * @return el estado con los datos modificados (ya limpios)
     * @throws ValidacionException si algún campo está vacío, es inválido, está duplicado
     *                             o el registro ya no existe en el archivo
     * @throws IOException si no se pudo leer o escribir el archivo
     */
    public Estado modificar(String claveOriginal, String clave, String nombre, String capital)
            throws ValidacionException, IOException {

        Estado estado = validar(clave, nombre, capital);
        validarDuplicados(estado, claveOriginal);
        if (!repository.actualizar(claveOriginal, estado)) {
            throw new ValidacionException(registroNoEncontrado(claveOriginal));
        }
        return estado;
    }

    /**
     * Elimina del archivo de texto el estado con la clave indicada.
     *
     * @throws ValidacionException si el registro ya no existe en el archivo
     * @throws IOException si no se pudo leer o escribir el archivo
     */
    public void eliminar(String clave) throws ValidacionException, IOException {
        if (!repository.eliminar(clave)) {
            throw new ValidacionException(registroNoEncontrado(clave));
        }
    }

    public List<Estado> listar() throws IOException {
        return repository.listar();
    }

    public Path getArchivo() {
        return repository.getArchivo();
    }

    /**
     * Limpia y valida los tres campos. Se usa tanto al registrar como al modificar.
     *
     * @return un estado con los datos ya limpios
     */
    private static Estado validar(String clave, String nombre, String capital) throws ValidacionException {
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

        return new Estado(claveLimpia, nombreLimpio, capitalLimpia);
    }

    /**
     * Verifica que la clave y el nombre no pertenezcan a otro registro.
     *
     * @param claveExcluida clave del registro que se está modificando (se ignora en la
     *                      comparación); {@code null} al registrar uno nuevo
     */
    private void validarDuplicados(Estado estado, String claveExcluida)
            throws ValidacionException, IOException {

        for (Estado existente : repository.listar()) {
            if (claveExcluida != null && existente.clave().equalsIgnoreCase(claveExcluida)) {
                continue;
            }
            if (existente.clave().equalsIgnoreCase(estado.clave())) {
                throw new ValidacionException("Ya existe un estado con la clave " + estado.clave() + ".");
            }
            if (existente.nombre().equalsIgnoreCase(estado.nombre())) {
                throw new ValidacionException("El estado " + estado.nombre() + " ya está registrado.");
            }
        }
    }

    private static String registroNoEncontrado(String clave) {
        return "El estado con clave " + clave + " ya no existe en el archivo.\n"
                + "Actualiza la consulta e inténtalo de nuevo.";
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