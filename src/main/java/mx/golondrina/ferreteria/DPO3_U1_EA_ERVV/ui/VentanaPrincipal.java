package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.service.EstadoService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class VentanaPrincipal extends JFrame{
    private static final long serialVersionUID = 1L;

    private static final String VISTA_INICIO = "inicio";
    private static final String VISTA_ESTADOS = "estados";
    private static final String VISTA_CONSULTA = "consulta";

    private final transient ConfigurableApplicationContext context;
    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final PanelConsulta panelConsulta;

    public VentanaPrincipal(ConfigurableApplicationContext context, EstadoService estadoService) {
        super(AppInfo.TITULO_VENTANA);
        this.context = context;

        Runnable irAInicio = () -> tarjetas.show(contenido, VISTA_INICIO);
        panelConsulta = new PanelConsulta(estadoService, irAInicio);

        setContentPane(contenido);
        setJMenuBar(crearMenu());

        contenido.add(new PanelInicio(), VISTA_INICIO);
        contenido.add(new PanelEstados(estadoService, irAInicio), VISTA_ESTADOS);
        contenido.add(panelConsulta, VISTA_CONSULTA);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        setMinimumSize(new Dimension(800, 560));
        setSize(1000, 680);
        setLocationRelativeTo(null);
    }

    private JMenuBar crearMenu() {
        JMenuBar barra = new JMenuBar();

        JMenu archivo = new JMenu("Archivo");
        archivo.setMnemonic(KeyEvent.VK_A);

        JMenuItem salir = new JMenuItem("Salir");
        salir.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        salir.addActionListener(e -> salir());

        archivo.addSeparator();
        archivo.add(salir);

        JMenu catalogos = new JMenu("Catálogos");
        catalogos.setMnemonic(KeyEvent.VK_C);

        JMenuItem estados = new JMenuItem("Estados");
        estados.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
        estados.addActionListener(e -> tarjetas.show(contenido, VISTA_ESTADOS));

        JMenuItem consulta = new JMenuItem("Consulta");
        consulta.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK));
        consulta.addActionListener(e -> mostrarConsulta());

        catalogos.add(estados);
        catalogos.add(consulta);

        barra.add(archivo);
        barra.add(catalogos);
        return barra;
    }

    /** Relee el archivo de texto antes de mostrar la consulta, para que siempre esté al día. */
    private void mostrarConsulta() {
        panelConsulta.cargarDatos();
        tarjetas.show(contenido, VISTA_CONSULTA);
    }

    private void salir() {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas salir de la aplicación?",
                "Salir",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(SpringApplication.exit(context));
        }
    }
}