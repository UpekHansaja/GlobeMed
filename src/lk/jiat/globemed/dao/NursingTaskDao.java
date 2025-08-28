package lk.jiat.globemed.dao;

import lk.jiat.globemed.model.NursingTask;
import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;

public class NursingTaskDao {

    public NursingTask create(NursingTask task) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(task);
            tx.commit();
            return task;
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
    }

    public NursingTask findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(NursingTask.class, id);
        }
    }

    public List<NursingTask> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM NursingTask ORDER BY createdAt DESC", NursingTask.class)
                    .getResultList();
        }
    }

    public List<NursingTask> findByAssignedNurse(Staff nurse) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NursingTask> query = session.createQuery(
                "FROM NursingTask t WHERE t.assignedNurse = :nurse ORDER BY t.createdAt DESC", 
                NursingTask.class);
            query.setParameter("nurse", nurse);
            return query.getResultList();
        }
    }

    public List<NursingTask> findByAssignedNurseId(Long nurseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NursingTask> query = session.createQuery(
                "FROM NursingTask t WHERE t.assignedNurse.id = :nurseId ORDER BY t.createdAt DESC", 
                NursingTask.class);
            query.setParameter("nurseId", nurseId);
            return query.getResultList();
        }
    }

    public List<NursingTask> findByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NursingTask> query = session.createQuery(
                "FROM NursingTask t WHERE t.status = :status ORDER BY t.createdAt DESC", 
                NursingTask.class);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    public List<NursingTask> findByPriority(String priority) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NursingTask> query = session.createQuery(
                "FROM NursingTask t WHERE t.priority = :priority ORDER BY t.createdAt DESC", 
                NursingTask.class);
            query.setParameter("priority", priority);
            return query.getResultList();
        }
    }

    public List<NursingTask> findByPatientId(Long patientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NursingTask> query = session.createQuery(
                "FROM NursingTask t WHERE t.patient.id = :patientId ORDER BY t.createdAt DESC", 
                NursingTask.class);
            query.setParameter("patientId", patientId);
            return query.getResultList();
        }
    }

    public List<NursingTask> findPendingTasks() {
        return findByStatus("Pending");
    }

    public List<NursingTask> findInProgressTasks() {
        return findByStatus("In Progress");
    }

    public List<NursingTask> findCompletedTasks() {
        return findByStatus("Completed");
    }

    public List<NursingTask> findOverdueTasks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NursingTask> query = session.createQuery(
                "FROM NursingTask t WHERE t.dueDateTime < :now AND t.status NOT IN ('Completed', 'Cancelled') ORDER BY t.dueDateTime ASC", 
                NursingTask.class);
            query.setParameter("now", LocalDateTime.now());
            return query.getResultList();
        }
    }

    public List<NursingTask> findHighPriorityTasks() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NursingTask> query = session.createQuery(
                "FROM NursingTask t WHERE t.priority IN ('High', 'Critical') AND t.status NOT IN ('Completed', 'Cancelled') ORDER BY t.priority DESC, t.createdAt ASC", 
                NursingTask.class);
            return query.getResultList();
        }
    }

    public List<NursingTask> findTasksByNurseAndStatus(Long nurseId, String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NursingTask> query = session.createQuery(
                "FROM NursingTask t WHERE t.assignedNurse.id = :nurseId AND t.status = :status ORDER BY t.createdAt DESC", 
                NursingTask.class);
            query.setParameter("nurseId", nurseId);
            query.setParameter("status", status);
            return query.getResultList();
        }
    }

    public List<NursingTask> findTasksDueToday(Long nurseId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
            
            Query<NursingTask> query = session.createQuery(
                "FROM NursingTask t WHERE t.assignedNurse.id = :nurseId AND " +
                "t.dueDateTime BETWEEN :startOfDay AND :endOfDay AND " +
                "t.status NOT IN ('Completed', 'Cancelled') ORDER BY t.dueDateTime ASC", 
                NursingTask.class);
            query.setParameter("nurseId", nurseId);
            query.setParameter("startOfDay", startOfDay);
            query.setParameter("endOfDay", endOfDay);
            return query.getResultList();
        }
    }

    public long countTasksByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(t) FROM NursingTask t WHERE t.status = :status", Long.class);
            query.setParameter("status", status);
            return query.getSingleResult();
        }
    }

    public long countTasksByNurseAndStatus(Long nurseId, String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(t) FROM NursingTask t WHERE t.assignedNurse.id = :nurseId AND t.status = :status", Long.class);
            query.setParameter("nurseId", nurseId);
            query.setParameter("status", status);
            return query.getSingleResult();
        }
    }

    public NursingTask update(NursingTask task) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            NursingTask updated = session.merge(task);
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
            NursingTask task = session.get(NursingTask.class, id);
            if (task == null) {
                return false;
            }
            tx = session.beginTransaction();
            session.remove(task);
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