package mx.golondrina.ferreteria.DPO3_U1_EA_ERVV;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import mx.golondrina.ferreteria.DPO3_U1_EA_ERVV.ui.VentanaPrincipal;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FerreteriaApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(FerreteriaApplication.class)
				.headless(false)
				.run(args);

		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				// Si falla, Swing usa su apariencia por defecto
			}
			context.getBean(VentanaPrincipal.class).setVisible(true);
		});
	}
}