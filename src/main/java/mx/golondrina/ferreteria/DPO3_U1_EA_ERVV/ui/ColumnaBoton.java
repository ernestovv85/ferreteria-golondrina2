package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.ui;

import java.awt.Component;
import java.util.function.IntConsumer;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ColumnaBoton extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

    private static final long serialVersionUID = 1L;

    private final JTable tabla;
    private final transient IntConsumer accion;

    /** Botón que solo se dibuja en cada celda. */
    private final JButton botonRender;

    /** Botón que recibe el clic de la celda que se está editando. */
    private final JButton botonEditor;

    private int filaEditada = -1;

    public ColumnaBoton(JTable tabla, int columna, String texto, IntConsumer accion) {
        this.tabla = tabla;
        this.accion = accion;

        botonRender = crearBoton(texto);
        botonEditor = crearBoton(texto);
        botonEditor.addActionListener(e -> ejecutar());

        TableColumn columnaTabla = tabla.getColumnModel().getColumn(columna);
        columnaTabla.setCellRenderer(this);
        columnaTabla.setCellEditor(this);
        columnaTabla.setPreferredWidth(110);
        columnaTabla.setMaxWidth(110);
        columnaTabla.setResizable(false);
    }

    private static JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        return boton;
    }

    private void ejecutar() {
        int filaModelo = tabla.convertRowIndexToModel(filaEditada);
        // Primero se termina la edición de la celda y después se ejecuta la acción,
        // así la tabla puede recargarse sin conflictos.
        fireEditingStopped();
        SwingUtilities.invokeLater(() -> accion.accept(filaModelo));
    }

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                   boolean conFoco, int fila, int columna) {
        return botonRender;
    }

    @Override
    public Component getTableCellEditorComponent(JTable tabla, Object valor, boolean seleccionada,
                                                 int fila, int columna) {
        filaEditada = fila;
        return botonEditor;
    }

    @Override
    public Object getCellEditorValue() {
        return botonEditor.getText();
    }
}