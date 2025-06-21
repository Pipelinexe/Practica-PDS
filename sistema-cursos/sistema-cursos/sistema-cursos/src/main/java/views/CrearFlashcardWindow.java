package views;

import models.Flashcard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Ventana para crear una flashcard de cualquiera de los cuatro tipos
 * (Completar hueco, Verdadero/Falso, Pregunta abierta, Opción múltiple).
 * Compatible con Java 11: usa switch clásico.
 */
public class CrearFlashcardWindow extends JFrame {

    /* ───────── Atributos de UI ───────── */

    private JComboBox<String> tipoFlashcardCombo;
    private JPanel contenidoPanel;

    private JTextField preguntaField;
    private JTextField respuestaCorrectaField;

    private final List<JTextField> opcionesIncorrectasFields = new ArrayList<>();
    private List<JTextField> distractorFields;   // sólo para multi‑opción

    private JRadioButton verdaderoRadio;
    private JRadioButton falsoRadio;

    private final Consumer<Flashcard> onFlashcardCreada;

    /* ───────── Constructores ───────── */

    public CrearFlashcardWindow(Consumer<Flashcard> onFlashcardCreada) {
        this(1, 1, onFlashcardCreada);
    }

    public CrearFlashcardWindow(int numeroActual, int total,
                                Consumer<Flashcard> onFlashcardCreada) {
        this.onFlashcardCreada = onFlashcardCreada;

        setTitle("Crear Flashcard " + numeroActual + " de " + total);
        setSize(600, 470);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        /* selector tipo */
        JPanel tipoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipoPanel.add(new JLabel("Tipo de flashcard:"));
        tipoFlashcardCombo = new JComboBox<>(new String[]{
                "Completar hueco",
                "Verdadero/Falso",
                "Pregunta abierta",
                "Opción múltiple"
        });
        tipoFlashcardCombo.addActionListener(e -> actualizarTipoFlashcard());
        tipoPanel.add(tipoFlashcardCombo);
        main.add(tipoPanel);

        /* panel dinámico */
        contenidoPanel = new JPanel();
        contenidoPanel.setLayout(new BoxLayout(contenidoPanel, BoxLayout.Y_AXIS));
        main.add(contenidoPanel);

        /* botón crear */
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton crearBtn = new JButton("Crear Flashcard");
        crearBtn.addActionListener(e -> crearFlashcard());
        btnPanel.add(crearBtn);
        main.add(Box.createVerticalGlue());
        main.add(btnPanel);

        add(main);
        actualizarTipoFlashcard();
    }

    /* ───────── Cambio de tipo ───────── */

    private void actualizarTipoFlashcard() {
        contenidoPanel.removeAll();
        opcionesIncorrectasFields.clear();
        distractorFields = null;

        switch (tipoFlashcardCombo.getSelectedIndex()) {
            case 0:
                mostrarPanelCompletarHueco();
                break;
            case 1:
                mostrarPanelVerdaderoFalso();
                break;
            case 2:
                mostrarPanelPreguntaAbierta();
                break;
            case 3:
                mostrarPanelMultiopcion();
                break;
            default:
                break;
        }
        contenidoPanel.revalidate();
        contenidoPanel.repaint();
    }

    /* ───────── Paneles por tipo ───────── */

    private void mostrarPanelCompletarHueco() {
        JPanel oracion = new JPanel(new BorderLayout(5, 5));
        oracion.add(new JLabel("Oración (usa '_' para el hueco):"), BorderLayout.NORTH);
        preguntaField = new JTextField();
        oracion.add(preguntaField, BorderLayout.CENTER);
        contenidoPanel.add(oracion);
        contenidoPanel.add(Box.createVerticalStrut(10));

        JPanel respuesta = new JPanel(new BorderLayout(5, 5));
        respuesta.add(new JLabel("Respuesta correcta:"), BorderLayout.NORTH);
        respuestaCorrectaField = new JTextField();
        respuesta.add(respuestaCorrectaField, BorderLayout.CENTER);
        contenidoPanel.add(respuesta);
        contenidoPanel.add(Box.createVerticalStrut(10));

        JPanel opc = new JPanel();
        opc.setLayout(new BoxLayout(opc, BoxLayout.Y_AXIS));
        opc.setBorder(BorderFactory.createTitledBorder("Opciones incorrectas"));
        for (int i = 0; i < 2; i++) {
            JPanel fila = new JPanel(new BorderLayout(5, 5));
            JTextField tf = new JTextField();
            opcionesIncorrectasFields.add(tf);
            fila.add(new JLabel("Opción " + (i + 1) + ":"), BorderLayout.WEST);
            fila.add(tf, BorderLayout.CENTER);
            opc.add(fila);
            opc.add(Box.createVerticalStrut(5));
        }
        contenidoPanel.add(opc);
    }

    private void mostrarPanelVerdaderoFalso() {
        JPanel afirm = new JPanel(new BorderLayout(5, 5));
        afirm.add(new JLabel("Afirmación:"), BorderLayout.NORTH);
        preguntaField = new JTextField();
        afirm.add(preguntaField, BorderLayout.CENTER);
        contenidoPanel.add(afirm);
        contenidoPanel.add(Box.createVerticalStrut(10));

        JPanel resp = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resp.setBorder(BorderFactory.createTitledBorder("La afirmación es:"));
        verdaderoRadio = new JRadioButton("Verdadera");
        falsoRadio = new JRadioButton("Falsa");
        ButtonGroup g = new ButtonGroup();
        g.add(verdaderoRadio);
        g.add(falsoRadio);
        verdaderoRadio.setSelected(true);
        resp.add(verdaderoRadio);
        resp.add(falsoRadio);
        contenidoPanel.add(resp);
    }

    private void mostrarPanelPreguntaAbierta() {
        JPanel preg = new JPanel(new BorderLayout(5, 5));
        preg.add(new JLabel("Pregunta:"), BorderLayout.NORTH);
        preguntaField = new JTextField();
        preg.add(preguntaField, BorderLayout.CENTER);
        contenidoPanel.add(preg);
        contenidoPanel.add(Box.createVerticalStrut(10));

        JPanel resp = new JPanel(new BorderLayout(5, 5));
        resp.add(new JLabel("Respuesta correcta:"), BorderLayout.NORTH);
        respuestaCorrectaField = new JTextField();
        resp.add(respuestaCorrectaField, BorderLayout.CENTER);
        contenidoPanel.add(resp);
    }

    private void mostrarPanelMultiopcion() {
        distractorFields = new ArrayList<>();

        JPanel pPanel = new JPanel(new BorderLayout(5, 5));
        pPanel.add(new JLabel("Pregunta:"), BorderLayout.NORTH);
        preguntaField = new JTextField();
        pPanel.add(preguntaField, BorderLayout.CENTER);
        contenidoPanel.add(pPanel);
        contenidoPanel.add(Box.createVerticalStrut(10));

        JPanel rcPanel = new JPanel(new BorderLayout(5, 5));
        rcPanel.add(new JLabel("Respuesta correcta:"), BorderLayout.NORTH);
        respuestaCorrectaField = new JTextField();
        rcPanel.add(respuestaCorrectaField, BorderLayout.CENTER);
        contenidoPanel.add(rcPanel);
        contenidoPanel.add(Box.createVerticalStrut(10));

        JPanel distPanel = new JPanel();
        distPanel.setLayout(new BoxLayout(distPanel, BoxLayout.Y_AXIS));
        distPanel.setBorder(BorderFactory.createTitledBorder("Distractores (mínimo 3)"));
        for (int i = 0; i < 3; i++) {
            JPanel fila = new JPanel(new BorderLayout(5, 5));
            JTextField tf = new JTextField();
            distractorFields.add(tf);
            fila.add(new JLabel("Opción " + (i + 1) + ":"), BorderLayout.WEST);
            fila.add(tf, BorderLayout.CENTER);
            distPanel.add(fila);
            distPanel.add(Box.createVerticalStrut(5));
        }
        contenidoPanel.add(distPanel);
    }

    /* ───────── Crear flashcard ───────── */

    private void crearFlashcard() {
        String pregunta = preguntaField.getText().trim();
        if (pregunta.isEmpty()) {
            mostrarError("Ingrese la pregunta o afirmación.");
            return;
        }

        Flashcard flashcard = null;
        int tipo = tipoFlashcardCombo.getSelectedIndex();

        switch (tipo) {
            case 0: // Completar hueco
                if (!validarCompletarHueco()) return;
                String respCH = respuestaCorrectaField.getText().trim();
                List<String> inc = new ArrayList<>();
                for (JTextField f : opcionesIncorrectasFields) inc.add(f.getText().trim());
                flashcard = Flashcard.crearCompletarHueco(pregunta, respCH, inc);
                break;
            case 1: // V/F
                flashcard = Flashcard.crearVerdaderoFalso(pregunta, verdaderoRadio.isSelected());
                break;
            case 2: // Abierta
                String resp = respuestaCorrectaField.getText().trim();
                if (resp.isEmpty()) {
                    mostrarError("Ingrese la respuesta correcta.");
                    return;
                }
                flashcard = Flashcard.crearPreguntaAbierta(pregunta, resp);
                break;
            case 3: // Multi-opción
                if (!validarMultiopcion()) return;
                String correcta = respuestaCorrectaField.getText().trim();
                List<String> dist = new ArrayList<>();
                for (JTextField f : distractorFields) dist.add(f.getText().trim());
                flashcard = Flashcard.crearMultiopcion(pregunta, correcta, dist);
                break;
            default:
                break;
        }

        if (flashcard != null) {
            onFlashcardCreada.accept(flashcard);
            dispose();
        }
    }

    /* ───────── Validaciones ───────── */

    private boolean validarCompletarHueco() {
        if (!preguntaField.getText().contains("_")) {
            mostrarError("La oración debe contener un '_' para el hueco.");
            return false;
        }
        if (respuestaCorrectaField.getText().trim().isEmpty()) {
            mostrarError("Ingrese la respuesta correcta.");
            return false;
        }
        long n = opcionesIncorrectasFields.stream()
                .filter(f -> !f.getText().trim().isEmpty()).count();
        if (n < 2) {
            mostrarError("Ingrese al menos dos opciones incorrectas.");
            return false;
        }
        return true;
    }

    private boolean validarMultiopcion() {
        if (respuestaCorrectaField.getText().trim().isEmpty()) {
            mostrarError("Ingrese la respuesta correcta.");
            return false;
        }
        long n = distractorFields.stream()
                .filter(f -> !f.getText().trim().isEmpty()).count();
        if (n < 3) {
            mostrarError("Ingrese al menos tres distractores.");
            return false;
        }
        return true;
    }

    /* ───────── Util ───────── */

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
