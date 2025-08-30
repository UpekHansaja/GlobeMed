package lk.jiat.globemed.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lk.jiat.globemed.model.Appointment;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class AppointmentDao {

    public Appointment create(Appointment appointment) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(appointment);
            tx.commit();
            return appointment;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public Appointment findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Appointment.class, id);
        }
    }

    public List<Appointment> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Appointment> q = session.createQuery("FROM Appointment", Appointment.class);
            return q.getResultList();
        }
    }

    public Appointment update(Appointment appointment) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Appointment merged = (Appointment) session.merge(appointment);
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
            Appointment a = session.get(Appointment.class, id);
            if (a == null) {
                return false;
            }
            tx = session.beginTransaction();
            session.remove(a);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public List<Appointment> findByDoctor(Long doctorId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Appointment a WHERE a.doctor.id = :doctorId", Appointment.class)
                    .setParameter("doctorId", doctorId)
                    .getResultList();
        }
    }

    public List<Appointment> findByPatientId(Long patientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Appointment> q = session.createQuery("FROM Appointment a WHERE a.patient.id = :pid", Appointment.class);
            q.setParameter("pid", patientId);
            return q.getResultList();
        }
    }

    public List<Appointment> findByStaffId(Long staffId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Appointment> q = session.createQuery("FROM Appointment a WHERE a.doctor.id = :sid", Appointment.class);
            q.setParameter("sid", staffId);
            return q.getResultList();
        }
    }

    public List<Appointment> findByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Appointment> q = session.createQuery("FROM Appointment a WHERE a.status = :st", Appointment.class);
            q.setParameter("st", status);
            return q.getResultList();
        }
    }

    public List<Appointment> findBetween(LocalDateTime from, LocalDateTime to) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Appointment> q = session.createQuery(
                    "FROM Appointment a WHERE a.appointmentDateTime >= :from AND a.appointmentDateTime <= :to",
                    Appointment.class);
            q.setParameter("from", from);
            q.setParameter("to", to);
            return q.getResultList();
        }
    }

    public Optional<Appointment> findOptionalById(Long id) {
        return Optional.ofNullable(findById(id));
    }
}
