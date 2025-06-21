package views;

import models.Estudiante;

import javax.swing.*;
import java.awt.*;

/**
 * Menú principal que ve el estudiante tras iniciar sesión.
 * Botones:
 *   • Crear curso
 *   • Ver mis cursos
 *   • Estadísticas globales
 *   • Cerrar sesión
 */
public class MainMenuWindow extends JFrame {

    private static final Dimension BTN_SIZE = new Dimension(200, 38);
    private final Estudiante estudianteActual;

    public MainMenuWindow(Estudiante estudianteActual) {
        this.estudianteActual = estudianteActual;

        setTitle("Sistema de Flashcards – " + estudianteActual.getNombre());
        setSize(420, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        /* Layout vertical */
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        add(content);

        JLabel title = new JLabel("Sistema de Flashcards");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 26f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(25));

        /* Botón "Crear curso" */
        JButton btnCrear = nuevoBoton("Crear Curso", e -> abrirCrearCurso());
        content.add(btnCrear);
        content.add(Box.createVerticalStrut(18));

        /* Botón "Ver Mis Cursos" */
        JButton btnCursos = nuevoBoton("Ver Mis Cursos", e -> abrirMisCursos());
        content.add(btnCursos);
        content.add(Box.createVerticalStrut(18));

        /* Botón "Estadísticas Globales" */
        JButton btnStats = nuevoBoton("Estadísticas Globales", e -> abrirStats());
        content.add(btnStats);
        content.add(Box.createVerticalStrut(18));

        /* Botón "Cerrar Sesión" */
        JButton btnCerrar = nuevoBoton("Cerrar Sesión", e -> cerrarSesion());
        content.add(btnCerrar);
    }

    /* -------- helpers -------- */
    private JButton nuevoBoton(String texto, java.awt.event.ActionListener al) {
        JButton b = new JButton(texto);
        b.setMaximumSize(BTN_SIZE);
        b.setPreferredSize(BTN_SIZE);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.addActionListener(al);
        return b;
    }

    /* -------- acciones -------- */
    private void abrirCrearCurso() {
        new CrearCursoWindow(null, estudianteActual).setVisible(true);
    }

    private void abrirMisCursos() {
        new ListadoCursosWindow(estudianteActual).setVisible(true);
    }

    private void abrirStats() {
        new GlobalStatsWindow(estudianteActual).setVisible(true);
    }

    private void cerrarSesion() {
        dispose();
        new LoginWindow().setVisible(true);
    }

    /* -------- main de prueba -------- */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Estudiante demo = new Estudiante("G", "g@x.com", "123");
            new MainMenuWindow(demo).setVisible(true);
        });
    }
}
