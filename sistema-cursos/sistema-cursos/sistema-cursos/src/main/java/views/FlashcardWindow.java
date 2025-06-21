package views;

import models.Flashcard;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Ventana (panel) genérica para practicar una única flashcard.
 * <p>
 *  • Soporta los cuatro tipos definidos en {@link models.Flashcard.TipoFlashcard}.<br>
 *  • Para VERDADERO_FALSO, COMPLETAR_HUECO y MULTIOPCION pinta un botón por opción.<br>
 *  • Para PREGUNTA_ABIERTA muestra un JTextField.<br>
 *  • Tras responder deshabilita la UI, enseña feedback y
 *    <strong>tras 5 segundos</strong> llama al callback {@link #onRespuesta(boolean)}
 *    para que la ventana contenedora cargue la siguiente pregunta.
 */
public abstract class FlashcardWindow extends JPanel {

    /* ──────────────────── atributos de estado ──────────────────── */

    private final Flashcard flashcard;

    private JLabel preguntaLabel;
    private List<JButton> opcionesButtons;
    private JLabel resultadoLabel;
    private JTextField respuestaField;

    private Timer timerNext; // avance automático tras 5 s

    /* ──────────────────── ctor ──────────────────── */

    public FlashcardWindow(Flashcard flashcard) {
        this.flashcard = flashcard;
        this.opcionesButtons = new ArrayList<>();

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        construirCabecera();
        construirCentro();
        construirPie();
    }

    /* ──────────────────── construcción de UI ──────────────────── */

    private void construirCabecera() {
        JPanel preguntaPanel = new JPanel();
        preguntaPanel.setLayout(new BoxLayout(preguntaPanel, BoxLayout.Y_AXIS));
        preguntaPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        preguntaLabel = new JLabel(flashcard.getPregunta());
        preguntaLabel.setFont(new Font("Arial", Font.BOLD, 18));
        preguntaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        preguntaPanel.add(preguntaLabel);

        add(preguntaPanel, BorderLayout.NORTH);
    }

    private void construirCentro() {
        JPanel centralPanel = new JPanel();
        centralPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        if (flashcard.getTipo() == Flashcard.TipoFlashcard.PREGUNTA_ABIERTA) {
            centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));

            respuestaField = new JTextField();
            respuestaField.setFont(new Font("Arial", Font.PLAIN, 16));
            respuestaField.setMaximumSize(new Dimension(400, 30));
            respuestaField.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton responderButton = new JButton("Responder");
            responderButton.setFont(new Font("Arial", Font.PLAIN, 14));
            responderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            responderButton.setMaximumSize(new Dimension(200, 35));
            responderButton.addActionListener(e -> verificarRespuesta(respuestaField.getText()));

            centralPanel.add(Box.createVerticalGlue());
            centralPanel.add(respuestaField);
            centralPanel.add(Box.createVerticalStrut(15));
            centralPanel.add(responderButton);
            centralPanel.add(Box.createVerticalGlue());
        } else {
            centralPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 0, 5, 0);

            /* obtenemos y barajamos todas las opciones */
            List<String> opciones = flashcard.getOpcionesBarajadas();

            for (String opcion : opciones) {
                JButton opcionButton = createOptionButton(opcion);
                opcionesButtons.add(opcionButton);
                centralPanel.add(opcionButton, gbc);
            }
        }
        add(centralPanel, BorderLayout.CENTER);
    }

    private void construirPie() {
        resultadoLabel = new JLabel(" ");
        resultadoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultadoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resultadoLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(resultadoLabel, BorderLayout.SOUTH);
    }

    /* ──────────────────── helpers ──────────────────── */

    private JButton createOptionButton(String opcion) {
        JButton button = new JButton(opcion);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(300, 40));
        button.setMinimumSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(400, 40));
        button.setMargin(new Insets(5, 15, 5, 15));
        button.setBackground(new Color(245, 245, 245));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)));

        /* Hover */
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(230, 230, 230));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(245, 245, 245));
            }
        });
        button.addActionListener(e -> verificarRespuesta(opcion));
        return button;
    }

    /* ──────────────────── verificación & temporizador ──────────────────── */

    private void verificarRespuesta(String respuesta) {
        /* deshabilitar controles para evitar doble clic */
        if (flashcard.getTipo() == Flashcard.TipoFlashcard.PREGUNTA_ABIERTA) {
            respuestaField.setEnabled(false);
        } else {
            opcionesButtons.forEach(b -> b.setEnabled(false));
        }

        boolean correcta = flashcard.esRespuestaCorrecta(respuesta);
        pintarFeedback(correcta, respuesta);

        /* programa notificación tras 5 segundos */
        if (timerNext != null && timerNext.isRunning()) timerNext.stop();
        timerNext = new Timer(5000, e -> onRespuesta(correcta));
        timerNext.setRepeats(false);
        timerNext.start();
    }

    private void pintarFeedback(boolean correcta, String respuestaDada) {
        if (correcta) {
            resultadoLabel.setText("¡Correcto!");
            resultadoLabel.setForeground(new Color(0, 128, 0));
        } else {
            resultadoLabel.setText("Incorrecto. La respuesta correcta era: " +
                    flashcard.getRespuestaCorrecta());
            resultadoLabel.setForeground(new Color(220, 20, 60));
        }

        /* colorea botones si los hay */
        if (flashcard.getTipo() != Flashcard.TipoFlashcard.PREGUNTA_ABIERTA) {
            for (JButton b : opcionesButtons) {
                if (b.getText().equals(flashcard.getRespuestaCorrecta())) {
                    b.setBackground(new Color(144, 238, 144)); // verde claro
                } else if (!correcta && b.getText().equals(respuestaDada)) {
                    b.setBackground(new Color(255, 182, 193)); // rojo claro
                }
            }
        }
    }

    /* ──────────────────── callback ──────────────────── */

    /**
     * Llamado AUTOMÁTICAMENTE 5 s después de responder.
     * El contenedor debe cargar la siguiente flashcard o cerrar la práctica.
     */
    protected abstract void onRespuesta(boolean correcta);

    /* ──────────────────── test manual ──────────────────── */

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            /* multi-opción de ejemplo */
            List<String> dist = List.of("Lyon", "Marsella", "Toulouse");
            Flashcard f = Flashcard.crearMultiopcion("¿Capital de Francia?", "París", dist);
            FlashcardWindow w = new FlashcardWindow(f) {
                @Override protected void onRespuesta(boolean correcta) {
                    System.out.println("Resultado: " + correcta + ". Cerrando…");
                    SwingUtilities.getWindowAncestor(this).dispose();
                }
            };
            JFrame frame = new JFrame("Test Flashcard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(w);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
