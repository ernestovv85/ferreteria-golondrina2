package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.model.Estado;
import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.service.EstadoService;
import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.service.ValidacionException;

/**
 * Formulario del Catálogo de Estados: captura, guarda en archivo de texto
 * y muestra los estados registrados.
 */
public class PanelEstados extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient EstadoService service;
    private final transient Runnable alRegresar;

    private final JTextField txtClave = new JTextField(6);
    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtCapital = new JTextField(20);

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(new Object[] {"Clave", "Nombre", "Capital"}, 0) {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };

    public PanelEstados(EstadoService service, Runnable alRegresar) {
        this.service = service;
        this.alRegresar = alRegresar;

        setLayout(new BorderLayout());
        setBackground(Tema.FONDO);

        add(crearTitulo(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);

        cargarTabla();
    }

    private JLabel crearTitulo() {
        JLabel titulo = new JLabel(AppInfo.MODULO, SwingConstants.CENTER);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        titulo.setBackground(Tema.PRIMARIO);
        titulo.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        return titulo;
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(20, 0));
        cuerpo.setOpaque(false);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        cuerpo.add(crearFormulario(), BorderLayout.WEST);
        cuerpo.add(crearTabla(), BorderLayout.CENTER);
        return cuerpo;
    }

    private JPanel crearFormulario() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Registro de estado"),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 4, 6, 4);
        c.anchor = GridBagConstraints.WEST;

        agregarCampo(formulario, c, 0, "Clave (ej. JAL):", txtClave);
        agregarCampo(formulario, c, 1, "Nombre del estado:", txtNombre);
        agregarCampo(formulario, c, 2, "Capital:", txtCapital);

        // Enter en cualquier campo equivale a presionar Guardar
        txtClave.addActionListener(e -> guardar());
        txtNombre.addActionListener(e -> guardar());
        txtCapital.addActionListener(e -> guardar());

        JLabel ayuda = new JLabel("Archivo de datos: " + service.getArchivo().getFileName());
        ayuda.setToolTipText(service.getArchivo().toString());
        ayuda.setForeground(Color.GRAY);
        ayuda.setPreferredSize(new Dimension(300, 30));
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTHWEST;
        formulario.add(ayuda, c);

        return formulario;
    }

    private static void agregarCampo(JPanel panel, GridBagConstraints c, int fila, String texto, JComponent campo) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setLabelFor(campo);
        c.gridx = 0;
        c.gridy = fila;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        panel.add(etiqueta, c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, c);
    }

    private JScrollPane crearTabla() {
        JTable tabla = new JTable(modeloTabla);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Estados registrados"));
        return scroll;
    }

    private JPanel crearBotones() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        botones.setBackground(Tema.PRIMARIO_OSCURO);

        JButton btnRegresar = new JButton("Regresar al menú principal");
        btnRegresar.addActionListener(e -> {
            limpiar();
            alRegresar.run();
        });

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiar());

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(btnGuardar.getFont().deriveFont(Font.BOLD));
        btnGuardar.addActionListener(e -> guardar());

        botones.add(btnRegresar);
        botones.add(btnLimpiar);
        botones.add(btnGuardar);
        return botones;
    }

    /** Acción del botón Guardar. */
    private void guardar() {
        try {
            Estado estado = service.registrar(txtClave.getText(), txtNombre.getText(), txtCapital.getText());
            modeloTabla.addRow(new Object[] {estado.clave(), estado.nombre(), estado.capital()});
            JOptionPane.showMessageDialog(
                    this,
                    "Estado guardado correctamente:\n\n" + estado.descripcion()
                            + "\n\nArchivo: " + service.getArchivo(),
                    "Registro guardado",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiar();
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            enfocarPrimerCampoVacio();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar en el archivo:\n" + ex.getMessage(),
                    "Error de archivo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Estado> estados = service.listar();
            for (Estado estado : estados) {
                modeloTabla.addRow(new Object[] {estado.clave(), estado.nombre(), estado.capital()});
            }
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo leer el archivo de estados:\n" + ex.getMessage(),
                    "Error de archivo",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiar() {
        txtClave.setText("");
        txtNombre.setText("");
        txtCapital.setText("");
        txtClave.requestFocusInWindow();
    }

    private void enfocarPrimerCampoVacio() {
        for (JTextField campo : new JTextField[] {txtClave, txtNombre, txtCapital}) {
            if (campo.getText().isBlank()) {
                campo.requestFocusInWindow();
                return;
            }
        }
    }
}
