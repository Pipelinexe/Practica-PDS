package views;

import dao.ProgresoEstudianteDAO;
import estrategias.*;
import models.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/** Ejecuta la práctica de un curso usando la estrategia elegida. */
public class PracticaWindow extends JFrame {

    private final Curso curso;
    private final Estudiante estudiante;
    private final ProgresoEstudianteDAO progDAO = new ProgresoEstudianteDAO();
    private final ProgresoEstudiante progreso;
    private final EstrategiaAprendizaje estrategia;   // ← final ya inicializado

    private final JPanel contenedor = new JPanel(new BorderLayout());
    private final JProgressBar barra = new JProgressBar(0, 100);
    private final JLabel lblEstado  = new JLabel();

    public PracticaWindow(Curso curso, Estudiante est) {
        this.curso = curso;
        this.estudiante = est;

        /* recuperar o crear progreso */
        progreso = progDAO.buscarPorEstudianteYCurso(est, curso);
        if (progreso == null) progDAO.guardar(new ProgresoEstudiante(est, curso));

        /* instanciar estrategia **antes** de posibles returns */
        estrategia = switch (curso.getTipoEstrategia()) {
            case REPETICION -> new EstrategiaRepeticion();
            default         -> new EstrategiaSecuencial();
        };

        /* ¿curso ya completado?  Ofrecer reiniciar */
        if (progreso.obtenerPorcentajeProgreso() == 1.0) {
            int op = JOptionPane.showConfirmDialog(null,
                    "Ya completaste este curso.\n¿Quieres reiniciar el progreso y practicar de nuevo?",
                    "Repetir curso", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                progreso.reset(); progDAO.guardar(progreso);
            } else {
                dispose();                         // ← estrategia ya asignada
                return;
            }
        }

        initUI();

        /* inicializar estrategia una sola vez */
        List<Flashcard> restantes = curso.getFlashcards().stream()
                .filter(f -> !progreso.getFlashcardsCompletadas().contains(f.getId()))
                .collect(Collectors.toList());
        estrategia.inicializar(restantes);

        mostrarSiguiente();
    }

    /* ───────── UI y flujo idénticos ───────── */

    private void initUI() {
        setTitle("Practicando: "+curso.getNombre());
        setSize(650,430); setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        barra.setStringPainted(true);

        JPanel top=new JPanel(new BorderLayout(8,8));
        top.add(new JLabel(curso.getNombre()), BorderLayout.WEST);
        top.add(barra, BorderLayout.CENTER);
        top.add(lblEstado, BorderLayout.EAST);

        JPanel root=new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        root.add(top,BorderLayout.NORTH);
        root.add(contenedor,BorderLayout.CENTER);
        setContentPane(root);
    }

    private void mostrarSiguiente() {
        if (!estrategia.hayMasFlashcards()) { fin(); return; }

        Flashcard f = estrategia.siguienteFlashcard();
        contenedor.removeAll();
        contenedor.add(new FlashcardWindow(f) {
            @Override protected void onRespuesta(boolean ok) {
                estrategia.registrarRespuesta(f, ok);
                progreso.registrarRespuesta(f, ok); //Se actualiza los datos de uso de ususario
                progDAO.guardar(progreso); //Se guarda las estadisticas de uso del usuario en el sistema
                actualizarBarra();
                new Timer(1500, e -> mostrarSiguiente()) {{ setRepeats(false); }}.start();
            }
        }, BorderLayout.CENTER);
        contenedor.revalidate(); contenedor.repaint();
        actualizarBarra();
    }

    private void actualizarBarra() {
        int pct = (int) (progreso.obtenerPorcentajeProgreso() * 100);
        barra.setValue(pct);
        lblEstado.setText("Aciertos "+progreso.getAciertos()+"/"+progreso.getTotalRespondidas());
    }

    private void fin() {
        JOptionPane.showMessageDialog(this,"¡Curso completado!","Fin",JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    @Override public void dispose() {
        progDAO.cerrar();
        super.dispose();
    }
}
