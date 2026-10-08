package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.model;

/**
 * Registro del Catálogo de Estados.
 *
 * @param clave   abreviatura del estado (ej. JAL)
 * @param nombre  nombre oficial del estado (ej. Jalisco)
 * @param capital ciudad capital (ej. Guadalajara)
 */
public record Estado(String clave, String nombre, String capital) {

    public static final String SEPARADOR = "|";

    /** Convierte el registro a una línea del archivo de texto. */
    public String aLineaTexto() {
        return clave + SEPARADOR + nombre + SEPARADOR + capital;
    }

    /** Reconstruye un registro a partir de una línea del archivo de texto. */
    public static Estado desdeLineaTexto(String linea) {
        String[] partes = linea.split("\\|", -1);
        if (partes.length != 3) {
            throw new IllegalArgumentException("Línea con formato inválido: " + linea);
        }
        return new Estado(partes[0], partes[1], partes[2]);
    }

    /** Texto para mostrar en el mensaje de confirmación. */
    public String descripcion() {
        return "Clave: " + clave + "\nNombre: " + nombre + "\nCapital: " + capital;
    }
}