package models;

import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    // Relaciones
    private List<Curso> cursos;
    
    // Constructor
    public Biblioteca() {
        this.cursos = new ArrayList<>();
    }
} 