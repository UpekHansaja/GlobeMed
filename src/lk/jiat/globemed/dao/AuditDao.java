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
                tx.rollback();
            }
            throw e;
        }
    }
}
