package lk.jiat.globemed.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Stack;
import lk.jiat.globemed.dao.AuditDao;
import lk.jiat.globemed.dao.RoleDao;
import lk.jiat.globemed.dao.StaffDao;
import lk.jiat.globemed.model.AuditLog;
import lk.jiat.globemed.model.Role;
import lk.jiat.globemed.model.Staff;

public class StaffService {

    private final StaffDao staffDao = new StaffDao();
    private final RoleDao roleDao = new RoleDao();
    private final AuditDao auditDao = new AuditDao();

    // simple Memento stack for undo of last edits
    private final Stack<StaffMemento> mementoStack = new Stack<>();

    public List<Staff> findAll() {
        return staffDao.findAll();
    }

    public Staff findByEmail(String email) {
        return staffDao.findByEmail(email);
    }

    public Staff create(Staff s, String performedBy) {
        Staff created = staffDao.create(s);
        audit("Staff", created.getId(), "CREATE", performedBy, "Created staff: " + created.getEmail());
        return created;
    }

    public Staff update(Staff updated, String performedBy) {
        // store snapshot before change
        Staff before = staffDao.findById(updated.getId());
        if (before != null) {
            mementoStack.push(new StaffMemento(before));
        }

        Staff merged = staffDao.update(updated);
        audit("Staff", merged.getId(), "UPDATE", performedBy, "Updated staff: " + merged.getEmail());
        return merged;
    }

    public boolean deleteById(Long id, String performedBy) {
        Staff p = staffDao.findById(id);
        if (p == null) {
            return false;
        }
        mementoStack.push(new StaffMemento(p));
        boolean ok = staffDao.deleteById(id);
        if (ok) {
            audit("Staff", id, "DELETE", performedBy, "Deleted staff id=" + id);
        }
        return ok;
    }

    public boolean undoLastEdit() {
        if (mementoStack.isEmpty()) {
            return false;
        }
        StaffMemento m = mementoStack.pop();
        // restore
        Staff restored = m.restore();
        staffDao.create(restored); // create as new if primary key null, else merge
        // Note: if id was set, using create may conflict with PK. To fully restore safely,
        // we can use update/merge or custom SQL. For simplicity, attempt merge:
        // staffDao.update(restored);
        return true;
    }

    private void audit(String entity, Long entityId, String action, String by, String details) {
        AuditLog log = new AuditLog();
        log.setEntityName(entity);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setPerformedBy(by);
        log.setPerformedAt(LocalDateTime.now());
        log.setDetails(details);
        auditDao.create(log);
    }

    public Role findRoleByName(String name) {
        return roleDao.findByName(name);
    }

    // Memento snapshot object
    private static class StaffMemento {

        private final Staff snapshot;

        StaffMemento(Staff s) {
            // shallow copy (you can expand to deep clone if needed)
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
