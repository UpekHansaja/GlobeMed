package lk.jiat.globemed.dao;

import lk.jiat.globemed.model.AuditLog;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AuditDao {

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
                    System.out.println("AuditLog create");
                }
            }
            throw e;
        }
    }

    public void persistWithSession(Session session, AuditLog log) {
        if (session == null) {
            throw new IllegalArgumentException("session is null");
        }
        session.persist(log);
    }
}
