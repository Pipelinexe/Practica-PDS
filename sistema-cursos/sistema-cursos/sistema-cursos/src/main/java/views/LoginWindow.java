package views;

import models.Estudiante;

import javax.persistence.*;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana de inicio de sesión / registro.
 */
public class LoginWindow extends JFrame {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("flashcards-jpa");
    private final EntityManager em      = emf.createEntityManager();

    private JTextField emailField;
    private JPasswordField passField;

    public LoginWindow() {
        initUI();
    }

    /* ─────────────────── UI ─────────────────── */

    private void initUI() {
        setTitle("Iniciar sesión");
        setSize(380, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0;
        root.add(new JLabel("Correo:"), g);
        emailField = new JTextField(22);
        g.gridx = 1;
        root.add(emailField, g);

        g.gridx = 0; g.gridy = 1;
        root.add(new JLabel("Contraseña:"), g);
        passField = new JPasswordField(22);
        g.gridx = 1;
        root.add(passField, g);

        JButton btnLogin  = new JButton("Iniciar sesión");
        JButton btnReg    = new JButton("Registrarse");

        btnLogin.addActionListener(e -> autenticar());
        btnReg  .addActionListener(e -> new RegisterWindow().setVisible(true));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        botones.add(btnLogin); botones.add(btnReg);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        g.anchor = GridBagConstraints.CENTER;
        root.add(botones, g);

        setContentPane(root);
    }

    /* ─────────────────── lógica ─────────────────── */

    private void autenticar() {
        String email = emailField.getText().trim();
        String pwd   = new String(passField.getPassword()).trim();

        if (email.isEmpty() || pwd.isEmpty()) {
            msg("Introduce email y contraseña", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            TypedQuery<Estudiante> q = em.createQuery("""
                    SELECT e FROM Estudiante e
                    WHERE e.email = :mail AND e.password = :pwd
                    """, Estudiante.class);
            q.setParameter("mail", email);
            q.setParameter("pwd",  pwd);

            Estudiante est = q.getResultStream().findFirst().orElse(null);
            if (est == null) {
                msg("Credenciales incorrectas", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Login OK → abrir menú principal
            new MainMenuWindow(est).setVisible(true);
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            msg("Error al conectar con la base de datos:\n" + ex.getMessage(),
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /* ─────────────────── util ─────────────────── */

    private void msg(String m, int t) {
        JOptionPane.showMessageDialog(this, m, "Aviso", t);
    }

    @Override public void dispose() {
        if (em.isOpen()) em.close();
        emf.close();
        super.dispose();
    }

    /* ──────────────── main standalone ──────────────── */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginWindow().setVisible(true));
    }
}
