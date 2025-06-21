package views;

import dao.ProgresoEstudianteDAO;
import models.Curso;
import models.Estudiante;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** Ventana con la clasificación de un curso por porcentaje de acierto. */
public class RankingWindow extends JFrame {

    public RankingWindow(Curso curso) {
        setTitle("Ranking • " + curso.getNombre());
        setSize(500, 420);
        setLocationRelativeTo(null);

        ProgresoEstudianteDAO dao = new ProgresoEstudianteDAO();
        List<Object[]> filas = dao.rankingPorCurso(curso);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Pos.", "Alumno", "% Acierto"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        int pos = 1;
        for (Object[] f : filas) {
            Estudiante e = (Estudiante) f[0];
            double pct  = (Double) f[1];
            model.addRow(new Object[]{pos++, e.getNombre(),
                                       String.format("%.1f%%", pct)});
        }

        JTable tabla = new JTable(model);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
}
