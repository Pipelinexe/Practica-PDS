package models;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entidad que representa a un usuario/alumno del sistema.
 */
@Entity
@Table(name = "estudiantes",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    protected Estudiante() { } // JPA

    public Estudiante(String nombre, String email, String password) {
        this.nombre   = nombre;
        this.email    = email;
        this.password = password;
    }

    public Long   getId()       { return id; }
    public String getNombre()   { return nombre; }
    public String getEmail()    { return email; }
    public String getPassword() { return password; }

    public void setNombre(String n)   { this.nombre = n; }
    public void setEmail(String e)    { this.email  = e; }
    public void setPassword(String p) { this.password = p; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estudiante)) return false;
        Estudiante that = (Estudiante) o;
        return Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    @Override
    public String toString() {
        return nombre;
    }
}
