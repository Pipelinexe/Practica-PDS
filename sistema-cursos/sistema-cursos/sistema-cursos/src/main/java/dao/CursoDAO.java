package dao;

import models.Curso;

import javax.persistence.*;
import java.util.List;

/** DAO básico para la entidad {@link Curso}. */
public class CursoDAO {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("flashcards-jpa");

    /* ───────── guardar ───────── */

    public void guardar(Curso c) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        if (c.getId() == null) em.persist(c); else em.merge(c);
        em.getTransaction().commit();
        em.close();
    }

    /* ───────── consultar ───────── */

    /** Devuelve todos los cursos ordenados por fecha de creación descendente. */
    public List<Curso> buscarTodos() {
        EntityManager em = emf.createEntityManager();
        List<Curso> l = em.createQuery("""
                SELECT c FROM Curso c
                ORDER BY c.fechaCreacion DESC
                """, Curso.class).getResultList();
        em.close();
        return l;
    }

    /* ───────── util ───────── */

    /** Llamar al cerrar la aplicación para liberar la conexión H2. */
    public static void cerrarFactory() {
        if (emf.isOpen()) emf.close();
    }
}
