package lk.jiat.globemed.dao;

import java.util.List;
import java.util.Optional;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class StaffDao {

    public Staff create(Staff staff) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(staff);
            tx.commit();
            return staff;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public Staff findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Staff.class, id);
        }
    }

    public Staff findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Staff> q = session.createQuery("FROM Staff s WHERE s.email = :em", Staff.class);
            q.setParameter("em", email);
            return q.uniqueResultOptional().orElse(null);
        }
    }

    public List<Staff> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Staff> q = session.createQuery("FROM Staff", Staff.class);
            return q.getResultList();
        }
    }

    public Staff update(Staff staff) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Staff merged = (Staff) session.merge(staff);
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
            Staff s = session.get(Staff.class, id);
            if (s == null) {
                return false;
            }
            tx = session.beginTransaction();
            session.remove(s);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public Optional<Staff> findOptionalById(Long id) {
        return Optional.ofNullable(findById(id));
    }
}
