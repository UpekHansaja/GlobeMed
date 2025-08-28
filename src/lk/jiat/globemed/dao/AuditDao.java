package lk.jiat.globemed.dao;

import lk.jiat.globemed.model.AuditLog;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AuditDao {

    // keep original single-session create for backward compatibility
    public AuditLog create(AuditLog log) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.persist(log);
            tx.commit();
            return log;
        } catch (Exception e) {
            if (tx != null) {
                try {
                    tx.rollback();
                } catch (Exception ex) {
                    /* ignore */ }
            }
            throw e;
        }
    }

    /**
     * Persist an AuditLog using an existing Session. This method does NOT
     * start/commit/rollback transactions; transaction management must be
     * handled by the caller.
     */
    public void persistWithSession(Session session, AuditLog log) {
        if (session == null) {
            throw new IllegalArgumentException("session is null");
        }
        session.persist(log);
    }
}
