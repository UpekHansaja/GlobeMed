package lk.jiat.globemed.service;

import java.util.List;
import java.util.Stack;
import lk.jiat.globemed.dao.AuditDao;
import lk.jiat.globemed.dao.RoleDao;
import lk.jiat.globemed.dao.StaffDao;
import lk.jiat.globemed.dao.StaffDaoAuditDecorator;
import lk.jiat.globemed.model.Role;
import lk.jiat.globemed.model.Staff;

public class StaffService {

    private final StaffDao rawDao = new StaffDao();
    private final StaffDaoAuditDecorator staffDao = new StaffDaoAuditDecorator(rawDao, new AuditDao());

    private final RoleDao roleDao = new RoleDao();

    private final Stack<StaffMemento> mementoStack = new Stack<>();

    public List<Staff> findAll() {
        return staffDao.findAll();
    }

    public Staff findByEmail(String email) {
        return staffDao.findByEmail(email);
    }

    public Staff create(Staff s, String performedBy) {

        Staff created = staffDao.create(s, performedBy);
        return created;
    }

    public Staff update(Staff updated, String performedBy) {

        Staff before = rawDao.findById(updated.getId());
        if (before != null) {
            mementoStack.push(new StaffMemento(before));
        }
        Staff merged = staffDao.update(updated, performedBy);
        return merged;
    }

    public boolean deleteById(Long id, String performedBy) {
        Staff before = rawDao.findById(id);
        if (before != null) {
            mementoStack.push(new StaffMemento(before));
        }
        boolean ok = staffDao.deleteById(id, performedBy);
        return ok;
    }

    public boolean undoLastEdit() {
        if (mementoStack.isEmpty()) {
            return false;
        }
        StaffMemento m = mementoStack.pop();
        Staff restored = m.restore();

        staffDao.update(restored, "system");
        return true;
    }

    public Role findRoleByName(String name) {
        return roleDao.findByName(name);
    }

    private static class StaffMemento {

        private final Staff snapshot;

        StaffMemento(Staff s) {
            Staff copy = new Staff();
            copy.setId(s.getId());
            copy.setName(s.getName());
            copy.setEmail(s.getEmail());
            copy.setPassword(s.getPassword());
            copy.setRole(s.getRole());
            this.snapshot = copy;
        }

        public Staff restore() {
            return snapshot;
        }
    }
}
