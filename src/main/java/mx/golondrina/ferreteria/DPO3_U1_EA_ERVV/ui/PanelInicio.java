package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PanelInicio extends JPanel {

    private static final long serialVersionUID = 1L;

    public PanelInicio() {
        setLayout(new BorderLayout());
        setBackground(Tema.FONDO);

        add(crearEncabezado(), BorderLayout.NORTH);
        add(new PanelImagen(cargarImagen(AppInfo.RUTA_IMAGEN_INICIO)), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Tema.PRIMARIO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel empresa = new JLabel(AppInfo.EMPRESA, SwingConstants.CENTER);
        empresa.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        empresa.setForeground(Color.WHITE);

        JLabel modulo = new JLabel(AppInfo.MODULO + " · " + AppInfo.ETAPA, SwingConstants.CENTER);
        modulo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        modulo.setForeground(Tema.ACENTO);

        encabezado.add(empresa, BorderLayout.CENTER);
        encabezado.add(modulo, BorderLayout.SOUTH);
        return encabezado;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(Tema.PRIMARIO_OSCURO);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));

        JLabel instruccion = new JLabel("Selecciona Catálogos > Estados para comenzar");
        instruccion.setForeground(Color.WHITE);

        JLabel alumno = new JLabel(AppInfo.ALUMNO);
        alumno.setForeground(Tema.ACENTO);

        pie.add(instruccion, BorderLayout.WEST);
        pie.add(alumno, BorderLayout.EAST);
        return pie;
    }

    private static Image cargarImagen(String ruta) {
        URL url = PanelInicio.class.getResource(ruta);
        return url == null ? null : new ImageIcon(url).getImage();
    }

    /**
     * Dibuja la imagen escalada al tamaño del panel sin deformarla.
     */
    private static class PanelImagen extends JPanel {

        private static final long serialVersionUID = 1L;
        private static final int MARGEN = 24;

        private final transient Image imagen;

        PanelImagen(Image imagen) {
            this.imagen = imagen;
            setBackground(Tema.FONDO);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int anchoDisponible = getWidth() - MARGEN * 2;
            int altoDisponible = getHeight() - MARGEN * 2;

            if (imagen == null || imagen.getWidth(this) <= 0) {
                g2.setColor(Tema.PRIMARIO);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
                String aviso = "Imagen no encontrada: " + AppInfo.RUTA_IMAGEN_INICIO;
                int x = (getWidth() - g2.getFontMetrics().stringWidth(aviso)) / 2;
                g2.drawString(aviso, Math.max(x, MARGEN), getHeight() / 2);
            } else {
                double escala = Math.min(
                        (double) anchoDisponible / imagen.getWidth(this),
                        (double) altoDisponible / imagen.getHeight(this));
                int ancho = (int) (imagen.getWidth(this) * escala);
                int alto = (int) (imagen.getHeight(this) * escala);
                int x = (getWidth() - ancho) / 2;
                int y = (getHeight() - alto) / 2;
                g2.drawImage(imagen, x, y, ancho, alto, this);
            }
            g2.dispose();
        }
    }
}

