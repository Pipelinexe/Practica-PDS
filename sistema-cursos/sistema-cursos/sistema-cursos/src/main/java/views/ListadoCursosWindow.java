package views;

import dao.CursoDAO;
import models.Curso;
import models.Estudiante;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ListadoCursosWindow extends JFrame {

    private final Estudiante estudianteActual;
    private final CursoDAO   cursoDAO = new CursoDAO();

    private final DefaultListModel<Curso> model = new DefaultListModel<>();
    private final JList<Curso> cursosList = new JList<>(model);

    private static final Dimension BTN = new Dimension(140,32);

    public ListadoCursosWindow(Estudiante est) {
        this.estudianteActual = est;
        initUI(); cargarCursos();
    }

    private void initUI(){
        setTitle("Mis cursos");
        setSize(540,440); setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        cursosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cursosList.setCellRenderer((l,v,i,s,f)->{
            JLabel lbl=(JLabel)new DefaultListCellRenderer().getListCellRendererComponent(l,v,i,s,f);
            if(v instanceof Curso c) lbl.setText(c.getNombre()+" ("+c.getFlashcards().size()+")");
            return lbl;
        });

        JPanel botones = new JPanel();
        JButton btnPract  = new JButton("Practicar");
        JButton btnEditar = new JButton("Editar");
        JButton btnRank   = new JButton("Ranking");
        JButton btnCerrar = new JButton("Cerrar");

        for (JButton b : new JButton[]{btnPract,btnEditar,btnRank,btnCerrar}) b.setPreferredSize(BTN);

        btnPract.addActionListener(e -> practicar());
        btnEditar.addActionListener(e -> editar());
        btnRank .addActionListener(e -> ranking());
        btnCerrar.addActionListener(e -> dispose());

        botones.add(btnPract); botones.add(btnEditar); botones.add(btnRank); botones.add(btnCerrar);

        add(new JScrollPane(cursosList), BorderLayout.CENTER);
        add(botones, BorderLayout.SOUTH);
    }

    private void cargarCursos(){
        model.clear();
        List<Curso> cursos = cursoDAO.buscarTodos();
        cursos.stream()
              .filter(c -> c.getCreador()==null || c.getCreador().equals(estudianteActual))
              .forEach(model::addElement);
    }

    private Curso sel(){ return cursosList.getSelectedValue(); }

    private void practicar(){
        Curso c = sel();
        if(c == null){info("Selecciona un curso",0); return;}
        new PracticaWindow(c, estudianteActual).setVisible(true);
        dispose();
    }

    private void editar(){
        Curso c = sel();
        if(c == null){info("Selecciona un curso"); return;}
        new CrearCursoWindow(c, estudianteActual).setVisible(true);
        dispose();
    }

    private void ranking(){
        Curso c = sel();
        if(c == null){info("Selecciona un curso"); return;}
        new RankingWindow(c).setVisible(true);
    }

    private void info(String m){ info(m, JOptionPane.WARNING_MESSAGE); }
    private void info(String m,int t){ JOptionPane.showMessageDialog(this,m,"Aviso",t); }
}
