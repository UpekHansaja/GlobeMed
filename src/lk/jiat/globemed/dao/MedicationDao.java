package lk.jiat.globemed.dao;

import lk.jiat.globemed.model.Medication;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class MedicationDao {

    public Medication create(Medication medication) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(medication);
            tx.commit();
            return medication;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public Medication findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Medication.class, id);
        }
    }

    public List<Medication> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Medication ORDER BY name", Medication.class).getResultList();
        }
    }

    public List<Medication> findByCategory(String category) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Medication> query = session.createQuery(
                "FROM Medication m WHERE m.category = :category ORDER BY m.name", Medication.class);
            query.setParameter("category", category);
            return query.getResultList();
        }
    }

    public List<Medication> findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Medication> query = session.createQuery(
                "FROM Medication m WHERE LOWER(m.name) LIKE LOWER(:name) ORDER BY m.name", Medication.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        }
    }

    public List<Medication> findLowStock() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Medication m WHERE m.stockQuantity <= m.minimumStock ORDER BY m.name", Medication.class)
                .getResultList();
        }
    }

    public List<Medication> findExpired() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Medication> query = session.createQuery(
                "FROM Medication m WHERE m.expiryDate < :currentDate ORDER BY m.expiryDate", Medication.class);
            query.setParameter("currentDate", LocalDate.now());
            return query.getResultList();
        }
    }

    public List<Medication> findExpiringSoon() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Medication> query = session.createQuery(
                "FROM Medication m WHERE m.expiryDate BETWEEN :currentDate AND :futureDate ORDER BY m.expiryDate", 
                Medication.class);
            query.setParameter("currentDate", LocalDate.now());
            query.setParameter("futureDate", LocalDate.now().plusMonths(3));
            return query.getResultList();
        }
    }

    public List<Medication> findAvailable() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Medication m WHERE m.status = 'Available' AND m.stockQuantity > 0 ORDER BY m.name", 
                Medication.class).getResultList();
        }
    }

    public Medication update(Medication medication) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Medication updated = session.merge(medication);
            tx.commit();
            return updated;
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
            Medication medication = session.get(Medication.class, id);
            if (medication == null) {
                return false;
            }
            tx = session.beginTransaction();
            session.remove(medication);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public List<String> findAllCategories() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "SELECT DISTINCT m.category FROM Medication m ORDER BY m.category", String.class)
                .getResultList();
        }
    }

    public List<String> findAllManufacturers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "SELECT DISTINCT m.manufacturer FROM Medication m ORDER BY m.manufacturer", String.class)
                .getResultList();
        }
    }
}