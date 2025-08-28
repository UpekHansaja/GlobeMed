package lk.jiat.globemed.dao;

import lk.jiat.globemed.model.PatientVitals;
import lk.jiat.globemed.model.Patient;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class PatientVitalsDao {

    public PatientVitals create(PatientVitals vitals) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(vitals);
            tx.commit();
            return vitals;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public PatientVitals findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(PatientVitals.class, id);
        }
    }

    public List<PatientVitals> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM PatientVitals ORDER BY recordedAt DESC", PatientVitals.class)
                    .getResultList();
        }
    }

    public List<PatientVitals> findByPatient(Patient patient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PatientVitals> query = session.createQuery(
                "FROM PatientVitals v WHERE v.patient = :patient ORDER BY v.recordedAt DESC", 
                PatientVitals.class);
            query.setParameter("patient", patient);
            return query.getResultList();
        }
    }

    public List<PatientVitals> findByPatientId(Long patientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PatientVitals> query = session.createQuery(
                "FROM PatientVitals v WHERE v.patient.id = :patientId ORDER BY v.recordedAt DESC", 
                PatientVitals.class);
            query.setParameter("patientId", patientId);
            return query.getResultList();
        }
    }

    public List<PatientVitals> findByNurseId(Long nurseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PatientVitals> query = session.createQuery(
                "FROM PatientVitals v WHERE v.nurse.id = :nurseId ORDER BY v.recordedAt DESC", 
                PatientVitals.class);
            query.setParameter("nurseId", nurseId);
            return query.getResultList();
        }
    }

    public List<PatientVitals> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PatientVitals> query = session.createQuery(
                "FROM PatientVitals v WHERE v.recordedAt BETWEEN :startDate AND :endDate ORDER BY v.recordedAt DESC", 
                PatientVitals.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        }
    }

    public PatientVitals getLatestVitalsForPatient(Long patientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PatientVitals> query = session.createQuery(
                "FROM PatientVitals v WHERE v.patient.id = :patientId ORDER BY v.recordedAt DESC", 
                PatientVitals.class);
            query.setParameter("patientId", patientId);
            query.setMaxResults(1);
            List<PatientVitals> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        }
    }

    public List<PatientVitals> findAbnormalVitals() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PatientVitals> query = session.createQuery(
                "FROM PatientVitals v WHERE " +
                "(v.temperature > 37.5) OR " +
                "(v.systolicBP > 140 OR v.diastolicBP > 90) OR " +
                "(v.heartRate < 60 OR v.heartRate > 100) OR " +
                "(v.oxygenSaturation < 95) " +
                "ORDER BY v.recordedAt DESC", 
                PatientVitals.class);
            return query.getResultList();
        }
    }

    public PatientVitals update(PatientVitals vitals) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            PatientVitals updated = session.merge(vitals);
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
            PatientVitals vitals = session.get(PatientVitals.class, id);
            if (vitals == null) {
                return false;
            }
            tx = session.beginTransaction();
            session.remove(vitals);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }
}