package views;

import dao.CursoDAO;
import models.Curso;
import models.Curso.TipoEstrategia;
import models.Estudiante;
import models.Flashcard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana para crear o editar un curso,
 * permitiendo elegir la estrategia de aprendizaje.
 */
public class CrearCursoWindow extends JFrame {

    private final CursoDAO  cursoDAO   = new CursoDAO();
    private final Curso     cursoExist;        // null ⇒ nuevo
    private final Estudiante creador;

    private JTextField nombreField;
    private JTextArea  descripcionArea;
    private JComboBox<TipoEstrategia> estrategiaCombo;
    private DefaultListModel<Flashcard> modelFlash;

    public CrearCursoWindow(Curso curso, Estudiante creador) {
        this.cursoExist = curso;
        this.creador    = creador;
        initUI();
        if (cursoExist != null) cargarCurso();
    }

    /* ───────────────────────────────────────── UI ───────────────────────────────────────── */

    private void initUI() {
        setTitle(cursoExist == null ? "Crear Curso" : "Editar Curso");
        setSize(760, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        /* Panel de datos */
        JPanel datos = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0;
        datos.add(new JLabel("Nombre:"), g);
        nombreField = new JTextField(28);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        datos.add(nombreField, g);

        g.gridx = 0; g.gridy = 1; g.fill = GridBagConstraints.NONE;
        datos.add(new JLabel("Descripción:"), g);
        descripcionArea = new JTextArea(3, 28);
        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);
        g.gridx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        datos.add(new JScrollPane(descripcionArea), g);

        g.gridx = 0; g.gridy = 2; g.fill = GridBagConstraints.NONE;
        datos.add(new JLabel("Estrategia:"), g);
        estrategiaCombo = new JComboBox<>(TipoEstrategia.values());
        g.gridx = 1;
        datos.add(estrategiaCombo, g);

        /* Lista de flashcards */
        modelFlash = new DefaultListModel<>();
        JList<Flashcard> lista = new JList<>(modelFlash);
        lista.setCellRenderer((l, v, i, s, f) -> {
            JLabel lab = (JLabel) new DefaultListCellRenderer()
                    .getListCellRendererComponent(l, v, i, s, f);
            lab.setText(((Flashcard) v).getPregunta());
            return lab;
        });

        /* Botones inferiores */
        JButton btnAdd = new JButton("Añadir");
        btnAdd.addActionListener(e ->
                new CrearFlashcardWindow(modelFlash::addElement).setVisible(true));

        JButton btnDel = new JButton("Eliminar");
        btnDel.addActionListener(e -> {
            int idx = lista.getSelectedIndex();
            if (idx >= 0) modelFlash.remove(idx);
        });

        JButton btnOK = new JButton("Guardar curso");
        btnOK.addActionListener(e -> guardarCurso());

        JPanel pnlBot = new JPanel();
        pnlBot.add(btnAdd);
        pnlBot.add(btnDel);
        pnlBot.add(btnOK);

        /* Root layout */
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        root.add(datos, BorderLayout.NORTH);
        root.add(new JScrollPane(lista), BorderLayout.CENTER);
        root.add(pnlBot, BorderLayout.SOUTH);

        setContentPane(root);
    }

    /* ─────────────────────────────── helpers ─────────────────────────────── */

    private void cargarCurso() {
        nombreField.setText(cursoExist.getNombre());
        descripcionArea.setText(cursoExist.getDescripcion());
        estrategiaCombo.setSelectedItem(cursoExist.getTipoEstrategia());
        cursoExist.getFlashcards().forEach(modelFlash::addElement);
    }

    private void guardarCurso() {
        String nombre = nombreField.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio");
            return;
        }
        if (modelFlash.isEmpty()) {
            mostrarError("Añade al menos una flashcard");
            return;
        }

        /* recopilar flashcards */
        List<Flashcard> fc = new ArrayList<>();
        for (int i = 0; i < modelFlash.size(); i++) fc.add(modelFlash.get(i));

        try {
            if (cursoExist == null) {
                Curso nuevo = new Curso(
                        nombre,
                        descripcionArea.getText().trim(),
                        (TipoEstrategia) estrategiaCombo.getSelectedItem(),
                        fc,
                        creador);
                cursoDAO.guardar(nuevo);
            } else {
                cursoExist.setNombre(nombre);
                cursoExist.setDescripcion(descripcionArea.getText().trim());
                cursoExist.setTipoEstrategia(
                        (TipoEstrategia) estrategiaCombo.getSelectedItem());
                cursoExist.getFlashcards().clear();
                cursoExist.getFlashcards().addAll(fc);
                cursoDAO.guardar(cursoExist);
            }
            mostrarInfo("Curso guardado correctamente");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al guardar el curso: " + ex.getMessage());
        }
    }

    /* ────────────────────────── diálogos utilitarios ───────────────────────── */

    private void mostrarInfo(String m)  { JOptionPane.showMessageDialog(this, m, "Info",   JOptionPane.INFORMATION_MESSAGE); }
    private void mostrarError(String m) { JOptionPane.showMessageDialog(this, m, "Error",  JOptionPane.ERROR_MESSAGE);       }
}
