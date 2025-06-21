package views;

import dao.ProgresoEstudianteDAO;
import models.ProgresoEstudiante;
import models.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Estadísticas globales del alumno en todos sus cursos. */
public class GlobalStatsWindow extends JFrame {

    public GlobalStatsWindow(Estudiante est) {
        setTitle("Estadísticas globales – " + est.getNombre());
        setSize(540, 440);
        setLocationRelativeTo(null);

        ProgresoEstudianteDAO dao = new ProgresoEstudianteDAO();
        List<ProgresoEstudiante> lista = dao.buscarPorEstudiante(est);

        int totResp = lista.stream().mapToInt(ProgresoEstudiante::getTotalRespondidas).sum();
        int totOk   = lista.stream().mapToInt(ProgresoEstudiante::getAciertos).sum();
        double pct  = totResp == 0 ? 0 : (totOk * 1.0 / totResp) * 100;

        /* Resumen arriba */
        JPanel top = new JPanel(new BorderLayout(8, 8));
        JLabel lbl = new JLabel(String.format(
                "<html><b>Respondidas: %d</b>  |  "
              + "<b>Aciertos: %d</b>  |  "
              + "<b>Precisión: %.1f%%</b></html>",
                totResp, totOk, pct));
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 14f));

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int) pct);
        bar.setString(String.format("%.1f%%", pct));
        bar.setStringPainted(true);

        top.add(lbl, BorderLayout.WEST);
        top.add(bar, BorderLayout.CENTER);

        /* Tabla por curso */
        DefaultTableModel m = new DefaultTableModel(
                new String[]{"Curso", "Respondidas", "Aciertos",
                             "% Precisión", "% Progreso"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (ProgresoEstudiante p : lista) {
            double prec = p.getTotalRespondidas() == 0 ? 0
                    : (p.getAciertos() * 1.0 / p.getTotalRespondidas()) * 100;
            double prog = p.obtenerPorcentajeProgreso() * 100;
            m.addRow(new Object[]{
                    p.getCurso().getNombre(),
                    p.getTotalRespondidas(),
                    p.getAciertos(),
                    String.format("%.1f%%", prec),
                    String.format("%.1f%%", prog)
            });
        }

        JTable tabla = new JTable(m);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {140, 90, 80, 100, 90};
        for (int i = 0; i < w.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
}
