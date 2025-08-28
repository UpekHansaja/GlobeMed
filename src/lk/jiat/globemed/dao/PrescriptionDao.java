package lk.jiat.globemed.dao;

import lk.jiat.globemed.model.Prescription;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionDao {

    public Prescription create(Prescription prescription) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(prescription);
            tx.commit();
            return prescription;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public Prescription findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Prescription.class, id);
        }
    }

    public List<Prescription> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "FROM Prescription p ORDER BY p.prescribedDate DESC", Prescription.class)
                .getResultList();
        }
    }

    public List<Prescription> findByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery(
                "FROM Prescription p WHERE p.status = :status ORDER BY p.prescribedDate DESC", Prescription.class);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    public List<Prescription> findPending() {
        return findByStatus("Pending");
    }

    public List<Prescription> findByPatient(Patient patient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery(
                "FROM Prescription p WHERE p.patient = :patient ORDER BY p.prescribedDate DESC", Prescription.class);
            query.setParameter("patient", patient);
            return query.getResultList();
        }
    }

    public List<Prescription> findByDoctor(Staff doctor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery(
                "FROM Prescription p WHERE p.doctor = :doctor ORDER BY p.prescribedDate DESC", Prescription.class);
            query.setParameter("doctor", doctor);
            return query.getResultList();
        }
    }

    public List<Prescription> findByPharmacist(Staff pharmacist) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery(
                "FROM Prescription p WHERE p.pharmacist = :pharmacist ORDER BY p.dispensedDate DESC", Prescription.class);
            query.setParameter("pharmacist", pharmacist);
            return query.getResultList();
        }
    }

    public List<Prescription> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery(
                "FROM Prescription p WHERE p.prescribedDate BETWEEN :startDate AND :endDate ORDER BY p.prescribedDate DESC", 
                Prescription.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        }
    }

    public List<Prescription> findRecentPrescriptions(int days) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Prescription> query = session.createQuery(
                "FROM Prescription p WHERE p.prescribedDate >= :cutoffDate ORDER BY p.prescribedDate DESC", 
                Prescription.class);
            query.setParameter("cutoffDate", LocalDateTime.now().minusDays(days));
            return query.getResultList();
        }
    }

    public Prescription update(Prescription prescription) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Prescription updated = session.merge(prescription);
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
            Prescription prescription = session.get(Prescription.class, id);
            if (prescription == null) {
                return false;
            }
            tx = session.beginTransaction();
            session.remove(prescription);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public long countByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Prescription p WHERE p.status = :status", Long.class);
            query.setParameter("status", status);
            return query.getSingleResult();
        }
    }

    public double getTotalRevenueByPharmacist(Staff pharmacist) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Double> query = session.createQuery(
                "SELECT SUM(pi.quantity * pi.medication.unitPrice) FROM PrescriptionItem pi " +
                "WHERE pi.prescription.pharmacist = :pharmacist AND pi.prescription.status = 'Filled'", 
                Double.class);
            query.setParameter("pharmacist", pharmacist);
            Double result = query.getSingleResult();
            return result != null ? result : 0.0;
        }
    }
}