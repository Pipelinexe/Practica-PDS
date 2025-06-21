package views;

import models.Estudiante;

import javax.persistence.*;
import javax.swing.*;
import java.awt.*;

/** Ventana de registro de nuevos estudiantes. */
public class RegisterWindow extends JFrame {

    private final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("flashcards-jpa");
    private final EntityManager em = emf.createEntityManager();

    private JTextField nombreField;
    private JTextField emailField;
    private JPasswordField passField;

    public RegisterWindow() {
        initUI();
    }

    /* ───────── UI ───────── */

    private void initUI() {
        setTitle("Registro");
        setSize(380, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8,8,8,8); g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; p.add(new JLabel("Nombre:"), g);
        nombreField = new JTextField(22); g.gridx=1;
        p.add(nombreField, g);

        g.gridx=0; g.gridy=1; p.add(new JLabel("Correo:"), g);
        emailField  = new JTextField(22); g.gridx=1;
        p.add(emailField, g);

        g.gridx=0; g.gridy=2; p.add(new JLabel("Contraseña:"), g);
        passField   = new JPasswordField(22); g.gridx=1;
        p.add(passField, g);

        JButton btnRegistrar = new JButton("Registrarse");
        btnRegistrar.addActionListener(e -> registrar());

        g.gridx=0; g.gridy=3; g.gridwidth=2; g.anchor = GridBagConstraints.CENTER;
        p.add(btnRegistrar, g);

        setContentPane(p);
    }

    /* ───────── lógica ───────── */

    private void registrar() {
        String nombre = nombreField.getText().trim();
        String mail   = emailField.getText().trim();
        String pwd    = new String(passField.getPassword()).trim();

        if (nombre.isEmpty() || mail.isEmpty() || pwd.isEmpty()) {
            msg("Todos los campos son obligatorios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ¿Existe ya el email?
        boolean existe = !em.createQuery("""
                    SELECT e FROM Estudiante e WHERE e.email = :mail
                """, Estudiante.class)
                .setParameter("mail", mail)
                .getResultList().isEmpty();

        if (existe) {
            msg("Ya existe un usuario con ese email", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            em.getTransaction().begin();
            em.persist(new Estudiante(nombre, mail, pwd));
            em.getTransaction().commit();

            msg("Registro completado", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            msg("Error al registrar: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void msg(String m, int t) {
        JOptionPane.showMessageDialog(this, m, "Aviso", t);
    }

    @Override public void dispose() {
        if (em.isOpen()) em.close(); emf.close(); super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterWindow().setVisible(true));
    }
}
