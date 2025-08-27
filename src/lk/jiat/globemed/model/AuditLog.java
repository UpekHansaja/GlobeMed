package lk.jiat.globemed.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;
    private Long entityId;
    private String action; // CREATE, UPDATE, DELETE
    private String performedBy; // staff email or id
    private LocalDateTime performedAt;

    @Column(length = 2000)
    private String details;

    // getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String e) {
        this.entityName = e;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long id) {
        this.entityId = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String a) {
        this.action = a;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String p) {
        this.performedBy = p;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime t) {
        this.performedAt = t;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String d) {
        this.details = d;
    }
}
