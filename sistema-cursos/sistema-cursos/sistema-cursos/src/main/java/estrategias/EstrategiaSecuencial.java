package estrategias;

import models.Flashcard;
import java.util.List;
import java.util.ArrayList;

public class EstrategiaSecuencial implements EstrategiaAprendizaje {
    private List<Flashcard> flashcards;
    private int indiceActual;
    private int totalRespondidas;
    
    @Override
    public void inicializar(List<Flashcard> flashcards) {
        this.flashcards = new ArrayList<>(flashcards);
        this.indiceActual = 0;
        this.totalRespondidas = 0;
    }
    
    @Override
    public Flashcard siguienteFlashcard() {
        if (!hayMasFlashcards()) {
            return null;
        }
        return flashcards.get(indiceActual);
    }
    
    @Override
    public void registrarRespuesta(Flashcard flashcard, boolean correcta) {
        if (indiceActual < flashcards.size()) {
            indiceActual++;
            totalRespondidas++;
        }
    }
    
    @Override
    public boolean hayMasFlashcards() {
        return indiceActual < flashcards.size();
    }
    
    @Override
    public double obtenerProgreso() {
        if (flashcards == null || flashcards.isEmpty()) {
            return 0.0;
        }
        return (double) totalRespondidas / flashcards.size();
    }
} 