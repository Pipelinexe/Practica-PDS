package models;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity @Table(name = "progresos")
public class ProgresoEstudiante {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) private Estudiante estudiante;
    @ManyToOne(optional = false) private Curso      curso;

    private int totalRespondidas;
    private int aciertos;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "progreso_flashcards",
            joinColumns = @JoinColumn(name = "progreso_id"))
    @Column(name = "flashcard_id")
    private Set<Long> flashcardsCompletadas = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date ultimaActividad;

    protected ProgresoEstudiante() { }

    public ProgresoEstudiante(Estudiante e, Curso c) {
        this.estudiante = e;
        this.curso      = c;
        this.ultimaActividad = new Date();
    }

    /* registrar */
    public void registrarRespuesta(Flashcard f, boolean correcta) {
        totalRespondidas++;
        if (correcta) aciertos++;
        flashcardsCompletadas.add(f.getId());
        ultimaActividad = new Date();
    }

    /* reiniciar */
    public void reset() {
        totalRespondidas = 0;
        aciertos         = 0;
        flashcardsCompletadas.clear();
        ultimaActividad  = new Date();
    }

    /* métricas */
    public double obtenerPorcentajeProgreso() {
        if (curso.getFlashcards().isEmpty()) return 0.0;
        return flashcardsCompletadas.size() / (double) curso.getFlashcards().size();
    }

    /* getters */
    public Long getId() { return id; }
    public int  getTotalRespondidas() { return totalRespondidas; }
    public int  getAciertos() { return aciertos; }
    public Set<Long> getFlashcardsCompletadas() { return flashcardsCompletadas; }
    public Curso getCurso() { return curso; }
}
