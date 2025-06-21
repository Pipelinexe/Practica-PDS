package models;

import java.util.Date;

public class Inscripcion {
    private Date fechaInicio;
    private String progreso;
    private String estrategiaSeleccionada;
    private Estudiante estudiante;
    private Curso curso;
    
    public Inscripcion(Estudiante estudiante, Curso curso, String estrategia) {
        this.estudiante = estudiante;
        this.curso = curso;
        this.fechaInicio = new Date();
        this.progreso = "0%";
        this.estrategiaSeleccionada = estrategia;
    }
    
    public void actualizarProgreso(String nuevoProgreso) {
        this.progreso = nuevoProgreso;
    }
    
    public Estudiante getEstudiante() {
        return estudiante;
    }
    
    public String getProgreso() {
        return progreso;
    }
    
    public String getEstrategia() {
        return estrategiaSeleccionada;
    }
} 