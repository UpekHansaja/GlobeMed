package lk.jiat.globemed.dao;

import java.util.List;
import java.util.Optional;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class PatientDao {

    public Patient create(Patient patient) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(patient); // Hibernate 6/7: persist to create
            tx.commit();
            return patient;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public Patient findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Patient.class, id);
        }
    }

    public List<Patient> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Patient> q = session.createQuery("FROM Patient", Patient.class);
            return q.getResultList();
        }
    }

    public Patient update(Patient patient) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Patient merged = (Patient) session.merge(patient); // returns managed instance
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public boolean deleteById(Long id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Patient p = session.get(Patient.class, id);
            if (p == null) {
                return false;
            }
            tx = session.beginTransaction();
            session.remove(p); // remove managed entity
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    // Additional useful lookup
    public List<Patient> findByDoctor(Long doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.id = :doctorId", Patient.class)
                    .setParameter("doctorId", doctorId)
                    .getResultList();
        }
    }

    public List<Patient> findByLastName(String lastName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Patient> q = session.createQuery("FROM Patient p WHERE p.lastName = :ln", Patient.class);
            q.setParameter("ln", lastName);
            return q.getResultList();
        }
    }

    public Optional<Patient> findOptionalById(Long id) {
        return Optional.ofNullable(findById(id));
    }

}
