package estrategias;

import models.Flashcard;
import java.util.List;

public interface EstrategiaAprendizaje {
    /**
     * Inicializa la estrategia con una lista de flashcards
     */
    void inicializar(List<Flashcard> flashcards);
    
    /**
     * Devuelve la siguiente flashcard según la estrategia
     */
    Flashcard siguienteFlashcard();
    
    /**
     * Registra el resultado de la respuesta del usuario
     */
    void registrarRespuesta(Flashcard flashcard, boolean correcta);
    
    /**
     * Indica si quedan más flashcards por mostrar
     */
    boolean hayMasFlashcards();
    
    /**
     * Devuelve el progreso actual (0.0 a 1.0)
     */
    double obtenerProgreso();
} 