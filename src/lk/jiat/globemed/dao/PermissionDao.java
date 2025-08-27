package lk.jiat.globemed.dao;

import lk.jiat.globemed.model.Permission;
import lk.jiat.globemed.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class PermissionDao {

    public Permission findById(Long id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(Permission.class, id);
        }
    }

    public List<Permission> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Permission", Permission.class).getResultList();
        }
    }
}
