package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity @Table(name = "cursos")
public class Curso {

    public enum TipoEstrategia { SECUENCIAL, REPETICION }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    /** Fecha de alta del curso. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_creacion", nullable = false,
            columnDefinition = "timestamp default current_timestamp")
    private Date fechaCreacion = new Date();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_estrategia", nullable = false)
    private TipoEstrategia tipoEstrategia = TipoEstrategia.SECUENCIAL;

    @ManyToOne @JoinColumn(name = "creador_id")
    private Estudiante creador;

    @OneToMany(mappedBy = "curso",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.EAGER)
    private List<Flashcard> flashcards = new ArrayList<>();

    protected Curso() { }                         // JPA

    public Curso(String nombre, String descripcion,
                 TipoEstrategia tipo, List<Flashcard> fc,
                 Estudiante creador) {
        this.nombre         = nombre;
        this.descripcion    = descripcion;
        this.tipoEstrategia = tipo == null ? TipoEstrategia.SECUENCIAL : tipo;
        this.creador        = creador;
        fc.forEach(this::addFlashcard);
    }

    /* dominio */
    public void addFlashcard(Flashcard f){ flashcards.add(f); f.setCurso(this); }

    /* getters/setters */
    public Long getId()              { return id; }
    public String getNombre()        { return nombre; }
    public void setNombre(String n)  { this.nombre = n; }
    public String getDescripcion()   { return descripcion; }
    public void setDescripcion(String d){ this.descripcion = d; }

    public Date getFechaCreacion()   { return fechaCreacion; }

    public TipoEstrategia getTipoEstrategia()              { return tipoEstrategia; }
    public void setTipoEstrategia(TipoEstrategia t)        { this.tipoEstrategia = t; }

    public Estudiante getCreador()                         { return creador; }
    public void setCreador(Estudiante e)                   { this.creador = e; }

    public List<Flashcard> getFlashcards()                 { return flashcards; }
}
