package lk.jiat.globemed.dao;

import lk.jiat.globemed.model.Staff;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class StaffDao {

    public Staff findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Staff> query = session.createQuery("FROM Staff WHERE email = :email", Staff.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        }
    }
}
