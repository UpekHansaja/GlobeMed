package lk.jiat.globemed.dao;

import java.util.List;
import lk.jiat.globemed.model.Role;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class RoleDao {

    public Role findById(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(Role.class, id);
        }
    }

    public Role findByName(String name) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Query<Role> q = s.createQuery("FROM Role r WHERE r.name = :n", Role.class);
            q.setParameter("n", name);
            return q.uniqueResultOptional().orElse(null);
        }
    }

    public List<Role> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Role", Role.class).getResultList();
        }
    }
}
