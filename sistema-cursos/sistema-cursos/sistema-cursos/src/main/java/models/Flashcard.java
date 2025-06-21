package models;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "flashcards")
public class Flashcard {

    /* ────────────────────  Atributos  ──────────────────── */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pregunta;

    /** Respuesta correcta en texto plano */
    @Column(nullable = false, name = "respuesta_correcta")
    private String respuestaCorrecta;

    /** Lista completa de opciones que se mostrarán al usuario
     *  (incluye la correcta; puede estar vacía para pregunta abierta) */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "flashcard_opciones",
        joinColumns = @JoinColumn(name = "flashcard_id"))
    @Column(name = "opcion")
    private List<String> opciones = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoFlashcard tipo;

    @ManyToOne
    @JoinColumn(name = "curso_id")
    private Curso curso;

    /* ────────────────────  Enum  ──────────────────── */

    public enum TipoFlashcard {
        COMPLETAR_HUECO,
        VERDADERO_FALSO,
        PREGUNTA_ABIERTA,
        MULTIOPCION
    }

    /* ────────────────────  Constructores  ──────────────────── */

    /** Requerido por JPA */
    protected Flashcard() {}

    /** Completar hueco */
    public static Flashcard crearCompletarHueco(String fraseCompleta,
                                                String respuesta,
                                                List<String> distractores) {
        Flashcard f = new Flashcard();
        f.tipo = TipoFlashcard.COMPLETAR_HUECO;
        f.pregunta = fraseCompleta.replace(respuesta, "___");
        f.respuestaCorrecta = respuesta;
        f.opciones.add(respuesta);
        f.opciones.addAll(distractores);
        return f;
    }

    /** Verdadero / Falso */
    public static Flashcard crearVerdaderoFalso(String afirmacion,
                                                boolean esVerdadera) {
        Flashcard f = new Flashcard();
        f.tipo = TipoFlashcard.VERDADERO_FALSO;
        f.pregunta = afirmacion;
        f.respuestaCorrecta = esVerdadera ? "Verdadero" : "Falso";
        f.opciones.addAll(List.of("Verdadero", "Falso"));
        return f;
    }

    /** Pregunta abierta */
    public static Flashcard crearPreguntaAbierta(String pregunta,
                                                 String respuesta) {
        Flashcard f = new Flashcard();
        f.tipo = TipoFlashcard.PREGUNTA_ABIERTA;
        f.pregunta = pregunta;
        f.respuestaCorrecta = respuesta;
        return f;                 // lista de opciones vacía
    }

    /** Multi-opción (una correcta + ≥3 distractores) */
    public static Flashcard crearMultiopcion(String pregunta,
                                             String correcta,
                                             List<String> distractores) {
        Flashcard f = new Flashcard();
        f.tipo = TipoFlashcard.MULTIOPCION;
        f.pregunta = pregunta;
        f.respuestaCorrecta = correcta;
        f.opciones.add(correcta);
        f.opciones.addAll(distractores);
        return f;
    }

    /* ────────────────────  Lógica  ──────────────────── */

    /** Devuelve la lista de opciones barajadas (para la GUI) */
    public List<String> getOpcionesBarajadas() {
        List<String> copia = new ArrayList<>(opciones);
        Collections.shuffle(copia);
        return copia;
    }

    /** Verifica la respuesta del usuario */
    public boolean esRespuestaCorrecta(String respuesta) {
        return respuestaCorrecta.equalsIgnoreCase(respuesta.trim());
    }

    /* ────────────────────  Getters / Setters  ──────────────────── */

    public Long getId()                { return id; }
    public String getPregunta()        { return pregunta; }
    public String getRespuestaCorrecta(){ return respuestaCorrecta; }
    public TipoFlashcard getTipo()     { return tipo; }
    public Curso getCurso()            { return curso; }
    public void setCurso(Curso c)      { this.curso = c; }

    /** Todas las opciones (sin barajar) – útil para tests o exportación */
    public List<String> getOpciones()  { return List.copyOf(opciones); }
}
