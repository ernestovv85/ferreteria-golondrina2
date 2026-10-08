package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.model.Estado;
import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.service.EstadoService;
import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.service.ValidacionException;

public class DialogoModificar extends JDialog {

    private static final long serialVersionUID = 1L;

    private static final Border BORDE_ERROR = BorderFactory.createLineBorder(new Color(0xC6, 0x28, 0x28), 2);

    private final transient EstadoService service;
    private final transient Estado original;

    private final JTextField txtClave = new JTextField(6);
    private final JTextField txtNombre = new JTextField(22);
    private final JTextField txtCapital = new JTextField(22);
    private final Border bordeNormal = txtNombre.getBorder();

    /** Estado con los datos ya guardados; queda en null si se cancela. */
    private transient Estado resultado;

    public DialogoModificar(Window propietario, Estado original, EstadoService service) {
        super(propietario, "Modificar estado", ModalityType.APPLICATION_MODAL);
        this.service = service;
        this.original = original;

        txtClave.setText(original.clave());
        txtNombre.setText(original.nombre());
        txtCapital.setText(original.capital());

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Tema.FONDO);
        contenido.add(crearTitulo(), BorderLayout.NORTH);
        contenido.add(crearFormulario(), BorderLayout.CENTER);
        contenido.add(crearBotones(), BorderLayout.SOUTH);
        setContentPane(contenido);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(propietario);
    }

    /**
     * Muestra la ventana y espera a que el usuario la cierre.
     *
     * @return el estado modificado si se guardaron los cambios; vacío si se canceló
     */
    public Optional<Estado> mostrar() {
        setVisible(true); // al ser modal, aquí se detiene hasta que la ventana se cierra
        return Optional.ofNullable(resultado);
    }

    private JLabel crearTitulo() {
        JLabel titulo = new JLabel("Modificar " + original.nombre() + " (" + original.clave() + ")",
                SwingConstants.CENTER);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        titulo.setOpaque(true);
        titulo.setBackground(Tema.PRIMARIO);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        return titulo;
    }

    private JPanel crearFormulario() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 6, 12),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Datos del estado"),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10))));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 4, 6, 4);
        c.anchor = GridBagConstraints.WEST;

        agregarCampo(formulario, c, 0, "Clave (ej. JAL):", txtClave);
        agregarCampo(formulario, c, 1, "Nombre del estado:", txtNombre);
        agregarCampo(formulario, c, 2, "Capital:", txtCapital);
        return formulario;
    }

    private static void agregarCampo(JPanel panel, GridBagConstraints c, int fila, String texto, JComponent campo) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setLabelFor(campo);
        c.gridx = 0;
        c.gridy = fila;
        c.fill = GridBagConstraints.NONE;
        panel.add(etiqueta, c);

        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, c);
    }

    private JPanel crearBotones() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botones.setBackground(Tema.PRIMARIO_OSCURO);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        JButton btnGuardar = new JButton("Guardar cambios");
        btnGuardar.setFont(btnGuardar.getFont().deriveFont(Font.BOLD));
        btnGuardar.addActionListener(e -> guardar());

        // Enter = Guardar cambios, Esc = Cancelar
        getRootPane().setDefaultButton(btnGuardar);
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        botones.add(btnCancelar);
        botones.add(btnGuardar);
        return botones;
    }

    /** Acción del botón Guardar cambios. */
    private void guardar() {
        if (!validarCamposVacios()) {
            return;
        }
        try {
            resultado = service.modificar(
                    original.clave(), txtClave.getText(), txtNombre.getText(), txtCapital.getText());
            dispose();
        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo guardar en el archivo:\n" + ex.getMessage(),
                    "Error de archivo",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCamposVacios() {
        JTextField[] campos = {txtClave, txtNombre, txtCapital};
        String[] nombres = {"Clave", "Nombre del estado", "Capital"};

        List<String> vacios = new ArrayList<>();
        JTextField primeroVacio = null;
        for (int i = 0; i < campos.length; i++) {
            boolean vacio = campos[i].getText().isBlank();
            campos[i].setBorder(vacio ? BORDE_ERROR : bordeNormal);
            if (vacio) {
                vacios.add(nombres[i]);
                if (primeroVacio == null) {
                    primeroVacio = campos[i];
                }
            }
        }

        if (vacios.isEmpty()) {
            return true;
        }
        JOptionPane.showMessageDialog(
                this,
                "Los siguientes campos no pueden estar vacíos:\n• " + String.join("\n• ", vacios),
                "Campos vacíos",
                JOptionPane.ERROR_MESSAGE);
        primeroVacio.requestFocusInWindow();
        return false;
    }
}