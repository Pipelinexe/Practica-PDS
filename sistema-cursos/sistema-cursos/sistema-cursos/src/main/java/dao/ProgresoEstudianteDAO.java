package dao;

import models.Curso;
import models.Estudiante;
import models.ProgresoEstudiante;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/** DAO para gestionar la entidad {@link ProgresoEstudiante}. */
public class ProgresoEstudianteDAO {

    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("flashcards-jpa");

    private final EntityManager em = emf.createEntityManager();

    /* ───── consultas ────────────────────────────────────── */

    public ProgresoEstudiante buscarPorEstudianteYCurso(
            Estudiante est, Curso curso) {

        List<ProgresoEstudiante> l = em.createQuery("""
                SELECT p FROM ProgresoEstudiante p
                WHERE p.estudiante = :est AND p.curso = :curso
                """, ProgresoEstudiante.class)
            .setParameter("est", est)
            .setParameter("curso", curso)
            .getResultList();
        return l.isEmpty() ? null : l.get(0);
    }

    /** Todos los progresos del estudiante (en todos los cursos). */
    public List<ProgresoEstudiante> buscarPorEstudiante(Estudiante est) {
        return em.createQuery("""
                SELECT p FROM ProgresoEstudiante p
                WHERE p.estudiante = :est
                """, ProgresoEstudiante.class)
            .setParameter("est", est)
            .getResultList();
    }

    /** Ranking por curso: lista (Estudiante, % acierto) ordenada desc. */
    public List<Object[]> rankingPorCurso(Curso curso) {
        return em.createQuery("""
                SELECT p.estudiante,
                       (p.aciertos * 1.0 / p.totalRespondidas) * 100
                FROM ProgresoEstudiante p
                WHERE p.curso = :curso AND p.totalRespondidas > 0
                ORDER BY (p.aciertos * 1.0 / p.totalRespondidas) DESC
                """, Object[].class)
            .setParameter("curso", curso)
            .getResultList();
    }

    /* ───── persistencia ─────────────────────────────────── */

    public void guardar(ProgresoEstudiante p) {
        em.getTransaction().begin();
        if (p.getId() == null) em.persist(p); else em.merge(p);
        em.getTransaction().commit();
    }

    public void cerrar() { if (em.isOpen()) em.close(); }
}
