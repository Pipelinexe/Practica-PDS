package models;

import java.util.ArrayList;
import java.util.List;

public class Administrador {
    // Relaciones
    private List<Pregunta> preguntas;
    private List<String> tiposDePreguntas;
    
    // Constructor
    public Administrador() {
        this.preguntas = new ArrayList<>();
        this.tiposDePreguntas = new ArrayList<>();
    }
    
    // Método para crear tipo de pregunta
    public boolean agregarTipoPregunta(String tipoPregunta) {
        if (tipoPregunta == null || tipoPregunta.trim().isEmpty()) {
            return false;
        }
        
        if (tiposDePreguntas.contains(tipoPregunta)) {
            return false;
        }
        
        return tiposDePreguntas.add(tipoPregunta);
    }
    
    public List<String> obtenerTiposDePreguntas() {
        return new ArrayList<>(tiposDePreguntas);
    }
} 