package estrategias;

import models.Flashcard;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Estrategia de aprendizaje "Repetición de errores":
 *   – Ronda inicial secuencial.
 *   – Cada fallo se guarda.
 *   – Cuando termina la ronda inicial, se
 *     repasan únicamente los fallados hasta
 *     acertarlos todos.
 */
public class EstrategiaRepeticion implements EstrategiaAprendizaje {

    /** Cola de trabajo de la ronda actual */
    private final Deque<Flashcard> cola = new ArrayDeque<>();

    /** Lista de flashcards falladas en la ronda actual */
    private final List<Flashcard> pendientes = new ArrayList<>();

    private boolean modoRepeticion = false;

    /* ────────────────────── API ────────────────────── */

    @Override
    public void inicializar(List<Flashcard> lista) {
        cola.clear();
        cola.addAll(lista);           // orden secuencial
        pendientes.clear();
        modoRepeticion = false;
    }

    @Override
    public boolean hayMasFlashcards() {
        return !cola.isEmpty() || !pendientes.isEmpty();
    }

    @Override
    public Flashcard siguienteFlashcard() {
        if (cola.isEmpty() && !pendientes.isEmpty()) {
            // pasar a modo repetición
            cola.addAll(pendientes);
            pendientes.clear();
            modoRepeticion = true;
        }
        return cola.pollFirst();      // puede devolver null si ya no hay nada
    }

    @Override
    public void registrarRespuesta(Flashcard f, boolean correcta) {
        if (!correcta) {
            pendientes.add(f);
        }
    }

    /* ───────── info para la UI ───────── */

    public boolean estaEnModoRepeticion()   { return modoRepeticion; }
    public int     getCantidadPendientes()  { return pendientes.size(); }

	@Override
	public double obtenerProgreso() {
		// TODO Auto-generated method stub
		return 0;
	}
}
