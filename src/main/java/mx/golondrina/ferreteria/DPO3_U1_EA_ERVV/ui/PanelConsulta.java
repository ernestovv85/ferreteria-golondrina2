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
import javax.swing.table.DefaultTableModel;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.model.Estado;
import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.service.EstadoService;

public class PanelConsulta extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient EstadoService service;
    private final transient Runnable alRegresar;

    /** Registros mostrados; la fila i de la tabla corresponde a registros.get(i). */
    private final transient List<Estado> registros = new ArrayList<>();

    private final JLabel lblTotal = new JLabel();

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(new Object[] {"Clave", "Nombre", "Capital"}, 0) {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
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
                modeloTabla.addRow(new Object[] {estado.clave(), estado.nombre(), estado.capital()});
            }
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo leer el archivo de estados:\n" + ex.getMessage(),
                    "Error de archivo",
                    JOptionPane.WARNING_MESSAGE);
        }
        actualizarTotal();
    }

    private void actualizarTotal() {
        int total = registros.size();
        lblTotal.setText(total == 0
                ? "No hay estados registrados. Usa Catálogos > Estados para agregar uno."
                : total + (total == 1 ? " estado registrado" : " estados registrados"));
    }
}