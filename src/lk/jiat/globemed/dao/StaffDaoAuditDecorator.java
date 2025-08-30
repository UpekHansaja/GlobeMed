package lk.jiat.globemed.dao;

import java.time.LocalDateTime;
import java.util.List;
import lk.jiat.globemed.model.AuditLog;
import lk.jiat.globemed.model.Staff;

public class StaffDaoAuditDecorator {

    private final StaffDao delegate;
    private final AuditDao auditDao;
    private final boolean failOnAuditError;

    public StaffDaoAuditDecorator(StaffDao delegate, AuditDao auditDao, boolean failOnAuditError) {
        this.delegate = delegate;
        this.auditDao = auditDao;
        this.failOnAuditError = failOnAuditError;
    }

    public StaffDaoAuditDecorator(StaffDao delegate, AuditDao auditDao) {
        this(delegate, auditDao, false);
    }

    public Staff create(Staff s, String performedBy) {
        Staff created = delegate.create(s);
        try {
            AuditLog log = buildAudit("Staff", created.getId(), "CREATE", performedBy, "Created staff: " + created.getEmail());
            auditDao.create(log);
        } catch (Exception e) {
            System.err.println("Audit write failed after create: " + e.getMessage());
            e.printStackTrace();
            if (failOnAuditError) {
                throw e;
            }
        }
        return created;
    }

    public Staff update(Staff s, String performedBy) {
        Staff updated = delegate.update(s);
        try {
            AuditLog log = buildAudit("Staff", updated.getId(), "UPDATE", performedBy, "Updated staff: " + updated.getEmail());
            auditDao.create(log);
        } catch (Exception e) {
            System.err.println("Audit write failed after update: " + e.getMessage());
            e.printStackTrace();
            if (failOnAuditError) {
                throw e;
            }
        }
        return updated;
    }

    public boolean deleteById(Long id, String performedBy) {
        boolean ok = delegate.deleteById(id);
        if (ok) {
            try {
                AuditLog log = buildAudit("Staff", id, "DELETE", performedBy, "Deleted staff id=" + id);
                auditDao.create(log);
            } catch (Exception e) {
                System.err.println("Audit write failed after delete: " + e.getMessage());
                e.printStackTrace();
                if (failOnAuditError) {
                    throw e;
                }
            }
        }
        return ok;
    }

    public Staff findById(Long id) {
        return delegate.findById(id);
    }

    public Staff findByEmail(String email) {
        return delegate.findByEmail(email);
    }

    public List<Staff> findAll() {
        return delegate.findAll();
    }

    private AuditLog buildAudit(String entityName, Long entityId, String action, String performedBy, String details) {
        AuditLog log = new AuditLog();
        log.setEntityName(entityName);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setPerformedAt(LocalDateTime.now());
        log.setDetails(details);
        return log;
    }
}
