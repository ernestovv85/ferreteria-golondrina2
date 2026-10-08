package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.model.Estado;
import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.service.EstadoService;
import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.service.ValidacionException;

public class PanelConsulta extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int COL_MODIFICAR = 3;
    private static final int COL_ELIMINAR = 4;

    private final transient EstadoService service;
    private final transient Runnable alRegresar;

    /** Registros mostrados; la fila i de la tabla corresponde a registros.get(i). */
    private final transient List<Estado> registros = new ArrayList<>();

    private final JLabel lblTotal = new JLabel();

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(new Object[] {"Clave", "Nombre", "Capital", "Modificar", "Eliminar"}, 0) {
                private static final long serialVersionUID = 1L;

                /** Solo las columnas de botones son "editables", para que reciban el clic. */
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return columna == COL_MODIFICAR || columna == COL_ELIMINAR;
                }
            };

    private final JTable tabla = new JTable(modeloTabla);

    public PanelConsulta(EstadoService service, Runnable alRegresar) {
        this.service = service;
        this.alRegresar = alRegresar;

        setLayout(new BorderLayout());
        setBackground(Tema.FONDO);

        add(crearTitulo(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);
    }

    private JLabel crearTitulo() {
        JLabel titulo = new JLabel("Consulta de estados", SwingConstants.CENTER);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        titulo.setBackground(Tema.PRIMARIO);
        titulo.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        return titulo;
    }

    private JPanel crearCuerpo() {
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);
        new ColumnaBoton(tabla, COL_MODIFICAR, "Modificar", this::modificar);
        new ColumnaBoton(tabla, COL_ELIMINAR, "Eliminar", this::eliminar);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder(
                "Registros almacenados en " + service.getArchivo().getFileName()));

        lblTotal.setForeground(Color.GRAY);
        lblTotal.setToolTipText(service.getArchivo().toString());

        JPanel cuerpo = new JPanel(new BorderLayout(0, 6));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        cuerpo.add(scroll, BorderLayout.CENTER);
        cuerpo.add(lblTotal, BorderLayout.SOUTH);
        return cuerpo;
    }

    private JPanel crearBotones() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        botones.setBackground(Tema.PRIMARIO_OSCURO);

        JButton btnRegresar = new JButton("Regresar al menú principal");
        btnRegresar.addActionListener(e -> alRegresar.run());

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> cargarDatos());

        botones.add(btnRegresar);
        botones.add(btnActualizar);
        return botones;
    }

    public void cargarDatos() {
        registros.clear();
        modeloTabla.setRowCount(0);
        try {
            registros.addAll(service.listar());
            for (Estado estado : registros) {
                modeloTabla.addRow(new Object[] {estado.clave(), estado.nombre(), estado.capital(), "Modificar", "Eliminar"});
            }
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo leer el archivo de estados:\n" + ex.getMessage(),
                    "Error de archivo",
                    JOptionPane.WARNING_MESSAGE);
        }
        actualizarTotal("");
    }

    private void modificar(int fila) {
        Estado original = registros.get(fila);
        DialogoModificar dialogo = new DialogoModificar(SwingUtilities.getWindowAncestor(this), original, service);

        dialogo.mostrar().ifPresent(modificado -> {
            registros.set(fila, modificado);
            modeloTabla.setValueAt(modificado.clave(), fila, 0);
            modeloTabla.setValueAt(modificado.nombre(), fila, 1);
            modeloTabla.setValueAt(modificado.capital(), fila, 2);
            actualizarTotal("Se modificó " + modificado.nombre() + " (" + modificado.clave() + ").");
        });
    }

    private void eliminar(int fila) {
        Estado estado = registros.get(fila);

        Object[] opciones = {"Confirmar", "Cancelar"};
        int respuesta = JOptionPane.showOptionDialog(
                this,
                "¿Deseas eliminar el siguiente estado?\n\n" + estado.descripcion(),
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[1]); // Cancelar como opción predeterminada

        if (respuesta != 0) {
            return;
        }

        try {
            service.eliminar(estado.clave());
            cargarDatos();
            actualizarTotal("Se eliminó " + estado.nombre() + " (" + estado.clave() + ").");
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            cargarDatos();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo actualizar el archivo:\n" + ex.getMessage(),
                    "Error de archivo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTotal(String aviso) {
        int total = registros.size();
        String texto = total == 0
                ? "No hay estados registrados. Usa Catálogos > Estados para agregar uno."
                : total + (total == 1 ? " estado registrado" : " estados registrados");
        lblTotal.setText(aviso.isEmpty() ? texto : aviso + "   ·   " + texto);
    }
}