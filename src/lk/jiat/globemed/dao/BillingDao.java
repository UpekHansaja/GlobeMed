package lk.jiat.globemed.dao;

import java.util.List;
import java.util.Optional;
import lk.jiat.globemed.model.BillingRecord;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class BillingDao {

    public BillingRecord create(BillingRecord billing) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(billing);
            tx.commit();
            return billing;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public BillingRecord findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(BillingRecord.class, id);
        }
    }

    public List<BillingRecord> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BillingRecord> q = session.createQuery("FROM BillingRecord", BillingRecord.class);
            return q.getResultList();
        }
    }

    public BillingRecord update(BillingRecord billing) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            BillingRecord merged = (BillingRecord) session.merge(billing);
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
            BillingRecord b = session.get(BillingRecord.class, id);
            if (b == null) {
                return false;
            }
            tx = session.beginTransaction();
            session.remove(b);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public List<BillingRecord> findByPatientId(Long patientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BillingRecord> q = session.createQuery("FROM BillingRecord b WHERE b.patient.id = :pid", BillingRecord.class);
            q.setParameter("pid", patientId);
            return q.getResultList();
        }
    }

    public List<BillingRecord> findByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BillingRecord> q = session.createQuery("FROM BillingRecord b WHERE b.status = :st", BillingRecord.class);
            q.setParameter("st", status);
            return q.getResultList();
        }
    }

    public Optional<BillingRecord> findOptionalById(Long id) {
        return Optional.ofNullable(findById(id));
    }
}
